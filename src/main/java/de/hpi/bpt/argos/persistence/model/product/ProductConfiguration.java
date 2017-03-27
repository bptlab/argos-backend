package de.hpi.bpt.argos.persistence.model.product;

import de.hpi.bpt.argos.persistence.database.PersistenceEntity;
import de.hpi.bpt.argos.persistence.model.event.EventQuery;
import de.hpi.bpt.argos.persistence.model.product.error.ErrorType;

import java.util.Set;

/**
 * This interface represents different product configurations within a specific product.
 */
public interface ProductConfiguration extends PersistenceEntity {

	/**
	 * This method returns the product this configuration belongs to.
	 * @return - the product this configuration belongs to
	 */
	Product getProduct();

	/**
	 * This method sets the product this configuration belongs to.
	 * @param product - the product to be set
	 */
	void setProduct(Product product);

	/**
	 * This method returns the coding plug id of this product configuration.
	 * @return - the coding plug id
	 */
	int getCodingPlugId();

	/**
	 * This method sets the coding plug id for this product configuration.
	 * @param codingPlugId - the coding plug id to be set
	 */
	void setCodingPlugId(int codingPlugId);

	/**
	 * This method returns a set of float, which represent all supported coding plug software versions for this configuration.
	 * @return - a set of float, which represent all supported coding plug software versions
	 */
	Set<Float> getCodingPlugSoftwareVersions();

	/**
	 * This method sets all supported coding plug versions for this configuration.
	 * @param codingPlugSoftwareVersions - a set of float, which represent the supported coding plug software versions
	 */
	void setCodingPlugSoftwareVersions(Set<Float> codingPlugSoftwareVersions);

	/**
	 * This method adds a new supported coding plug software version to this configuration.
	 * @param codingPlugSoftwareVersion - the new software version to be added
	 */
	void addCodingPlugSoftwareVersion(float codingPlugSoftwareVersion);

	/**
	 * This method checks whether a given coding plug software version is supported by this configuration.
	 * @param codingPlugSoftwareVersion - the software version to check
	 * @return - true, if the version is supported
	 */
	boolean supports(float codingPlugSoftwareVersion);

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
