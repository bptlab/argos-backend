package de.hpi.bpt.argos.storage.dataModel.event.type;

import de.hpi.bpt.argos.common.EventPlatformFeedback;
import de.hpi.bpt.argos.core.Argos;
import de.hpi.bpt.argos.core.ArgosNotRunningException;
import de.hpi.bpt.argos.eventProcessing.EventCreationObserver;
import de.hpi.bpt.argos.eventProcessing.creation.EventFactoryImpl;
import de.hpi.bpt.argos.eventProcessing.mapping.EventEntityMappingStatus;
import de.hpi.bpt.argos.storage.PersistenceAdapterImpl;
import de.hpi.bpt.argos.storage.dataModel.attribute.Attribute;
import de.hpi.bpt.argos.storage.dataModel.attribute.type.TypeAttribute;
import de.hpi.bpt.argos.storage.dataModel.attribute.type.TypeAttributeImpl;
import de.hpi.bpt.argos.storage.dataModel.entity.Entity;
import de.hpi.bpt.argos.storage.dataModel.event.Event;
import de.hpi.bpt.argos.storage.dataModel.event.query.EventQuery;
import de.hpi.bpt.argos.storage.dataModel.event.query.EventQueryImpl;
import de.hpi.bpt.argos.util.LoggerUtilImpl;
import de.hpi.bpt.argos.util.PairImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * {@inheritDoc}
 * This is a special implementation for indicating that an entity has changed its status.
 */
public final class StatusUpdatedEventType extends EventTypeImpl {
	private static final Logger logger = LoggerFactory.getLogger(StatusUpdatedEventType.class);

	public static final String NAME = "StatusUpdatedEventType";

	private static final String TIMESTAMP_ATTRIBUTE_NAME = "Timestamp";
	private static final String OLD_STATUS_ATTRIBUTE_NAME = "OldStatus";
	private static final String NEW_STATUS_ATTRIBUTE_NAME = "NewStatus";
	private static final String CAUSE_EVENT_ID_ATTRIBUTE_NAME = "CauseEventId";
	private static final String CAUSE_EVENT_TYPE_ID_ATTRIBUTE_NAME = "CauseEventTypeId";
	private static final String ENTITY_ID_ATTRIBUTE_NAME = "EntityId";

	/**
	 * This constructor hides the default public one to implement the singleton pattern.
	 */
	private StatusUpdatedEventType() {

	}

	/**
	 * This method sets up the statusUpdatedEventType.
	 * @param argos - the argos instance
	 * @return - the statusUpdatedEventType
	 */
	public static EventType setup(Argos argos) {
		EventType instance = getInstance();

		if (instance != null) {
			return instance;
		}

		instance = create();
		registerCustomMapping(argos);
		return instance;
	}

	/**
	 * This method returns the statusUpdatedEventType.
	 * @return - the statusUpdatedEventType
	 */
	public static EventType getInstance() {
		return PersistenceAdapterImpl.getInstance().getEventType(NAME);
	}

	/**
	 * This method sends a statusUpdatedEvent to the eventProcessingPlatform.
	 * @param entity - the entity, which status was updated
	 * @param oldStatus - the old status of the given entity
	 * @param newStatus - the new status of the given entity
	 * @param statusUpdateTrigger - the event, which caused the status update
	 * @return - the feedback of the eventProcessingPlatform
	 */
	public static EventPlatformFeedback postEvent(Entity entity, String oldStatus, String newStatus, Event statusUpdateTrigger) {

		EventPlatformFeedback feedback = EventFactoryImpl.getInstance().postEvent(getInstance(), TIMESTAMP_ATTRIBUTE_NAME,
				new PairImpl<>(OLD_STATUS_ATTRIBUTE_NAME, oldStatus),
				new PairImpl<>(NEW_STATUS_ATTRIBUTE_NAME, newStatus),
				new PairImpl<>(CAUSE_EVENT_ID_ATTRIBUTE_NAME, statusUpdateTrigger.getId()),
				new PairImpl<>(CAUSE_EVENT_TYPE_ID_ATTRIBUTE_NAME, statusUpdateTrigger.getTypeId()),
				new PairImpl<>(ENTITY_ID_ATTRIBUTE_NAME, entity.getId()));

		logger.info(String.format("sending status update event: entity: '%1$s' status: '%2$s' -> '%3$s' : response '%4$s'",
				entity.getName(),
				oldStatus,
				newStatus,
				feedback.getResponseText()));

		return feedback;
	}

	/**
	 * This method creates the statusUpdatedEventType in the database
	 * @return - the newly created statusUpdateEventType
	 */
	private static EventType create() {
		EventType statusUpdatedEventType = new EventTypeImpl();
		statusUpdatedEventType.setName(NAME);
		statusUpdatedEventType.setDeletable(false);
		statusUpdatedEventType.setShouldBeRegistered(true);

		PersistenceAdapterImpl.getInstance().saveArtifacts(statusUpdatedEventType);

		EventQuery statusUpdatedEventQuery = new EventQueryImpl();
		statusUpdatedEventQuery.setTypeId(statusUpdatedEventType.getId());
		statusUpdatedEventQuery.setDescription("default query");
		statusUpdatedEventQuery.setQuery("SELECT * FROM " + NAME);

		PersistenceAdapterImpl.getInstance().saveArtifacts(statusUpdatedEventQuery);

		List<TypeAttribute> typeAttributes = new ArrayList<>();
		TypeAttribute timestampAttribute = createTypeAttribute(TIMESTAMP_ATTRIBUTE_NAME, statusUpdatedEventType);
		typeAttributes.add(timestampAttribute);
		typeAttributes.add(createTypeAttribute(OLD_STATUS_ATTRIBUTE_NAME, statusUpdatedEventType));
		typeAttributes.add(createTypeAttribute(NEW_STATUS_ATTRIBUTE_NAME, statusUpdatedEventType));
		typeAttributes.add(createTypeAttribute(CAUSE_EVENT_ID_ATTRIBUTE_NAME, statusUpdatedEventType));
		typeAttributes.add(createTypeAttribute(CAUSE_EVENT_TYPE_ID_ATTRIBUTE_NAME, statusUpdatedEventType));
		typeAttributes.add(createTypeAttribute(ENTITY_ID_ATTRIBUTE_NAME, statusUpdatedEventType));

		PersistenceAdapterImpl.getInstance().saveArtifacts(typeAttributes.toArray(new TypeAttribute[typeAttributes.size()]));

		statusUpdatedEventType.setTimeStampAttributeId(timestampAttribute.getId());
		PersistenceAdapterImpl.getInstance().saveArtifacts(statusUpdatedEventType);

		return statusUpdatedEventType;
	}

	/**
	 * This method returns a new typeAttribute for a given eventType.
	 * @param name - the name of the typeAttribute to create
	 * @param eventType - the eventType this attribute is for
	 * @return - the newly created typeAttribute
	 */
	private static TypeAttribute createTypeAttribute(String name, EventType eventType) {
		TypeAttribute typeAttribute = new TypeAttributeImpl();
		typeAttribute.setName(name);
		typeAttribute.setTypeId(eventType.getId());
		return typeAttribute;
	}

	/**
	 * This method registers a new eventEntityMapping in the given argos instance.
	 * @param argos - the argos instance to use for mapping
	 */
	private static void registerCustomMapping(Argos argos) {
		EventCreationObserver customMapping = new EventCreationObserver() {
			@Override
			public void onEventCreated(EventEntityMappingStatus mappingStatus,
									   EventType eventType,
									   List<TypeAttribute> eventTypeAttributes,
									   Event event,
									   List<Attribute> eventAttributes) {

				if (mappingStatus.isMapped() || eventType.getId() != getInstance().getId()) {
					return;
				}

				long entityId = getAsLong(getAttribute(ENTITY_ID_ATTRIBUTE_NAME, eventTypeAttributes, eventAttributes));

				if (entityId == 0) {
					return;
				}

				Attribute newStatusAttribute = getAttribute(NEW_STATUS_ATTRIBUTE_NAME, eventTypeAttributes, eventAttributes);

				if (newStatusAttribute == null) {
					return;
				}

				Entity entity = PersistenceAdapterImpl.getInstance().getEntity(entityId);
				mappingStatus.setEventOwner(entity);
				mappingStatus.getStatusUpdateStatus().setNewStatus(newStatusAttribute.getValue());

			}
		};

		try {
			argos.addEventEntityMapper(customMapping);
		} catch (ArgosNotRunningException e) {
			LoggerUtilImpl.getInstance().error(logger, "cannot register custom mapping for statusUpdatedEventType", e);
		}
	}

	/**
	 * This method searches for a specific attribute, identified by the name of the typeAttribute.
	 * @param attributeName - the name of the searched attribute
	 * @param typeAttributes - a list of all typeAttributes
	 * @param attributes - a list of all attributes
	 * @return - the attribute of null
	 */
	private static Attribute getAttribute(String attributeName, List<TypeAttribute> typeAttributes, List<Attribute> attributes) {
		for (TypeAttribute typeAttribute : typeAttributes) {
			if (!typeAttribute.getName().equals(attributeName)) {
				continue;
			}

			for (Attribute attribute : attributes) {
				if (attribute.getTypeAttributeId() == typeAttribute.getId()) {
					return attribute;
				}
			}
		}

		return null;
	}

	/**
	 * This method tries to convert the value of a given attribute to long.
	 * @param attribute - the attribute which value should be converted
	 * @return - the converted attribute value
	 */
	private static long getAsLong(Attribute attribute) {
		if (attribute == null) {
			return 0;
		}

		try {
			return Long.parseLong(attribute.getValue());
		} catch (Exception e) {
			LoggerUtilImpl.getInstance().error(logger, "cannot get long from event attribute", e);
			return 0;
		}
	}
}
