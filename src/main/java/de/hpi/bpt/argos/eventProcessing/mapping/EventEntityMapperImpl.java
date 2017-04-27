package de.hpi.bpt.argos.eventProcessing.mapping;


import de.hpi.bpt.argos.common.ObservableImpl;
import de.hpi.bpt.argos.eventProcessing.EventReceiver;
import de.hpi.bpt.argos.storage.PersistenceAdapterImpl;
import de.hpi.bpt.argos.storage.dataModel.attribute.Attribute;
import de.hpi.bpt.argos.storage.dataModel.attribute.type.TypeAttribute;
import de.hpi.bpt.argos.storage.dataModel.entity.Entity;
import de.hpi.bpt.argos.storage.dataModel.event.Event;
import de.hpi.bpt.argos.storage.dataModel.event.type.EventType;
import de.hpi.bpt.argos.storage.dataModel.mapping.EventEntityMapping;
import de.hpi.bpt.argos.storage.dataModel.mapping.MappingCondition;
import de.hpi.bpt.argos.util.LoggerUtilImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class EventEntityMapperImpl extends ObservableImpl<EventMappingObserver> implements EventEntityMapper {
	private static final Logger logger = LoggerFactory.getLogger(EventEntityMapperImpl.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setup(EventReceiver eventReceiver) {
		eventReceiver.subscribe(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onEventCreated(EventEntityMappingStatus mappingStatus,
			EventType eventType, List<TypeAttribute> eventTypeAttributes, Event event, List<Attribute> eventAttributes) {

		if (mappingStatus.isMapped()) {
			notifyObservers((EventMappingObserver observer) -> observer.onEventMapped(event, mappingStatus.getEventOwner()));
			return;
		}

		List<EventEntityMapping> mappings = PersistenceAdapterImpl.getInstance().getEventEntityMappingsForEventType(eventType.getId());

		for (EventEntityMapping mapping : mappings) {
			List<TypeAttribute> entityTypeAttributes = PersistenceAdapterImpl.getInstance().getTypeAttributes(mapping.getEntityTypeId());
			List<MappingCondition> mappingConditions = PersistenceAdapterImpl.getInstance().getMappingConditions(mapping.getId());

			StringBuilder sqlQuery = new StringBuilder();
			sqlQuery.append("FROM EntityImpl entity WHERE ");
			boolean firstCondition = true;

			try {
				for (MappingCondition mappingCondition : mappingConditions) {
					String sqlCondition = String.format("entity.%1$s = %2$s ",
							getAttributeName(mappingCondition.getEntityTypeAttributeId(), entityTypeAttributes),
							getAttributeValue(mappingCondition.getEventTypeAttributeId(), eventAttributes));

					if (!firstCondition) {
						sqlQuery.append("AND ");
					} else {
						firstCondition = false;
					}

					sqlQuery.append(sqlCondition);
				}

				Entity owner = PersistenceAdapterImpl.getInstance().getMappingEntity(sqlQuery.toString());
				event.setEntityId(owner.getId());
				mappingStatus.setEventOwner(owner);

				notifyObservers((EventMappingObserver observer) -> observer.onEventMapped(event, owner));

				return;

			} catch (EventEntityMappingException e) {
				LoggerUtilImpl.getInstance().error(logger, e.getMessage(), e);
			}
		}
	}

	/**
	 * This method returns the name of a specific typeAttribute.
	 * @param attributeId - the unique identifier of the typeAttribute
	 * @param typeAttributes - a list of all typeAttributes
	 * @return - the name of the typeAttribute
	 * @throws EventEntityMappingException - thrown when the typeAttribute could not be found
	 */
	private String getAttributeName(long attributeId, List<TypeAttribute> typeAttributes) throws EventEntityMappingException {
		for (TypeAttribute typeAttribute : typeAttributes) {
			if (typeAttribute.getId() == attributeId) {
				return typeAttribute.getName();
			}
		}

		throw new EventEntityMappingException("type attribute not found");
	}

	/**
	 * This method returns the value of a specific attribute.
	 * @param typeAttributeId - the unique identifier of the typeAttribute
	 * @param attributes - a list of all attributes
	 * @return - the value of the attribute
	 * @throws EventEntityMappingException - thrown when the attribute could not be found
	 */
	private String getAttributeValue(long typeAttributeId, List<Attribute> attributes) throws EventEntityMappingException {
		for (Attribute attribute : attributes) {
			if (attribute.getTypeAttributeId() == typeAttributeId) {
				return attribute.getValue();
			}
		}

		throw new EventEntityMappingException("attribute not found");
	}
}
