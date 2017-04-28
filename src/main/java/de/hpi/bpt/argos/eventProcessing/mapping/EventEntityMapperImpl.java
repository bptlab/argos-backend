package de.hpi.bpt.argos.eventProcessing.mapping;


import de.hpi.bpt.argos.common.Observable;
import de.hpi.bpt.argos.common.ObservableImpl;
import de.hpi.bpt.argos.eventProcessing.EventCreationObserver;
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
	public void setup(Observable<EventCreationObserver> eventReceiver) {
		eventReceiver.subscribe(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onEventCreated(EventEntityMappingStatus mappingStatus,
			EventType eventType, List<TypeAttribute> eventTypeAttributes, Event event, List<Attribute> eventAttributes) {

		if (mappingStatus.isMapped()) {
			notifyObservers((EventMappingObserver observer) ->
					observer.onEventMapped(event, mappingStatus.getEventOwner(), mappingStatus.getUsedMapping()));
			return;
		}

		List<EventEntityMapping> mappings = PersistenceAdapterImpl.getInstance().getEventEntityMappingsForEventType(eventType.getId());

		for (EventEntityMapping mapping : mappings) {
			List<MappingCondition> mappingConditions = PersistenceAdapterImpl.getInstance().getMappingConditions(mapping.getId());
			String sqlQuery = buildMappingQuery(mappingConditions, eventAttributes);
			Entity owner = PersistenceAdapterImpl.getInstance().getMappingEntity(sqlQuery);

			if (owner == null) {
				continue;
			}

			event.setEntityId(owner.getId());
			mappingStatus.setEventOwner(owner, mapping);

			notifyObservers((EventMappingObserver observer) -> observer.onEventMapped(event, owner, mapping));
			return;
		}
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

	/**
	 * This method builds a mapping sql query.
	 * @param mappingConditions - the list of all mapping conditions to consider in the query
	 * @param eventAttributes - the list of all attribute of the received event
	 * @return - the mapping sql query, or an empty string if something went wrong
	 */
	private String buildMappingQuery(List<MappingCondition> mappingConditions, List<Attribute> eventAttributes) {
		if (mappingConditions.isEmpty()) {
			return "";
		}

		try {
			StringBuilder sqlWhere = new StringBuilder();
			sqlWhere.append("WHERE");

			sqlWhere.append(String.format(" (attribute.TypeAttributeId = %1$d AND attribute.Value = '%2$s')",
					mappingConditions.get(0).getEntityTypeAttributeId(),
					getAttributeValue(mappingConditions.get(0).getEventTypeAttributeId(), eventAttributes)));

			for (int i = 1; i < mappingConditions.size(); i++) {
				sqlWhere.append(String.format(" OR (attribute.TypeAttributeId = %1$d AND attribute.Value = '%2$s')",
						mappingConditions.get(i).getEntityTypeAttributeId(),
						getAttributeValue(mappingConditions.get(i).getEventTypeAttributeId(), eventAttributes)));
			}

			return String.format(
					"SELECT OwnerId "
							+ "FROM ( "
							+ "SELECT OwnerId, COUNT(*) AS AttributeCount "
							+ "FROM AttributeImpl attribute " + "%1$s "
							+ "GROUP BY attribute.OwnerId "
							+ "HAVING AttributeCount = %2$d ) AS MappingTable",
					sqlWhere.toString(),
					mappingConditions.size()
			);

		} catch (EventEntityMappingException e) {
			LoggerUtilImpl.getInstance().error(logger, e.getMessage(), e);
			return "";
		}
	}
}
