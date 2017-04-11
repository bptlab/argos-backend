package de.hpi.bpt.argos.persistence.database;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.hpi.bpt.argos.api.event.EventEndpoint;
import de.hpi.bpt.argos.api.eventType.EventTypeEndpoint;
import de.hpi.bpt.argos.api.product.ProductEndpoint;
import de.hpi.bpt.argos.api.productConfiguration.ProductConfigurationEndPoint;
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
import de.hpi.bpt.argos.persistence.model.parsing.DataFile;
import de.hpi.bpt.argos.persistence.model.parsing.DataFileImpl;
import de.hpi.bpt.argos.persistence.model.product.Product;
import de.hpi.bpt.argos.persistence.model.product.ProductConfiguration;
import de.hpi.bpt.argos.persistence.model.product.ProductConfigurationImpl;
import de.hpi.bpt.argos.persistence.model.product.ProductFamily;
import de.hpi.bpt.argos.persistence.model.product.ProductFamilyImpl;
import de.hpi.bpt.argos.persistence.model.product.ProductImpl;
import de.hpi.bpt.argos.persistence.model.product.ProductState;
import de.hpi.bpt.argos.persistence.model.product.error.ErrorCause;
import de.hpi.bpt.argos.persistence.model.product.error.ErrorType;
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
	public boolean updateEntity(PersistenceEntity entity) {
		return databaseConnection.saveEntities(entity);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean updateEntity(PersistenceEntity entity, String fetchUri) {
		if (!updateEntity(entity)) {
			return false;
		}
		updateEntity(PushNotificationType.UPDATE, entity, fetchUri);
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean deleteEntity(PersistenceEntity entity) {
		if (!databaseConnection.deleteEntities(entity)) {
			return false;
		}
		updateEntity(PushNotificationType.DELETE, entity, "");
		return true;
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
	public ProductConfiguration getProductConfiguration(long productConfigurationId) {
		return databaseConnection.getProductConfiguration(productConfigurationId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<EventType, Integer> getEventTypesForProduct(long productId) {
		return databaseConnection.getEventTypesForProduct(productId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<EventType, Integer> getEventTypesForProductConfiguration(long productConfigurationId) {
		return databaseConnection.getEventTypesForProductConfiguration(productConfigurationId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Event> getEventsForProduct(long productId, long eventTypeId, int indexFrom, int indexTo) {
		return databaseConnection.getEventsForProduct(productId, eventTypeId, indexFrom, indexTo);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Event> getEventsForProductConfiguration(long productConfigurationId, long eventTypeId, int indexFrom, int indexTo) {
		return databaseConnection.getEventsForProductConfiguration(productConfigurationId, eventTypeId, indexFrom, indexTo);
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
	public Product getProductByExternalId(long externalProductId) {
		return databaseConnection.getProductByExternalId(externalProductId);
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
	 * This method returns a data file with the specified path and, if it did not exist, creates it in the database.
	 * @param path - the file to look for
	 * @return - a data file
	 */
	@Override
	public DataFile getDataFile(String path) {
		DataFile file = databaseConnection.getDataFile(path);

		if (file == null) {
			file = new DataFileImpl();
			file.setPath(path);
			updateEntity(file);
		}

		return file;
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

		if (product == null) {
			logger.error(String.format("can not find product for event '%1$s'", requestBody));
			return null;
		}

		long productConfigurationId;
		ProductConfiguration configuration;

		try {
		    productConfigurationId = product.getProductConfiguration(
                    getCodingPlugIdentifier(event.getEventData()), getCodingPlugSoftwareVersion(event.getEventData()))
                    .getId();
            configuration = databaseConnection.getProductConfiguration(productConfigurationId);
        } catch (NullPointerException e) {
		    logger.warn("No configurations for product");
		    logger.trace(e.getMessage());
		    configuration = new ProductConfigurationImpl();
		    configuration.setProduct(product);
		    product.addProductConfiguration(configuration);
		    if (!databaseConnection.saveEntities(product)) {
		        return null;
            }
        }

		ErrorType errorType = configuration.getErrorType(getCauseIdentifier(event.getEventData()));
		if (errorType != null) {
			ErrorCause cause = errorType.getErrorCause(getErrorDescription(event.getEventData()));

			if (cause != null) {
				cause.incrementErrorOccurrences(1);
				if (!databaseConnection.saveEntities(cause)) {
				   return null;
                }
			}
		}

		event.setProductConfiguration(configuration);

		configuration.incrementNumberOfEvents(1);
		if (!databaseConnection.saveEntities(configuration, event)) {
			return null;
		}
		updateEntity(PushNotificationType.UPDATE, product, ProductEndpoint.getProductUri(product.getId()));
		updateEntity(PushNotificationType.UPDATE, configuration, ProductConfigurationEndPoint.getProductConfigurationUri(configuration.getId()));
		updateEntity(PushNotificationType.CREATE, event, EventEndpoint.getEventUri(event.getId()));

		return event;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Event createStatusUpdateEvent(long productConfigurationId, ProductState newProductState, String requestBody) {

		ProductConfiguration configuration = getProductConfiguration(productConfigurationId);
		EventType statusUpdateEventType = getEventType(EventType.getStatusUpdateEventTypeName());

		if (configuration == null || statusUpdateEventType == null) {
			return null;
		}

		Event statusUpdateEvent = new EventImpl();
		statusUpdateEvent.setEventType(statusUpdateEventType);
		statusUpdateEvent.setProductConfiguration(configuration);

		JsonObject jsonBody = jsonParser.parse(requestBody).getAsJsonObject();
		statusUpdateEvent.setEventData(getStatusUpdateEventData(configuration, newProductState, statusUpdateEventType, jsonBody));

		configuration.setState(newProductState);

		if (!databaseConnection.saveEntities(configuration, statusUpdateEvent)) {
			return null;
		}

		updateEntity(PushNotificationType.UPDATE, configuration.getProduct(), ProductEndpoint.getProductUri(configuration.getProduct().getId()));
		updateEntity(PushNotificationType.UPDATE, configuration, ProductConfigurationEndPoint.getProductConfigurationUri(configuration.getId()));
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

		if (!databaseConnection.saveEntities(updatedEventType)) {
			return null;
		}
		updateEntity(PushNotificationType.UPDATE, updatedEventType, EventTypeEndpoint.getEventTypeUri(eventTypeId));

		return updatedEventType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Product getProduct(ProductFamily productFamily, long externalProductId) {
		Product product = databaseConnection.getProductByExternalId(externalProductId);

		if (product == null) {
			product = new ProductImpl();
			product.setOrderNumber(externalProductId);
			product.setProductFamily(productFamily);

			productFamily.getProducts().add(product);
			if (!databaseConnection.saveEntities(productFamily, product)) {
				return null;
			}
			updateEntity(PushNotificationType.CREATE, product, ProductEndpoint.getProductUri(product.getId()));
		}

		return product;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Product getProduct(String productFamilyName, long externalProductId) {
		ProductFamily productFamily = getProductFamily(productFamilyName);

		return getProduct(productFamily, externalProductId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProductConfiguration getProductConfiguration(Product product, int codingPlugId, float codingPlugSoftwareVersion) {
	    ProductConfiguration configuration = product.getProductConfiguration(codingPlugId, codingPlugSoftwareVersion);

		if (configuration == null) {
			configuration = new ProductConfigurationImpl();
			configuration.setProduct(product);
			configuration.setCodingPlugId(codingPlugId);
			configuration.addCodingPlugSoftwareVersion(codingPlugSoftwareVersion);
			product.addProductConfiguration(configuration);

			if (!databaseConnection.saveEntities(product, configuration)) {
				return null;
			}
			updateEntity(PushNotificationType.UPDATE, product, ProductEndpoint.getProductUri(product.getId()));
			updateEntity(PushNotificationType.CREATE, configuration, ProductConfigurationEndPoint.getProductConfigurationUri(configuration.getId()));
		}

		return configuration;
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

			if (!databaseConnection.saveEntities(productFamily)) {
				return null;
			}
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
	protected long getProductIdentification(List<EventData> eventData) {
		for (EventData data : eventData) {
			if (data.getEventAttribute().getName().equals(EventType.getProductIdentificationAttributeName())) {
				try {
					return Long.parseLong(data.getValue());
				} catch (Exception e) {
					logger.error(String.format("can not cast '%1$s' to external product id (long)", data.getValue()));
					logTrace(e);
					return -1;
				}
			}
		}

		return -1;
	}

	/**
	 * This method returns the product family identification for a list of event data.
	 * @param eventData - the event data of the requested event
	 * @return - the product family identification
	 */
	protected String getProductFamilyIdentification(List<EventData> eventData) {
		for (EventData data : eventData) {
			if (data.getEventAttribute().getName().equalsIgnoreCase(EventType.getProductFamilyIdentificationAttributeName())) {
				return data.getValue();
			}
		}

		return "";
	}

	/**
	 * This method returns the coding plug id for a list of event data.
	 * @param eventData - the event data for the requested event
	 * @return - the coding plug id
	 */
	protected int getCodingPlugIdentifier(List<EventData> eventData) {
		for (EventData data : eventData) {
			if (data.getEventAttribute().getName().equalsIgnoreCase(EventType.getCodingPlugIdentificationAttributeName())) {
				try {
					return Integer.parseInt(data.getValue());
				} catch (Exception e) {
					logger.error(String.format("can not cast '%1$s' to coding plug id (int)", data.getValue()));
					logTrace(e);
					return -1;
				}
			}
		}

		return -1;
	}

	/**
	 * This method returns the coding plug software version for a list of event data.
	 * @param eventData - the event data for the requested event
	 * @return - the coding plug software version
	 */
	protected float getCodingPlugSoftwareVersion(List<EventData> eventData) {
		for (EventData data : eventData) {
			if (data.getEventAttribute().getName().equalsIgnoreCase(EventType.getCodingPlugSoftwareVersionAttributeName())) {
				try {
					return Float.parseFloat(data.getValue());
				} catch (Exception e) {
					logger.error(String.format("can not cast '%1$s' to coding plug software version (float)", data.getValue()));
					logTrace(e);
					return -1.0f;
				}
			}
		}

		return -1.0f;
	}

	/**
	 * This method returns the cause id for a list of event data.
	 * @param eventData - the event data for the requested event
	 * @return - the cause id
	 */
	protected int getCauseIdentifier(List<EventData> eventData) {
		for (EventData data : eventData) {
			if (data.getEventAttribute().getName().equalsIgnoreCase(EventType.getCauseIdentifierAttributeName())) {
				try {
					return Integer.parseInt(data.getValue());
				} catch (Exception e) {
					logger.error(String.format("can not cast '%1$s' to cause id (int)", data.getValue()));
					logTrace(e);
					return -1;
				}
			}
		}

		return -1;
	}

	/**
	 * This method returns the error description for a list of event data.
	 * @param eventData - the event data for the requested event
	 * @return - the error description
	 */
	protected String getErrorDescription(List<EventData> eventData) {
		for (EventData data : eventData) {
			if (data.getEventAttribute().getName().equalsIgnoreCase(EventType.getErrorCauseDescriptionAttributeName())) {
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

		if (!databaseConnection.saveEntities(eventType)) {
			return null;
		}

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

		if (!isValid(eventType.getAttribute(EventType.getProductIdentificationAttributeName()),
				EventType.getProductIdentificationAttributeDataType())) {
			return false;
		}

		if (!isValid(eventType.getAttribute(EventType.getProductFamilyIdentificationAttributeName()),
				EventType.getProductFamilyIdentificationAttributeDataType())) {
			return false;
		}

		if (!isValid(eventType.getAttribute(EventType.getCodingPlugIdentificationAttributeName()),
				EventType.getCodingPlugIdentificationAttributeDataType())) {
			return false;
		}

		if (!isValid(eventType.getAttribute(EventType.getCodingPlugSoftwareVersionAttributeName()),
				EventType.getCodingPlugSoftwareVersionAttributeDataType())) {
			return false;
		}

		if (!isValid(eventType.getTimestampAttribute(), EventDataType.DATE)) {
			return false;
		}

		return true;
	}

	/**
	 * This method returns a list of event data for the json representation of a status update event.
	 * @param productConfiguration - the product configuration which status is updated
	 * @param newProductState - the updated product state
	 * @param updateStatusEventType - the update event status event type
	 * @param jsonEvent - the json representation of the event
	 * @return - a list of event data
	 */
	protected List<EventData> getStatusUpdateEventData(ProductConfiguration productConfiguration, ProductState newProductState,
													   EventType updateStatusEventType, JsonObject jsonEvent) {

		EventData oldStatus = new EventDataImpl();
		oldStatus.setEventAttribute(getAttributeForEventDataMember(updateStatusEventType, StatusUpdateEventType.getOldStatusAttributeName()));
		oldStatus.setValue(productConfiguration.getState().toString());

		EventData newStatus = new EventDataImpl();
		newStatus.setEventAttribute(getAttributeForEventDataMember(updateStatusEventType, StatusUpdateEventType.getNewStatusAttributeName()));
		newStatus.setValue(newProductState.toString());

		EventData timestamp = new EventDataImpl();
		timestamp.setEventAttribute(getAttributeForEventDataMember(updateStatusEventType, StatusUpdateEventType.getTimestampAttributeName()));
		timestamp.setValue(jsonEvent.get(StatusUpdateEventType.getTimestampAttributeName()).getAsString());

		return new ArrayList<>(Arrays.asList(oldStatus, newStatus, timestamp));
	}

	/**
	 * This method checks whether an event attribute is valid.
	 * @param attribute - the event attribute to check
	 * @param attributeDataType - the attribute data type for the event
	 * @return - true if event attribute is valid
	 */
	protected boolean isValid(EventAttribute attribute, EventDataType attributeDataType) {
		return attribute != null && attribute.getName().length() > 0 && attribute.getType() == attributeDataType;
	}

	/**
	 * Logs an exception stack trace on log level trace.
	 * @param e - exception to log
	 */
	private void logTrace(Exception e) {
		logger.trace("Reason: ", e);
	}
}
