package de.hpi.bpt.argos.api.eventQuery;

import de.hpi.bpt.argos.common.RestEndpoint;
import de.hpi.bpt.argos.core.Argos;
import de.hpi.bpt.argos.util.RestEndpointUtilImpl;
import spark.Request;
import spark.Response;

import java.util.Objects;

/**
 * This interface represents the endpoint to receive event queries.
 */
public interface EventQueryEndpoint extends RestEndpoint {
    String EVENT_QUERY_BASE_URI = String.format("%1$s/api/eventquery", Argos.getRoutePrefix());

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

    /**
     * This method returns the basic URI to create an event query for an event type.
     * @return - the URI to create an event query
     */
    static String getCreateEventQueryBaseUri() {
        return  String.format("%1$s/create", EVENT_QUERY_BASE_URI);
    }

    /**
     * This method returns the basic URI to delete an event query.
     * @return - the URI to delete an event query
     */
    static String getDeleteEventQueryBaseUri() {
        return  String.format("%1$s/%2$s/delete", EVENT_QUERY_BASE_URI, getEventQueryIdParameter(true));
    }

    /**
     * This method returns the basic URI to edit an event query.
     * @return - the URI to edit an event query
     */
    static String getEditEventQueryBaseUri() {
        return  String.format("%1$s/%2$s/edit", EVENT_QUERY_BASE_URI, getEventQueryIdParameter(true));
    }

    /**
     * This method returns the URI to delete an event query.
     * @param eventQueryId - the id of the event query to be searched for
     * @return - the URI to delete an event query
     */
    static String getDeleteEventQueryUri(long eventQueryId) {
        return  getDeleteEventQueryBaseUri().replaceAll(getEventQueryIdParameter(true), Objects.toString(eventQueryId, "0"));
    }

    /**
     * This method returns the URI to edit an event query.
     * @param eventQueryId - the id of the event query to be searched for
     * @return - the URI to edit an event query
     */
    static String getEditEventQueryUri(long eventQueryId) {
        return  getEditEventQueryBaseUri().replaceAll(getEventQueryIdParameter(true), Objects.toString(eventQueryId, "0"));
    }

    /**
     * This method returns the entity type id path parameter.
     * @param includePrefix - if a prefix should be included
     * @return - entity type id path parameter as a string
     */
    static String getEventQueryIdParameter(boolean includePrefix) {
        return RestEndpointUtilImpl.getInstance().getParameter("typeId", includePrefix);
    }
}
