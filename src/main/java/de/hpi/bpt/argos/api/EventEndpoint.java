package de.hpi.bpt.argos.api;

import de.hpi.bpt.argos.common.RestEndpoint;
import spark.Request;
import spark.Response;

/**
 * This interface represents the endpoint for retrieving single events.
 */
public interface EventEndpoint extends RestEndpoint {
    /**
     * This method is called via API and returns a single event.
     * @param request - Spark defined parameter containing request object
     * @param response - Spark defined parameter containing response object
     * @return - returns a JSON string of a single event object
     */
    String getSingleEvent(Request request, Response response);

}
