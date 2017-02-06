package de.hpi.bpt.argos.persistence.model.event;

import de.hpi.bpt.argos.persistence.database.PersistenceEntity;
import de.hpi.bpt.argos.persistence.model.event.data.EventData;
import de.hpi.bpt.argos.persistence.model.event.type.EventType;
import de.hpi.bpt.argos.persistence.model.product.Product;

import java.util.List;

/**
 * This interface represents the events. It extends persistence entity.
 */
public interface Event extends PersistenceEntity {

	/**
	 * This method return the related product.
	 * @return - the related product
	 */
	Product getProduct();

	/**
	 * This method sets related product.
	 * @param product - the related product to be set
	 */
	void setProduct(Product product);

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
	 * This method returns the event data for this event.
	 * @return - a list of event data
	 */
	List<EventData> getEventData();

	/**
	 * This method sets the event data of this event.
	 * @param eventData - the event data to be set
	 */
	void setEventData(List<EventData> eventData);
}
