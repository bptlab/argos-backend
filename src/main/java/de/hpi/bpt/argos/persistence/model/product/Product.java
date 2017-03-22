package de.hpi.bpt.argos.persistence.model.product;

import de.hpi.bpt.argos.persistence.database.PersistenceEntity;
import de.hpi.bpt.argos.persistence.model.event.EventQuery;
import de.hpi.bpt.argos.persistence.model.product.error.ErrorType;

import java.util.Date;
import java.util.Set;

/**
 * This interface represents the products. It extends persistence entity.
 */
public interface Product extends PersistenceEntity {

	/**
	 * This method returns the product family this product is in.
	 * @return - the product family this product is in
	 */
	ProductFamily getProductFamily();

	/**
	 * This method sets the product family this product is in.
	 * @param productFamily - the product family to be set
	 */
	void setProductFamily(ProductFamily productFamily);

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
	 * This method returns the event query which leads to a change in the state of this product.
	 * @return - the event query which sets this product in Running state
	 */
	EventQuery getTransitionToRunningState();

	/**
	 * This method sets the event query which leads to a change in the state of this product.
	 * @param eventQuery - the event query which sets this product in Running state
	 */
	void setTransitionToRunningState(EventQuery eventQuery);

	/**
	 * This method returns the event query which leads to a change in the state of this product.
	 * @return - the event query which sets this product in Warning state
	 */
	EventQuery getTransitionToWarningState();

	/**
	 * This method sets the event query which leads to a change in the state of this product.
	 * @param eventQuery - the event query which sets this product in Warning state
	 */
	void setTransitionToWarningState(EventQuery eventQuery);

	/**
	 * This method returns the event query which leads to a change in the state of this product.
	 * @return - the event query which sets this product in Error state
	 */
	EventQuery getTransitionToErrorState();

	/**
	 * This method sets the event query which leads to a change in the state of this product.
	 * @param eventQuery - the event query which sets this product in Error state
	 */
	void setTransitionToErrorState(EventQuery eventQuery);

	/**
	 * This method returns the status update query which changes the product state to a specific state.
	 * @param newState - the new state of the product, after an event of the query arrived
	 * @return - the status update query which changes the product state to a specific state
	 */
	EventQuery getStatusUpdateQuery(ProductState newState);

	/**
	 * This method sets the status update query which changes the product state to a specific state.
	 * @param newState - the new state of the product, after an event of the query arrived
	 * @param eventQuery - the status update query to be set
	 */
	void setStatusUpdateQuery(ProductState newState, EventQuery eventQuery);

	/**
	 * This method returns the number of devices that are installed from this product families.
	 * @return - the number of devices installed as an integer
	 */
	long getNumberOfDevices();

	/**
	 * This method returns the number of events that occurred for this product.
	 * @return - the number of events occurred as an integer
	 */
	long getNumberOfEvents();

	/**
	 * This method increments the number of events for this product.
	 * @param count - the count of how much new events were received
	 */
	void incrementNumberOfEvents(long count);

	/**
	 * This method increments the number of devices for this product.
	 * @param count - the count of how much new devices were found
	 */
	void incrementNumberOfDevices(long count);

	/**
	 * This method returns a set of error types, which occurred for this product.
	 * @return - a set of error types
	 */
	Set<ErrorType> getErrorTypes();

	/**
	 * This method returns the error type with the corresponding cause code, or null.
	 * @param causeCode - the cause code of the error type
	 * @return - the error type with the cause code or null
	 */
	ErrorType getErrorType(int causeCode);

	/**
	 * This method sets the error types, which occurred for this product.
	 * @param errorTypes - a set of error types to be set
	 */
	void setErrorTypes(Set<ErrorType> errorTypes);

	/**
	 * This method adds a new error type to this product.
	 * @param errorType - the error type to be added
	 */
	void addErrorType(ErrorType errorType);
}
