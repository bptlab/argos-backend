package de.hpi.bpt.argos.model.product;

import de.hpi.bpt.argos.model.event.EventType;
import de.hpi.bpt.argos.serialization.Serializable;

import java.util.Date;
import java.util.Set;

/**
 * This interface represents the metadata for a product. It is serializable.
 */
public interface ProductMetaData extends Serializable {
	/**
	 * This method returns the number of devices of the related product.
	 * @return - the number of devices the related product  as an integer
	 */
	int getNumberOfDevices();

	/**
	 * This method sets the number of devices of the related product.
	 * @param numberOfDevices - the number of devices to be set
	 */
	void setNumberOfDevices(int numberOfDevices);

	/**
	 * This method returns the number of events that occurred and included the related product.
	 * @return - the number of events that occurred and included the related product as an integer
	 */
	int getNumberOfEvents();

	/**
	 * This method sets the number of events that occurred and included the related product.
	 * @param numberOfEvents - the number of events that occurred and included the related product to be set
	 */
	void setNumberOfEvents(int numberOfEvents);

	/**
	 * This method returns the production start date of the related product.
	 * @return - the production start date of the related product as a date object
	 */
	Date getProductionStart();

	/**
	 * This method sets the production start date of the related product.
	 * @param productionStart - the production start date of the related product to be set
	 */
	void setProductionStart(Date productionStart);

	/**
	 * This method returns the state of the related product.
	 * @return - the product state as a ProductState enum
	 */
	ProductState getState();

	/**
	 * This method sets the state of the related product.
	 * @return - the product state to be set
	 */
	void setState(ProductState productState);

	/**
	 * This method returns the name of the related product.
	 * @return - the name of the related product to be set
	 */
	String getName();

	/**
	 * This method sets the name of the related product.
	 * @param name - the name of the related product to be set
	 */
	void setName(String name);

	/**
	 * This method returns the order number of the related product.
	 * @return - the order number of the related product as an integer
	 */
	int getOrderNumber();

	/**
	 * This method sets the order number of the related product.
	 * @param orderNumber - the order number of the related product to be set
	 */
	void setOrderNumber(int orderNumber);

	/**
	 * This method returns the status description of the related product.
	 * @return - the status description of the related product as a string
	 */
	String getStateDescription();

	/**
	 * This method sets the status description of the related product.
	 * @return - the status description of the related product to be set
	 */
	void setStateDescription(String stateDescription);

	/**
	 * This method returns a set of event types. These are the types of events that occurred for the related product.
	 * @return - a set of event types
	 */
	Set<EventType> getEventTypes();

	/**
	 * This method sets the event types of the related product.
	 * @param eventTypes - a set of event types
	 */
	void setEventTypes(Set<EventType> eventTypes);
}
