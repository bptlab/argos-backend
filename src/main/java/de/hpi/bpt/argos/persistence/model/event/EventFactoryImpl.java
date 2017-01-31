package de.hpi.bpt.argos.persistence.model.event;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.hpi.bpt.argos.persistence.database.DatabaseConnection;
import de.hpi.bpt.argos.persistence.model.event.attribute.EventAttribute;
import de.hpi.bpt.argos.persistence.model.event.data.Event;
import de.hpi.bpt.argos.persistence.model.event.data.EventData;
import de.hpi.bpt.argos.persistence.model.event.data.EventDataImpl;
import de.hpi.bpt.argos.persistence.model.event.type.EventType;
import de.hpi.bpt.argos.persistence.model.product.Product;
import de.hpi.bpt.argos.persistence.model.product.ProductFactory;
import de.hpi.bpt.argos.persistence.model.product.ProductFactoryImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class EventFactoryImpl implements EventFactory {

	protected static final JsonParser jsonParser = new JsonParser();
	protected DatabaseConnection databaseConnection;

	public EventFactoryImpl(DatabaseConnection databaseConnection) {
		this.databaseConnection = databaseConnection;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDatabaseConnection(DatabaseConnection databaseConnection) {
		this.databaseConnection = databaseConnection;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Event getEvent(EventType eventType, String jsonEvent) {
		Event event = new EventImpl();
		JsonObject json = jsonParser.parse(jsonEvent).getAsJsonObject();

		event.setEventData(getEventData(eventType, json));
		event.setEventType(eventType);

		ProductFactory productFactory = new ProductFactoryImpl(databaseConnection);
		Product product = productFactory.getProduct(getProductFamilyIdentification(eventType, event.getEventData()),
				getProductIdentification(eventType, event.getEventData()));

		if (product == null) {
			return null;
		}

		event.setProduct(product);

		return event;
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
}
