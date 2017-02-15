package de.hpi.bpt.argos.api.event;

import de.hpi.bpt.argos.common.RestEndpoint;
import spark.Request;
import spark.Response;

import java.util.Objects;

/**
 * This interface represents the endpoint for retrieving events.
 */
public interface EventEndpoint extends RestEndpoint {

    /**
     * This method is called via API and returns a single event.
     * @param request - Spark defined parameter containing request object
     * @param response - Spark defined parameter containing response object
     * @return - returns a JSON string of a single event object
     */
    String getEvent(Request request, Response response);

    /**
     * This method returns the basic URI to retrieve single event with path variables.
     * @return - the URI to retrieve single events from
     */
    static String getEventBaseUri() {
        return String.format("/api/events/%1$s", getEventIdParameter(true));
    }

    /**
     * This method returns the URI to receive a single event.
     * @param eventId - the event id to receive
     * @return - the URI to receive the specified event
     */
    static String getEventUri(long eventId)  {
        return getEventBaseUri().replaceAll(getEventIdParameter(true), Objects.toString(eventId, "0"));
    }

    /**
     * This method returns the event id path parameter.
     * @param includePrefix - if a prefix should be included
     * @return - event id path parameter as a string
     */
    static String getEventIdParameter(boolean includePrefix) {
        return RestEndpoint.getParameter("eventId", includePrefix);
    }
}
