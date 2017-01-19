package de.hpi.bpt.argos.model.product;

import de.hpi.bpt.argos.model.event.Event;
import de.hpi.bpt.argos.serialization.Serializable;

import java.util.Set;

/**
 * This interface represents the products. It is serializable.
 */
public interface Product extends Serializable {
	/**
	 * This method returns the id of this product.
	 * @return - the id of this product  as an integer
	 */
	int getId();

	/**
	 * This method returns the id of this product.
	 * @param id - the id of this product  to be set
	 */
	void setId(int id);

	/**
	 * This method returns a set of events of this product.
	 * @return - a set of events
	 */
	Set<Event> getEvents();

	/**
	 * This method sets the events of this product.
	 * @param events - the events to be set
	 */
	void setEvents(Set<Event> events);

	/**
	 * This method returns the metadata of this product.
	 * @return - the metadata of this product  as a ProductMetadata object
	 */
	ProductMetaData getMetaData();

	/**
	 * This method sets the metadata of this product.
	 * @param productMetaData - the product  metadata to be set
	 */
	void setMetaData(ProductMetaData productMetaData);
}
