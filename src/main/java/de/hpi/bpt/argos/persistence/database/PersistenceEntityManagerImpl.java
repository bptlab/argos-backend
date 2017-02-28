package de.hpi.bpt.argos.persistence.database;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.hpi.bpt.argos.api.event.EventEndpoint;
import de.hpi.bpt.argos.api.eventType.EventTypeEndpoint;
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
import de.hpi.bpt.argos.persistence.model.event.statusUpdate.StatusUpdateEventType;
import de.hpi.bpt.argos.persistence.model.event.type.EventType;
import de.hpi.bpt.argos.persistence.model.event.type.EventTypeImpl;
import de.hpi.bpt.argos.persistence.model.product.Product;
import de.hpi.bpt.argos.persistence.model.product.ProductFamily;
import de.hpi.bpt.argos.persistence.model.product.ProductFamilyImpl;
import de.hpi.bpt.argos.persistence.model.product.ProductImpl;
import de.hpi.bpt.argos.persistence.model.product.ProductState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
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
	public Product getProduct(int externalProductId) {
		return databaseConnection.getProduct(externalProductId);
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
	public EventType getEventType(String eventTypeName) {
		return databaseConnection.getEventType(eventTypeName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Event createEvent(long eventTypeId, String requestBody) {

		EventType eventType = getEventType(eventTypeId);

		if (eventType == null) {
			return null;
		}

		Event event = new EventImpl();
		JsonObject json = jsonParser.parse(requestBody).getAsJsonObject();

		event.setEventData(getEventData(eventType, json));
		event.setEventType(eventType);

		Product product = getProduct(getProductFamilyIdentification(event.getEventData()),
				getProductIdentification(event.getEventData()));

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
	public Event createStatusUpdateEvent(long productId, ProductState newProductState, String requestBody) {

		Product product = getProduct(productId);
		EventType statusUpdateEventType = getEventType(EventType.getStatusUpdateEventTypeName());

		if (product == null || statusUpdateEventType == null) {
			return null;
		}

		Event statusUpdateEvent = new EventImpl();
		statusUpdateEvent.setEventType(statusUpdateEventType);
		statusUpdateEvent.setProduct(product);

		JsonObject jsonBody = jsonParser.parse(requestBody).getAsJsonObject();
		statusUpdateEvent.setEventData(getStatusUpdateEventData(product, newProductState, jsonBody));

		product.setState(newProductState);

		databaseConnection.saveEntities(product, statusUpdateEvent);
		updateEntity(PushNotificationType.UPDATE, product, ProductEndpoint.getProductUri(product.getId()));
		updateEntity(PushNotificationType.CREATE, statusUpdateEvent, EventEndpoint.getEventUri(statusUpdateEvent.getId()));

		return statusUpdateEvent;
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
		// but keep the id and the query
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
			product.setState(ProductState.UNDEFINED);

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
	 * @param eventData - the event data of the requested event
	 * @return - the product identification
	 */
	protected int getProductIdentification(List<EventData> eventData) {
		for (EventData data : eventData) {
			if (data.getEventAttribute().getName().equals(EventType.getProductIdentificationAttributeName())) {
				return Integer.parseInt(data.getValue());
			}
		}

		return -1;
	}

	/**
	 * This method returns the product family identification for an event type and a list of event data.
	 * @param eventData - the event data of the requested event
	 * @return - the product family identification
	 */
	protected String getProductFamilyIdentification(List<EventData> eventData) {
		for (EventData data : eventData) {
			if (data.getEventAttribute().getName().equals(EventType.getProductFamilyIdentificationAttributeName())) {
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

		// default event types should not be editable nor deletable
		eventType.setEditable(false);
		eventType.setDeletable(false);

		// but they must be registered, since they do not use INSERT INTO in their queries
		eventType.setShouldBeRegistered(true);

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

		EventQuery query = new EventQueryImpl();
		eventType.setEventQuery(query);

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

		eventType.setTimestampAttribute(eventType.getAttribute(timeStampAttributeName));
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

		if (!isValid(eventType.getAttribute(EventType.getProductIdentificationAttributeName()))) {
			return false;
		}

		if (!isValid(eventType.getAttribute(EventType.getProductFamilyIdentificationAttributeName()))) {
			return false;
		}

		if (!isValid(eventType.getTimestampAttribute())) {
			return false;
		}

		return true;
	}

	/**
	 * This method returns a list of event data for the json representation of a status update event.
	 * @param product - the product which status is updated
	 * @param jsonEvent - the json representation of the event
	 * @param newProductState - the updated product state
	 * @return - a list of event data
	 */
	protected List<EventData> getStatusUpdateEventData(Product product, ProductState newProductState, JsonObject jsonEvent) {

		EventType updateStatusEventType = getEventType(EventType.getStatusUpdateEventTypeName());

		EventData oldStatus = new EventDataImpl();
		oldStatus.setEventAttribute(updateStatusEventType.getAttribute(StatusUpdateEventType.getOldStatusAttributeName()));
		oldStatus.setValue(product.getState().toString());

		EventData newStatus = new EventDataImpl();
		newStatus.setEventAttribute(updateStatusEventType.getAttribute(StatusUpdateEventType.getNewStatusAttributeName()));
		newStatus.setValue(newProductState.toString());

		EventData timestamp = new EventDataImpl();
		timestamp.setEventAttribute(updateStatusEventType.getTimestampAttribute());
		timestamp.setValue(jsonEvent.get(StatusUpdateEventType.getTimestampAttributeName()).getAsString());

		return new ArrayList<>(Arrays.asList(oldStatus, newStatus, timestamp));
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
