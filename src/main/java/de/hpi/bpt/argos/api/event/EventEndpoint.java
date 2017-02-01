package de.hpi.bpt.argos.api.event;

import de.hpi.bpt.argos.common.RestEndpoint;
import spark.Request;
import spark.Response;

import java.util.Objects;

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

    /**
     * This method returns the basic URI to retrieve single event with path variables.
     * @return - the URI to retrieve single events from
     */
    static String getSingleEventBaseUri() {
        return "/api/events/:eventId";
    }

    /**
     * This method returns the URI to receive a single event.
     * @param eventId - the event id to receive
     * @return - the URI to receive the specified event
     */
    static String getSingleEventUri(long eventId)  {
        return getSingleEventBaseUri().replaceAll(":eventId", Objects.toString(eventId, "0"));
    }

}
