package de.hpi.bpt.argos.persistence.database;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.hpi.bpt.argos.api.event.EventEndpoint;
import de.hpi.bpt.argos.api.eventTypes.EventTypeEndpoint;
import de.hpi.bpt.argos.api.product.ProductEndpoint;
import de.hpi.bpt.argos.api.productFamily.ProductFamilyEndpoint;
import de.hpi.bpt.argos.notifications.PushNotificationType;
import de.hpi.bpt.argos.persistence.model.event.Event;
import de.hpi.bpt.argos.persistence.model.event.EventImpl;
import de.hpi.bpt.argos.persistence.model.event.EventQuery;
import de.hpi.bpt.argos.persistence.model.event.EventQueryImpl;
import de.hpi.bpt.argos.persistence.model.event.attribute.EventAttribute;
import de.hpi.bpt.argos.persistence.model.event.attribute.EventAttributeImpl;
import de.hpi.bpt.argos.persistence.model.event.data.EventData;
import de.hpi.bpt.argos.persistence.model.event.data.EventDataImpl;
import de.hpi.bpt.argos.persistence.model.event.data.EventDataType;
import de.hpi.bpt.argos.persistence.model.event.type.EventType;
import de.hpi.bpt.argos.persistence.model.event.type.EventTypeImpl;
import de.hpi.bpt.argos.persistence.model.product.Product;
import de.hpi.bpt.argos.persistence.model.product.ProductFamily;
import de.hpi.bpt.argos.persistence.model.product.ProductFamilyImpl;
import de.hpi.bpt.argos.persistence.model.product.ProductImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class PersistenceEntityManagerImpl implements PersistenceEntityManager {
	protected static final Logger logger = LoggerFactory.getLogger(PersistenceEntityManagerImpl.class);
	protected static final JsonParser jsonParser = new JsonParser();

	protected static final String JSON_NAME_ATTRIBUTE = "name";
	protected static final String JSON_TIMESTAMP_ATTRIBUTE = "timestamp";
	protected static final String JSON_ATTRIBUTES_ATTRIBUTE = "attributes";

	protected DatabaseConnection databaseConnection;
	protected List<PersistenceEntityManagerEventReceiver> eventReceivers;

	/**
	 * This constructor initializes all member with default values.
	 */
	public PersistenceEntityManagerImpl() {
		eventReceivers = new ArrayList<>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean setup() {
		databaseConnection = new DatabaseConnectionImpl();
		return databaseConnection.setup();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void subscribe(PersistenceEntityManagerEventReceiver eventReceiver) {
		eventReceivers.add(eventReceiver);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void unsubscribe(PersistenceEntityManagerEventReceiver eventReceiver) {
		eventReceivers.remove(eventReceiver);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateEntity(PersistenceEntity entity) {
		databaseConnection.saveEntities(entity);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateEntity(PersistenceEntity entity, String fetchUri) {
		updateEntity(entity);
		updateEntity(PushNotificationType.UPDATE, entity, fetchUri);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteEntity(PersistenceEntity entity) {
		databaseConnection.deleteEntities(entity);
		updateEntity(PushNotificationType.DELETE, entity, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProductFamily> getProductFamilies() {
		return databaseConnection.getProductFamilies();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProductFamily getProductFamily(long productFamilyId) {
		return databaseConnection.getProductFamily(productFamilyId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Product getProduct(long productId) {
		return databaseConnection.getProduct(productId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<EventType, Integer> getEventTypes(long productId) {
		return databaseConnection.getEventTypes(productId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Event> getEvents(long productId, long eventTypeId, int indexFrom, int indexTo) {
		return databaseConnection.getEvents(productId, eventTypeId, indexFrom, indexTo);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventType getEventType(long eventTypeId) {
		return databaseConnection.getEventType(eventTypeId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Product getProduct(int productOrderNumber) {
		return databaseConnection.getProduct(productOrderNumber);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<EventType> getEventTypes() {
		return databaseConnection.getEventTypes();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Event getEvent(long eventId) {
		return databaseConnection.getEvent(eventId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<EventQuery> getEventQueries() {
		return databaseConnection.getEventQueries();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Event createEvent(EventType eventType, String jsonEvent) {
		Event event = new EventImpl();
		JsonObject json = jsonParser.parse(jsonEvent).getAsJsonObject();

		event.setEventData(getEventData(eventType, json));
		event.setEventType(eventType);

		Product product = getProduct(getProductFamilyIdentification(eventType, event.getEventData()),
				getProductIdentification(eventType, event.getEventData()));

		event.setProduct(product);

		product.incrementNumberOfEvents(1);
		databaseConnection.saveEntities(product, event);
		updateEntity(PushNotificationType.UPDATE, product, ProductEndpoint.getProductUri(product.getId()));
		updateEntity(PushNotificationType.CREATE, event, EventEndpoint.getEventUri(event.getId()));

		return event;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventType createSimpleEventType(JsonObject jsonEventType) {
		return createEventType(jsonEventType, true, this::createSimpleEventTypeFromJson);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventType createEventType(JsonObject jsonEventType) {
		return createEventType(jsonEventType, false, this::createEventTypeFromJson);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventType updateEventType(JsonObject jsonEventType, long eventTypeId) {
		EventType eventType = getEventType(eventTypeId);

		if (eventType == null) {
			return null;
		}

		// update name and attributes
		EventType updatedEventType = createEventType(jsonEventType);
		// but keep the id and the subscription query
		updatedEventType.setId(eventTypeId);
		updatedEventType.setEventQuery(eventType.getEventQuery());

		databaseConnection.saveEntities(updatedEventType);
		updateEntity(PushNotificationType.UPDATE, updatedEventType, EventTypeEndpoint.getEventTypeUri(eventTypeId));

		return updatedEventType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Product getProduct(ProductFamily productFamily, int productOrderNumber) {
		Product product = databaseConnection.getProduct(productOrderNumber);

		if (product == null) {
			product = new ProductImpl();
			product.setOrderNumber(productOrderNumber);
			product.setProductFamily(productFamily);

			productFamily.getProducts().add(product);
			databaseConnection.saveEntities(productFamily, product);
			updateEntity(PushNotificationType.CREATE, product, ProductEndpoint.getProductUri(product.getId()));
		}

		return product;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Product getProduct(String productFamilyName, int productIdentifier) {
		ProductFamily productFamily = getProductFamily(productFamilyName);

		return getProduct(productFamily, productIdentifier);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProductFamily getProductFamily(String productFamilyName) {
		ProductFamily productFamily = databaseConnection.getProductFamily(productFamilyName);

		if (productFamily == null) {
			productFamily = new ProductFamilyImpl();
			productFamily.setName(productFamilyName);

			databaseConnection.saveEntities(productFamily);
			updateEntity(PushNotificationType.CREATE, productFamily, ProductFamilyEndpoint.getProductFamilyUri(productFamily.getId()));
		}

		return productFamily;
	}

	/**
	 * This method returns a list of event data for the json representation of an event.
	 * @param eventType - the type of the event
	 * @param jsonEvent - the json representation of the event
	 * @return - a list of event data
	 */
	protected List<EventData> getEventData(EventType eventType, JsonObject jsonEvent) {
		List<EventData> eventDataList = new ArrayList<>();

		for (Map.Entry<String, JsonElement> property : jsonEvent.entrySet()) {
			EventAttribute attribute = getAttributeForEventDataMember(eventType, property.getKey());

			if (attribute == null) {
				continue;
			}

			EventData data = new EventDataImpl();
			data.setEventAttribute(attribute);
			data.setValue(property.getValue().getAsString());
			eventDataList.add(data);
		}

		return eventDataList;
	}

	/**
	 * This method returns the corresponding event attribute for an attribute name within an event type.
	 * @param eventType - the event type
	 * @param memberName - the attribute name
	 * @return - the event attribute
	 */
	protected EventAttribute getAttributeForEventDataMember(EventType eventType, String memberName) {
		for (EventAttribute attribute : eventType.getAttributes()) {
			if (attribute.getName().equals(memberName)) {
				return attribute;
			}
		}

		return null;
	}

	/**
	 * This method returns the product identification (which should be it's orderNumber by default) for an event type and a list of event data.
	 * @param eventType - the event type of the requested event
	 * @param eventData - the event data of the requested event
	 * @return - the product identification
	 */
	protected int getProductIdentification(EventType eventType, List<EventData> eventData) {
		for (EventData data : eventData) {
			if (data.getEventAttribute().getName().equals(eventType.getProductIdentificationAttribute().getName())) {
				return Integer.parseInt(data.getValue());
			}
		}

		return -1;
	}

	/**
	 * This method returns the product family identification for an event type and a list of event data.
	 * @param eventType - the event type of the requested event
	 * @param eventData - the event data of the requested event
	 * @return - the product family identification
	 */
	protected String getProductFamilyIdentification(EventType eventType, List<EventData> eventData) {
		for (EventData data : eventData) {
			if (data.getEventAttribute().getName().equals(eventType.getProductFamilyIdentificationAttribute().getName())) {
				return data.getValue();
			}
		}

		return "";
	}

	/**
	 * This method notifies the event receivers about entity changes.
	 * @param type - the type of change
	 * @param entity - the entity that changed
	 * @param fetchUrl - the url where the updated/created can be fetched
	 */
	protected void updateEntity(PushNotificationType type, PersistenceEntity entity, String fetchUrl) {
		for (PersistenceEntityManagerEventReceiver eventReceiver : eventReceivers) {
			eventReceiver.onEntityModified(type, entity, fetchUrl);
		}
	}

	/**
	 * This method creates a new simple event type.
	 * @param name - the name of the event type
	 * @return - the new event type
	 */
	protected EventType createSimpleEventTypeFromName(String name) {
		EventType eventType = createEventTypeFromName(name);
		eventType.getEventQuery().setQueryString(String.format("SELECT * FROM %1$s", name));
		return eventType;
	}

	/**
	 * This method creates a new simple event type.
	 * @param jsonEventType - the json representation of the event type
	 * @return - the new event type
	 */
	protected EventType createSimpleEventTypeFromJson(JsonObject jsonEventType) {
		for (Map.Entry<String, JsonElement> entry : jsonEventType.entrySet()) {
			if (entry.getKey().equals(JSON_NAME_ATTRIBUTE)) {
				return createSimpleEventTypeFromName(entry.getValue().getAsString());
			}
		}

		return null;
	}

	/**
	 * This method creates a new event type.
	 * @param name - the name of the event type
	 * @return - the new event type
	 */
	protected EventType createEventTypeFromName(String name) {
		EventType eventType = new EventTypeImpl();
		eventType.setName(name);

		EventQuery subscriptionQuery = new EventQueryImpl();
		eventType.setEventQuery(subscriptionQuery);

		return eventType;
	}

	/**
	 * This method creates a new event type.
	 * @param jsonEventType - the json representation of the event type
	 * @return - the new event type
	 */
	protected EventType createEventTypeFromJson(JsonObject jsonEventType) {
		for (Map.Entry<String, JsonElement> entry : jsonEventType.entrySet()) {
			if (entry.getKey().equals(JSON_NAME_ATTRIBUTE)) {
				return createEventTypeFromName(entry.getValue().getAsString());
			}
		}

		return null;
	}

	/**
	 * This method parses and attaches event type attributes from a json representation of an event type.
	 * @param eventType - the event type to attach the attributes to
	 * @param jsonEventType - the json representation of the event type
	 */
	protected void parseEventTypeAttributes(EventType eventType, JsonObject jsonEventType) {
		String timeStampAttributeName = "";

		if (!eventType.getAttributes().isEmpty()) {
			return;
		}

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
	 * This method creates or updates an event type, based on its json representation.
	 * @param jsonEventType - the json representation of the event type
	 * @param notifyClients - indicates whether to notify clients about the changes
	 * @param eventTypeCreation - the function to create a new event type from
	 * @return - the new event type
	 */
	protected EventType createEventType(JsonObject jsonEventType, boolean notifyClients, Function<JsonObject, EventType> eventTypeCreation) {

		List<EventType> eventTypes = getEventTypes();
		EventType eventType = eventTypeCreation.apply(jsonEventType);

		if (eventType == null) {
			logger.error("cannot parse json into new event type. '" + jsonEventType.getAsString() + "'");
			return null;
		}

		for (EventType existingEventType : eventTypes) {
			if (existingEventType.getName().equals(eventType.getName())) {

				return null;
			}
		}

		parseEventTypeAttributes(eventType, jsonEventType);

		if (!isValid(eventType)) {
			logger.error("invalid event type '" + jsonEventType.toString() + "'.");
			return null;
		}

		databaseConnection.saveEntities(eventType);

		if (notifyClients) {
			updateEntity(PushNotificationType.CREATE, eventType, EventTypeEndpoint.getEventTypeUri(eventType.getId()));
		}

		return eventType;
	}

	/**
	 * This method checks whether an event type is valid.
	 * @param eventType - the event type to check
	 * @return - true if event type is valid
	 */
	protected boolean isValid(EventType eventType) {
		if (eventType.getName() == null || eventType.getName().length() == 0) {
			return false;
		}

		if (!isValid(eventType.getProductIdentificationAttribute())) {
			return false;
		}

		if (!isValid(eventType.getProductFamilyIdentificationAttribute())) {
			return false;
		}

		if (!isValid(eventType.getTimestampAttribute())) {
			return false;
		}

		return true;
	}

	/**
	 * This method checks whether an event attribute is valid.
	 * @param attribute - the event attribute to check
	 * @return - true if event attribute is valid
	 */
	protected boolean isValid(EventAttribute attribute) {
		return attribute != null && attribute.getName().length() > 0;
	}
}
