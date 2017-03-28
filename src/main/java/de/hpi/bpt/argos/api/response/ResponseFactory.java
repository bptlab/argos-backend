package de.hpi.bpt.argos.api.response;

import de.hpi.bpt.argos.eventHandling.EventPlatformRestEndpoint;
import de.hpi.bpt.argos.persistence.database.PersistenceEntityManager;
import de.hpi.bpt.argos.persistence.model.product.ProductState;

/**
 * This interface represents factories which produce rest responses.
 */
public interface ResponseFactory {

	int HTTP_SUCCESS_CODE = 200;

	int HTTP_FORBIDDEN_CODE = 403;

	int HTTP_NOT_FOUND_CODE = 404;

	int HTTP_ERROR_CODE = 500;

	/**
	 * This method sets up this response factory.
	 * @param entityManager - the entity manager to get persistence entities from
	 * @param eventPlatformRestEndpoint - the event platform rest endpoint to register event queries at
	 */
	void setup(PersistenceEntityManager entityManager, EventPlatformRestEndpoint eventPlatformRestEndpoint);

	/**
	 * This method returns a json representation of all product families.
	 * @return - a json representation of all product families
	 */
	String getAllProductFamilies();

	/**
	 * This method returns a json representation of one specific product family.
	 * @param productFamilyId - the product family id
	 * @return - a json representation of the specific product family
	 */
	String getProductFamily(long productFamilyId);

	/**
	 * This method returns a json representation of one specific product.
	 * @param productId - the product id
	 * @return - a json representation of the specific product
	 */
	String getProduct(long productId);

	/**
	 * This method returns a json representation of one specific product configuration.
	 * @param productConfigurationId - the product configuration id
	 * @return - a json representation of the specific product configuration
	 */
	String getProductConfiguration(long productConfigurationId);

	/**
	 * This method returns a json representation of all event types for one specific product id.
	 * @param productId - the specific product identifier
	 * @return - a json representation of all event types
	 */
	String getAllProductEventTypes(long productId);

	/**
	 * This method returns a json representation of all event types for one specific product configuration id.
	 * @param productConfigurationId - the specific product configuration identifier
	 * @return - a json representation of all event types
	 */
	String getAllProductConfigurationEventTypes(long productConfigurationId);

	/**
	 * This method returns a json representation of the specified event type.
	 * @param eventTypeId - the id of the event type
	 * @return - a json representation of the event type
	 */
	String getEventType(long eventTypeId);

	/**
	 * This method returns a json representation of all event types.
	 * @return - a json representation of all event types
	 */
	String getAllEventTypes();

	/**
	 * This method returns a json representation of all events for one specific product with a specific event type within a certain range.
	 * @param productId - the product identifier
	 * @param eventTypeId - the event type identifier
	 * @param eventIndexFrom - the start index for the events
	 * @param eventIndexTo - the end index of the events
	 * @return - a json representation of the requested events
	 */
	String getEventsForProduct(long productId, long eventTypeId, int eventIndexFrom, int eventIndexTo);

	/**
	 * This method returns a json representation of all events for one specific product configuration with a specific event type within a certain
	 * range.
	 * @param productConfigurationId - the product configuration identifier
	 * @param eventTypeId - the event type identifier
	 * @param eventIndexFrom - the start index for the events
	 * @param eventIndexTo - the end index of the events
	 * @return - a json representation of the requested events
	 */
	String getEventsForProductConfiguration(long productConfigurationId, long eventTypeId, int eventIndexFrom, int eventIndexTo);

	/**
	 * This method returns a json representation of an event defined by the event id.
	 * @param eventId - the event identifier
	 * @return - a json representation of the requested event
	 */
	String getEvent(long eventId);

	/**
	 * This method tries to create a new event type from a request body.
	 * @param requestBody - the request body as string
	 */
	void createEventType(String requestBody);

	/**
	 * This method tries to update an existing event query from a request body.
	 * @param eventTypeId - the event type id, which contains the event query to update
	 * @param requestBody - the request body, which should contain the new event query
	 */
	void updateEventQuery(long eventTypeId, String requestBody);

	/**
	 * This method tries to update the status update event query for a specific product.
	 * @param productConfigurationId - the product configuration to update
	 * @param newState - the new state of the product, after an event for this query arrived
	 * @param requestBody - the request body, which should contain the new status update query and the corresponding state
	 */
	void updateStatusEventQuery(long productConfigurationId, ProductState newState, String requestBody);

	/**
	 * This method tries to delete an existing event type.
	 * @param eventTypeId - the event type id to delete
	 * @return - a json array of event type ids, which block the deletion process or the success response
	 */
	String deleteEventType(long eventTypeId);

	/**
	 * This method returns a json representation of all supported data types.
	 * @return - a json representation of all supported data types
	 */
	String getSupportedDataTypes();

	/**
	 * This method returns a default response to generic requests.
	 * @return - a simple response to a request
	 */
	default String finishRequest() {
		return "request finished";
	}
}
