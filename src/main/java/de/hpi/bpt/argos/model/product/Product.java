package de.hpi.bpt.argos.model.product;

import de.hpi.bpt.argos.model.event.Event;
import de.hpi.bpt.argos.serialization.Serializable;

import java.util.Set;

/**
 * This interface represents the products. It is serializable.
 */
public interface Product extends Serializable {

	/**
	 * This method return the unique id of this product.
	 * @return - the unique id of this product as integer
	 */
	int getId();

	/**
	 * This method sets the unique id for this product.
	 * @param id - the unique id
	 */
	void setId(int id);

	/**
	 * This method returns a set of events which occurred including this product.
	 * @return - a set of events which occurred including this product as Set<Event>
	 */
	Set<Event> getEvents();

	/**
	 * This method sets the events which occurred including this product.
	 * @param events - a set of events
	 */
	void setEvents(Set<Event> events);

	/**
	 * This methods return the meta data of this product.
	 * @return - the meta data of this product as ProductMetaData object
	 */
	ProductMetaData getMetaData();

	/**
	 * This method sets the meta data of this product.
	 * @param metaData - the meta data
	 */
	void setMetaData(ProductMetaData metaData);
}
