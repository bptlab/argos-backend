package de.hpi.bpt.argos.eventHandling;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.hpi.bpt.argos.api.response.ResponseFactory;
import de.hpi.bpt.argos.api.response.ResponseFactoryImpl;
import de.hpi.bpt.argos.persistence.database.PersistenceEntityManager;
import de.hpi.bpt.argos.persistence.model.event.EventSubscriptionQuery;
import de.hpi.bpt.argos.persistence.model.event.EventSubscriptionQueryImpl;
import de.hpi.bpt.argos.persistence.model.event.attribute.EventAttribute;
import de.hpi.bpt.argos.persistence.model.event.attribute.EventAttributeImpl;
import de.hpi.bpt.argos.persistence.model.event.data.EventDataType;
import de.hpi.bpt.argos.persistence.model.event.type.EventType;
import de.hpi.bpt.argos.persistence.model.event.type.EventTypeImpl;
import org.hibernate.annotations.common.util.impl.LoggerFactory;
import org.jboss.logging.Logger;
import spark.Service;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class EventPlatformRestEndpointImpl implements EventPlatformRestEndpoint {
	protected static final Logger logger = LoggerFactory.logger(EventPlatformRestEndpointImpl.class);
	protected static final JsonParser jsonParser = new JsonParser();

	protected static final String EVENT_TYPES_DIRECTORY = "/event_types";
	protected static final String JSON_NAME_ATTRIBUTE = "name";
	protected static final String JSON_TIMESTAMP_ATTRIBUTE = "timestamp";
	protected static final String JSON_ATTRIBUTES_ATTRIBUTE = "attributes";

	protected PersistenceEntityManager entityManager;

	protected EventSubscriber eventSubscriber;
	protected ResponseFactory responseFactory;
	protected EventReceiver eventReceiver;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setup(PersistenceEntityManager entityManager, Service sparkService) {
		this.entityManager = entityManager;
		responseFactory = new ResponseFactoryImpl();
		responseFactory.setup(entityManager);

		loadDefaultEventTypes();

		eventSubscriber = new EventSubscriberImpl();
		eventSubscriber.setup(entityManager);
		eventSubscriber.setupEventPlatform();

		eventReceiver = new EventReceiverImpl();
		eventReceiver.setup(responseFactory, entityManager, sparkService);
	}

	/**
	 * This method loads all default event types from the disk.
	 */
	protected void loadDefaultEventTypes() {
		List<EventType> newEventTypes = new ArrayList<>();

		try {
			// since the path is delivered as URI, it is represented as a HTML string. Thus we need to replace %20 with file system spaces.
			File eventTypesDirectory = new File(this.getClass().getResource(EVENT_TYPES_DIRECTORY).getPath().replaceAll("%20", " "));

			for(File eventType : eventTypesDirectory.listFiles()) {
				if (!eventType.getName().endsWith(".json")) {
					continue;
				}

				EventType newEventType = loadDefaultEventType(eventType);

				if (newEventType != null) {
					newEventTypes.add(newEventType);
				}
			}

		} catch (NullPointerException e) {
			logger.error("cannot find directory for default event types", e);
		}

		createNewEventTypes(newEventTypes);
	}

	/**
	 * This method loads the content of a specific file and parses this into an event type.
	 * @param eventTypeFile - the file to load
	 * @return - the new event type
	 */
	protected EventType loadDefaultEventType(File eventTypeFile) {
		try {
			String fileContent = new String(Files.readAllBytes(Paths.get(eventTypeFile.toURI())), StandardCharsets.UTF_8);

			JsonObject jsonEventType = jsonParser.parse(fileContent).getAsJsonObject();
			return parseDefaultEventType(jsonEventType);

		} catch (Exception e) {
			logger.error("cannot load default event type '" + eventTypeFile.getName() + "'.", e);
			return null;
		}
	}

	/**
	 * This method parses a given json object to a new event type.
	 * @param jsonEventType - the json object which should be parsed
	 * @return - the new event type
	 */
	protected EventType parseDefaultEventType(JsonObject jsonEventType) {
		EventType eventType = createSimpleEventType(jsonEventType);

		if (eventType == null) {
			logger.error("cannot parse json into new event type. '" + jsonEventType.getAsString() + "'");
			return null;
		}

		parseEventTypeAttributes(eventType, jsonEventType);
		return eventType;
	}

	/**
	 * This method creates all event types, which are not already existing in the database.
	 * @param eventTypes - the event types to create
	 */
	protected void createNewEventTypes(List<EventType> eventTypes) {
		List<EventType> existingEventTypes = entityManager.getEventTypes();

		for (EventType existingType : existingEventTypes) {

			for (int i = 0; i < eventTypes.size(); i++) {
				if (existingType.getName().equals(eventTypes.get(i).getName())) {
					eventTypes.remove(i);
					break;
				}
			}
		}

		for (EventType newEventType : eventTypes) {
			logger.info("new event type created: " + newEventType.getName());
			entityManager.updateEntity(newEventType);
		}
	}

	/**
	 * This method creates a new event attribute.
	 * @param name - the name of the attribute
	 * @param type - the type of the attribute
	 * @return - the new event attribute
	 */
	protected EventAttribute createEventAttribute(String name, EventDataType type) {
		EventAttribute attribute = new EventAttributeImpl();
		attribute.setName(name);
		attribute.setType(type);

		return attribute;
	}

	/**
	 * This method attaches a new event attribute to an existing event type.
	 * @param eventType - the type which should get expanded
	 * @param attributeName - the name of the new attribute
	 * @param attributeType - the name of the attribute type
	 */
	protected void attachEventAttribute(EventType eventType, String attributeName, String attributeType) {
		EventAttribute attribute = null;

		for (EventDataType type : EventDataType.values()) {
			if (type.name().equals(attributeType)) {
				attribute = createEventAttribute(attributeName, type);
				break;
			}
		}

		if (attribute != null) {
			eventType.getAttributes().add(attribute);
		}
	}

	/**
	 * This method parses and attaches event type attributes from a json representation of an event type.
	 * @param eventType - the event type to attach the attributes to
	 * @param jsonEventType - the json representation of the event type
	 */
	protected void parseEventTypeAttributes(EventType eventType, JsonObject jsonEventType) {
		String timeStampAttributeName = "";

		for (Map.Entry<String, JsonElement> entry : jsonEventType.entrySet()) {
			if (entry.getKey().equals(JSON_ATTRIBUTES_ATTRIBUTE)) {
				for (Map.Entry<String, JsonElement> attribute : entry.getValue().getAsJsonObject().entrySet()) {
					attachEventAttribute(eventType, attribute.getKey(), attribute.getValue().getAsString());
				}

			} else if (entry.getKey().equals(JSON_TIMESTAMP_ATTRIBUTE)) {
				timeStampAttributeName = entry.getValue().getAsString();
				eventType.getAttributes().add(createEventAttribute(entry.getValue().getAsString(), EventDataType.DATE));
			}
		}

		for (EventAttribute attribute : eventType.getAttributes()) {

			if (attribute.getName().equals(timeStampAttributeName)) {
				eventType.setTimestampAttribute(attribute);
			} else if (attribute.getName().equals(EventType.getProductIdentificationAttributeName())) {
				eventType.setProductIdentificationAttribute(attribute);
			} else if (attribute.getName().equals(EventType.getProductFamilyIdentificationAttributeName())) {
				eventType.setProductFamilyIdentificationAttribute(attribute);
			}
		}
	}

	/**
	 * This method creates a new simple event type.
	 * @param name - the name of the event type
	 * @return - the new event type
	 */
	protected EventType createSimpleEventType(String name) {
		EventType eventType = new EventTypeImpl();
		eventType.setName(name);

		EventSubscriptionQuery subscriptionQuery = new EventSubscriptionQueryImpl();
		subscriptionQuery.setQueryString(String.format("SELECT * FROM %1$s", name));

		eventType.setEventSubscriptionQuery(subscriptionQuery);

		return eventType;
	}

	/**
	 * This method creates a new simple event type.
	 * @param jsonEventType - the json representation of the event type
	 * @return - the new event type
	 */
	protected EventType createSimpleEventType(JsonObject jsonEventType) {
		for(Map.Entry<String, JsonElement> entry : jsonEventType.entrySet()) {
			if (entry.getKey().equals(JSON_NAME_ATTRIBUTE)) {
				return createSimpleEventType(entry.getValue().getAsString());
			}
		}

		return null;
	}
}
