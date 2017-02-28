package de.hpi.bpt.argos.api.eventType;

import de.hpi.bpt.argos.common.RestEndpoint;
import spark.Request;
import spark.Response;

import java.util.Objects;

/**
 * This interface represents the endpoint to receive event types.
 */
public interface EventTypeEndpoint extends RestEndpoint {

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
	 * This method returns the basic URI to retrieve all event types.
	 * @return - the URI to retrieve all event types from
	 */
	static String getEventTypesBaseUri() {
		return "/api/eventtypes";
	}

	/**
	 * This method returns the basic URI to retrieve a single event type with path variables.
	 * @return - the URI to retrieve a single event types from
	 */
	static String getEventTypeBaseUri() {
		return String.format("/api/eventtypes/%1$s", getEventTypeIdParameter(true));
	}

	/**
	 * This method returns the basic URI to create new event type.
	 * @return - the URI to create new event type
	 */
	static String getCreateEventTypeBaseUri() {
		return String.format("%1$s/%2$s", getEventTypesBaseUri(), "create");
	}

	/**
	 * This method returns the basic URI to delete a specific event type.
	 * @return - the URI to delete a specific event type
	 */
	static String getDeleteEventTypeBaseUri() {
		return String.format("%1$s/%2$s/%3$s", getEventTypesBaseUri(), "delete", getEventTypeIdParameter(true));
	}

	/**
	 * This method returns the URI to receive a single event type.
	 * @param eventTypeId - the event type id to receive
	 * @return - the URI to receive the specified event type
	 */
	static String getEventTypeUri(long eventTypeId) {
		return getEventTypeBaseUri().replaceAll(getEventTypeIdParameter(true), Objects.toString(eventTypeId, "0"));
	}

	/**
	 * This method returns the URI to delete a specific event type.
	 * @param eventTypeId - the event type id to delete
	 * @return - the URI to delete a specific event type
	 */
	static String getDeleteEventTypeUri(long eventTypeId) {
		return getDeleteEventTypeBaseUri().replaceAll(getEventTypeIdParameter(true), Objects.toString(eventTypeId, "0"));
	}

	/**
	 * This method returns the event type id path parameter.
	 * @param includePrefix - if a prefix should be included
	 * @return - event type id path parameter as a string
	 */
	static String getEventTypeIdParameter(boolean includePrefix) {
		return RestEndpoint.getParameter("eventTypeId", includePrefix);
	}
}
