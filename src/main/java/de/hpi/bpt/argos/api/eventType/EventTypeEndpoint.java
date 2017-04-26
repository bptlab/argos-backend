package de.hpi.bpt.argos.api.eventType;

import de.hpi.bpt.argos.core.Argos;
import de.hpi.bpt.argos.util.RestEndpointUtilImpl;
import spark.Request;
import spark.Response;

import java.util.Objects;

/**
 * This interface represents the endpoint to receive event types.
 */
public interface EventTypeEndpoint {
    String EVENT_TYPE_BASE_URI = String.format("%1$s/api/eventtype", Argos.getRoutePrefix());

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

    /**
     * This method returns the basic URI to retrieve all event types.
     * @return - the URI to retrieve all event types from
     */
    static String getEventTypesBaseUri() {
        return String.format("%1$s/api/eventtypes", Argos.getRoutePrefix());
    }

    /**
     * This method returns the basic URI to retrieve all event types.
     * @return - the URI to retrieve all event types from
     */
    static String getEventTypeBaseUri() {
        return String.format("%1$s/%2$s", EVENT_TYPE_BASE_URI, getEventTypeIdParameter(true));
    }

    /**
     * This method returns the basic URI to create a new event type.
     * @return - the URI to create a new event type
     */
    static String getCreateEventTypeBaseUri() {
        return String.format("%1$s/create", EVENT_TYPE_BASE_URI);
    }

    /**
     * This method returns the basic URI to delete a new event type.
     * @return - the URI to delete a new event type
     */
    static String getDeleteEventTypeBaseUri() {
        return String.format("%1$s/delete", EVENT_TYPE_BASE_URI);
    }

    /**
     * This method returns the basic URI to retrieve attributes of an event type.
     * @return - the URI to retrieve attributes of an event type
     */
    static String getEventTypeAttributesBaseUri() {
        return String.format("%1$s/%2$s/attributes", EVENT_TYPE_BASE_URI, getEventTypeIdParameter(true));
    }

    /**
     * This method returns the basic URI to retrieve queries of an event type.
     * @return - the URI to retrieve queries of an event type
     */
    static String getEventTypeQueriesBaseUri() {
        return String.format("%1$s/%2$s/queries", EVENT_TYPE_BASE_URI, getEventTypeIdParameter(true));
    }

    /**
     * This method returns the basic URI to retrieve entity mappings of an event type.
     * @return - the URI to retrieve entity mappings of an event type
     */
    static String getEventTypeEntityMappingsBaseUri() {
        return String.format("%1$s/%2$s/entitymappings", EVENT_TYPE_BASE_URI, getEventTypeIdParameter(true));
    }

    /**
     * This method returns the URI to retrieve all event types.
     * @param eventTypeId - the id of the event type to be searched for
     * @return - the URI to retrieve all event types from
     */
    static String getEventTypeUri(long eventTypeId) {
        return getEventTypeBaseUri().replaceAll(getEventTypeIdParameter(true), Objects.toString(eventTypeId, "0"));
    }

    /**
     * This method returns the URI to retrieve attributes of an event type.
     * @param eventTypeId - the id of the event type to be searched for
     * @return - the URI to retrieve attributes of an event type
     */
    static String getEventTypeAttributesUri(long eventTypeId) {
        return getEventTypeAttributesBaseUri().replaceAll(getEventTypeIdParameter(true), Objects.toString(eventTypeId, "0"));
    }

    /**
     * This method returns the URI to retrieve queries of an event type.
     * @param eventTypeId - the id of the event type to be searched for
     * @return - the URI to retrieve queries of an event type
     */
    static String getEventTypeQueriesUri(long eventTypeId) {
        return getEventTypeQueriesBaseUri().replaceAll(getEventTypeIdParameter(true), Objects.toString(eventTypeId, "0"));
    }

    /**
     * This method returns the URI to retrieve entity mappings of an event type.
     * @param eventTypeId - the id of the event type to be searched for
     * @return - the URI to retrieve entity mappings of an event type
     */
    static String getEventTypeEntityMappingsUri(long eventTypeId) {
        return getEventTypeEntityMappingsBaseUri().replaceAll(getEventTypeIdParameter(true), Objects.toString(eventTypeId, "0"));
    }

    /**
     * This method returns the event type id path parameter.
     * @param includePrefix - if a prefix should be included
     * @return - event type id path parameter as a string
     */
    static String getEventTypeIdParameter(boolean includePrefix) {
        return RestEndpointUtilImpl.getInstance().getParameter("eventTypeId", includePrefix);
    }
}
