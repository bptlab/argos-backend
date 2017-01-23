package de.hpi.bpt.argos.persistence.model.event;

import de.hpi.bpt.argos.persistence.model.product.Product;

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
	 * This method returns the event type of this event.
	 * @return - the event type of this event as an EventyType object
	 */
	EventType getType();

	/**
	 * This method sets the event type of this event.
	 * @param type - the event type
	 */
	void setEventType(EventType type);
}
