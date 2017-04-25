package de.hpi.bpt.argos.eventProcessing;

import de.hpi.bpt.argos.core.Argos;
import spark.Request;
import spark.Response;

/**
 * This class offers an interface for the eventProcessingPlatform to send events to.
 */
@FunctionalInterface
public interface EventReceiver {

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
		// TODO
		return "";
	}

	/**
	 * This method returns the uri to send events from a specific eventType to.
	 * @param eventTypeId - the unique identifier of the eventType
	 * @return - the uri to send events from a specific eventType to
	 */
	static String getReceiveEventUri(long eventTypeId) {
		// TODO
		return "";
	}
}
