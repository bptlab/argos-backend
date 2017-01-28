package de.hpi.bpt.argos.persistence.model.product;

import de.hpi.bpt.argos.persistence.model.event.data.Event;
import de.hpi.bpt.argos.persistence.model.event.EventSubscriptionQuery;

import java.util.Date;
import java.util.Set;

/**
 * This interface represents the products.
 */
public interface Product {

	/**
	 * This method return the unique identifier of this product.
	 * @return - the unique id of this product as integer
	 */
	int getId();

	/**
	 * This method sets the unique identifier for this product.
	 * @param id - the unique id
	 */
	void setId(int id);

	/**
	 * This method returns the production start date of this product.
	 * @return - the production start date of this product as a date object
	 */
	Date getProductionStart();

	/**
	 * This method sets the production start date of this product.
	 * @param productionStart - the production start date of this product to be set
	 */
	void setProductionStart(Date productionStart);

	/**
	 * This method returns the state of this product.
	 * @return - the product state as a ProductState enum
	 */
	ProductState getState();

	/**
	 * This method sets the state of this product.
	 * @param productState - the product state to be set
	 */
	void setState(ProductState productState);

	/**
	 * This method returns the name of this product.
	 * @return - the name of this product to be set
	 */
	String getName();

	/**
	 * This method sets the name of this product.
	 * @param name - the name of this product to be set
	 */
	void setName(String name);

	/**
	 * This method returns the order number of this product.
	 * @return - the order number of this product as an integer
	 */
	int getOrderNumber();

	/**
	 * This method sets the order number of this product.
	 * @param orderNumber - the order number of this product to be set
	 */
	void setOrderNumber(int orderNumber);

	/**
	 * This method returns the status description of this product.
	 * @return - the status description of this product as a string
	 */
	String getStateDescription();

	/**
	 * This method sets the status description of this product.
	 * @param stateDescription - the status description of this product to be set
	 */
	void setStateDescription(String stateDescription);

	/**
	 * This method returns a set of events for this product.
	 * @return - a set of events
	 */
	Set<Event> getEvents();

	/**
	 * This methods set a set of events for this product.
	 * @param events - a set of events to be set
	 */
	void setEvents(Set<Event> events);

	/**
	 * This method returns the event subscription query which leads to a change in the state of this product.
	 * @return - the event subscription query which sets this product in Running state
	 */
	EventSubscriptionQuery getTransitionToRunningState();

	/**
	 * This method sets the event subscription query which leads to a change in the state of this product.
	 * @param eventSubscriptionQuery - the event subscription query which sets this product in Running state
	 */
	void setTransitionToRunningSet(EventSubscriptionQuery eventSubscriptionQuery);

	/**
	 * This method returns the event subscription query which leads to a change in the state of this product.
	 * @return - the event subscription query which sets this product in Warning state
	 */
	EventSubscriptionQuery getTransitionToWarningState();

	/**
	 * This method sets the event subscription query which leads to a change in the state of this product.
	 * @param eventSubscriptionQuery - the event subscription query which sets this product in Warning state
	 */
	void setTransitionToWarningSet(EventSubscriptionQuery eventSubscriptionQuery);

	/**
	 * This method returns the event subscription query which leads to a change in the state of this product.
	 * @return - the event subscription query which sets this product in Error state
	 */
	EventSubscriptionQuery getTransitionToErrorState();

	/**
	 * This method sets the event subscription query which leads to a change in the state of this product.
	 * @param eventSubscriptionQuery - the event subscription query which sets this product in Error state
	 */
	void setTransitionToErrorSet(EventSubscriptionQuery eventSubscriptionQuery);

	/**
	 * This method sets the number of devices currently in use of this product eventType.
	 * @param numberOfDevices - the number of devices to be set
	 */
	void setNumberOfDevices(int numberOfDevices);

	/**
	 * This method returns the number of devices that are installed from this product families.
	 * @return - the number of devices installed as an integer
	 */
	int getNumberOfDevices();

	/**
	 * This method sets the number of events that occurred for this product.
	 * @return - the number of events occurred as an integer
	 */
	int getNumberOfEvents();

	/**
	 * This method sets the number of events that occurred for this product.
	 * @param numberOfEvents - the number of events occurred
	 */
	void setNumberOfEvents(int numberOfEvents);

	/**
	 * This method sets the number of events intelligently.
	 */
	void setNumberOfEvents();
}