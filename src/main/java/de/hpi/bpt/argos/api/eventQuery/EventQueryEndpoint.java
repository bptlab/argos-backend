package de.hpi.bpt.argos.api.eventQuery;

import spark.Request;
import spark.Response;

/**
 * This interface represents the endpoint to receive event queries.
 */
public interface EventQueryEndpoint {

    /**
     * This method is called via API and creates an event query to an event type.
     * @param request - Spark defined parameter containing request object
     * @param response - Spark defined parameter containing response object
     * @return - the response for the creation process
     */
    String createEventQuery(Request request, Response response);

    /**
     * This method is called via API and deletes the event query.
     * @param request - Spark defined parameter containing request object
     * @param response - Spark defined parameter containing response object
     * @return - the response for the deletion process
     */
    String deleteEventQuery(Request request, Response response);

    /**
     * This method is called via API and edits the event query.
     * @param request - Spark defined parameter containing request object
     * @param response - Spark defined parameter containing response object
     * @return - the response for the editing process
     */
    String editEventQuery(Request request, Response response);
}
