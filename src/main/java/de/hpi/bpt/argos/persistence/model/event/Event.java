package de.hpi.bpt.argos.persistence.model.event;

import de.hpi.bpt.argos.persistence.model.product.Product;

import java.util.List;
import java.util.Map;

/**
 * This interface represents the events.
 */
public interface Event {

	/**
	 * This method return the unique identifier for this event.
	 * @return - the unique identifier fot this event
	 */
	int getId();

	/**
	 * This method sets the unique identifier for this event.
	 * @param id - the unique identifier to be set
	 */
	void setId(int id);

	/**
	 * This method return the related product.
	 * @return - the related product
	 */
	Product getProduct();

	/**
	 * This method sets related product.
	 * @param product - the related product to be set
	 */
	void setProductId(Product product);

	/**
	 * This method returns the event eventType of this event.
	 * @return - the event eventType of this event as an EventyType object
	 */
	EventType getEventType();

	/**
	 * This method sets the event eventType of this event.
	 * @param type - the event eventType
	 */
	void setEventType(EventType type);

	/**
	 * This method returns all event data for this event.
	 * @return - a map from event attribute to event data
	 */
	Map<EventAttribute, EventData> getEventData();

	/**
	 * This method sets the event data for this event.
	 * @param eventData - the event data to be set
	 */
	void setEventData(Map<EventAttribute, EventData> eventData);
}
