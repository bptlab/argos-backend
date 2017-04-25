package de.hpi.bpt.argos.api.eventType;

import spark.Request;
import spark.Response;

/**
 * This interface represents the endpoint to receive event types.
 */
public interface EventTypeEndpoint {

    /**
     * This method is called via API and returns all event types.
     * @param request - Spark defined parameter containing request object
     * @param response - Spark defined parameter containing response object
     * @return - returns a JSON string of all event types
     */
    String getEventTypes(Request request, Response response);

    /**
     * This method is called via API and returns a single event type.
     * @param request - Spark defined parameter containing request object
     * @param response - Spark defined parameter containing response object
     * @return - returns a JSON string of a single event type
     */
    String getEventType(Request request, Response response);

    /**
     * This method is called via API and creates a new event type.
     * @param request - Spark defined parameter containing request object
     * @param response - Spark defined parameter containing response object
     * @return - the response for the creation process
     */
    String createEventType(Request request, Response response);

    /**
     * This method is called via API and deletes a specific event type.
     * @param request - Spark defined parameter containing request object
     * @param response - Spark defined parameter containing response object
     * @return - the response for the deletion process
     */
    String deleteEventType(Request request, Response response);

    /**
     * This method is called via API and returns the attributes of an event type.
     * @param request - Spark defined parameter containing request object
     * @param response - Spark defined parameter containing response object
     * @return - returns a JSON string of the attributes of an event type
     */
    String getEventTypeAttributes(Request request, Response response);

    /**
     * This method is called via API and returns the queries of an event type.
     * @param request - Spark defined parameter containing request object
     * @param response - Spark defined parameter containing response object
     * @return - returns a JSON string of the queries of an event type
     */
    String getEventTypeQueries(Request request, Response response);

    /**
     * This method is called via API and returns the entity mappings of an event type.
     * @param request - Spark defined parameter containing request object
     * @param response - Spark defined parameter containing response object
     * @return - returns a JSON string of the entity mappings of an event type
     */
    String getEventTypeEntityMappings(Request request, Response response);
}
