package de.hpi.bpt.argos.api.eventQueries;

import de.hpi.bpt.argos.api.event.EventEndpoint;
import de.hpi.bpt.argos.api.eventTypes.EventTypeEndpoint;
import de.hpi.bpt.argos.common.RestEndpoint;
import spark.Request;
import spark.Response;

import java.util.Objects;


/**
 * This interface represents the rest endpoint to edit event queries.
 */
public interface EventQueryEndpoint extends RestEndpoint {

	/**
	 * This method is called via the API and updates an existing event query.
	 * @param request - Spark defined parameter containing request object
	 * @param response - Spark defined parameter containing response object
	 * @return - returns a success message
	 */
	String updateEventQuery(Request request, Response response);

	/**
	 * This method returns the basic URI to update an existing event query.
	 * @return - the URI to update an existing event query
	 */
	static String getUpdateEventQueryBaseUri() {
		return String.format("/api/eventqueries/update/%1$s", EventTypeEndpoint.getEventTypeIdParameter(true));
	}

	/**
	 * This method returns the URI to update a specific event query.
	 * @param eventTypeId - the event type id of the event type, which query should be updated
	 * @return - the URI to update a specific event query
	 */
	static String getUpdateEventQueryUri(long eventTypeId) {
		return getUpdateEventQueryBaseUri().replaceAll(
				EventEndpoint.getEventIdParameter(true),
				Objects.toString(eventTypeId, "0"));
	}
}
