package de.hpi.bpt.argos.eventProcessing;

import de.hpi.bpt.argos.common.Observable;
import de.hpi.bpt.argos.common.RestEndpoint;
import de.hpi.bpt.argos.core.Argos;
import de.hpi.bpt.argos.util.RestEndpointUtilImpl;
import spark.Request;
import spark.Response;

import java.util.Objects;

/**
 * This class offers an interface for the eventProcessingPlatform to send events to.
 */
public interface EventReceiver extends Observable<EventCreationObserver>, RestEndpoint {

	String EVENT_RECEIVER_BASE_URI = String.format("%1$s/api/eventreceiver", Argos.getRoutePrefix());

	/**
	 * This method is responsible for receiving events by reacting to the spark request sent from the event
	 * processing platform.
	 * @param request - spark request to be used
	 * @param response - spark request to be used
	 * @return - returns a response for the event platform
	 */
	String receiveEvent(Request request, Response response);

	/**
	 * This method returns the base uri to send events to.
	 * @return - the base uri to send events to
	 */
	static String getReceiveEventBaseUri() {
		return String.format("%1$s/%2$s", EVENT_RECEIVER_BASE_URI, getEventTypeIdParameter(true));
	}

	/**
	 * This method returns the uri to send events from a specific eventType to.
	 * @param eventTypeId - the unique identifier of the eventType
	 * @return - the uri to send events from a specific eventType to
	 */
	static String getReceiveEventUri(long eventTypeId) {
		return getReceiveEventBaseUri().replaceAll(getEventTypeIdParameter(true), Objects.toString(eventTypeId, "0"));
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
