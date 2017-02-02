package de.hpi.bpt.argos.persistence.database;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.hpi.bpt.argos.api.event.EventEndpoint;
import de.hpi.bpt.argos.api.product.ProductEndpoint;
import de.hpi.bpt.argos.api.productFamily.ProductFamilyEndpoint;
import de.hpi.bpt.argos.notifications.PushNotificationType;
import de.hpi.bpt.argos.persistence.model.event.Event;
import de.hpi.bpt.argos.persistence.model.event.EventImpl;
import de.hpi.bpt.argos.persistence.model.event.attribute.EventAttribute;
import de.hpi.bpt.argos.persistence.model.event.data.EventData;
import de.hpi.bpt.argos.persistence.model.event.data.EventDataImpl;
import de.hpi.bpt.argos.persistence.model.event.type.EventType;
import de.hpi.bpt.argos.persistence.model.product.Product;
import de.hpi.bpt.argos.persistence.model.product.ProductFamily;
import de.hpi.bpt.argos.persistence.model.product.ProductFamilyImpl;
import de.hpi.bpt.argos.persistence.model.product.ProductImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class PersistenceEntityManagerImpl implements PersistenceEntityManager {
	protected static final JsonParser jsonParser = new JsonParser();

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
		updateEntity(PushNotificationType.CREATION, event, EventEndpoint.getEventUri(event.getId()));

		return event;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<EventType> createDefaultEventTypes() {
		// TODO: implement XSD parsing
		return new ArrayList<>();
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
			updateEntity(PushNotificationType.CREATION, product, ProductEndpoint.getProductUri(product.getId()));
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
			updateEntity(PushNotificationType.CREATION, productFamily, ProductFamilyEndpoint.getProductFamilyUri(productFamily.getId()));
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

		for(Map.Entry<String, JsonElement> property : jsonEvent.entrySet()) {
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
		for(EventAttribute attribute : eventType.getAttributes()) {
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
		for(EventData data : eventData) {
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
		for(EventData data : eventData) {
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
		for(PersistenceEntityManagerEventReceiver eventReceiver : eventReceivers) {
			eventReceiver.onEntityModified(type, entity, fetchUrl);
		}
	}
}
