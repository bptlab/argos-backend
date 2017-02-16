package de.hpi.bpt.argos.eventHandling;

import de.hpi.bpt.argos.api.product.ProductEndpoint;
import de.hpi.bpt.argos.common.RestEndpoint;
import spark.Request;
import spark.Response;

import java.util.Objects;

/**
 * This interface represents an event receiver that is called when an event is sent from the event processing platform.
 * It extends the RestEndpoint.
 */
public interface EventReceiver extends RestEndpoint {
    /**
     * This method is responsible for receiving events by reacting to the spark request sent from the event
     * processing platform.
     * @param request - spark request to be used
     * @param response - spark request to be used
     * @return - returns a response for the event platform
     */
	String receiveEvent(Request request, Response response);

	/**
	 * This method returns the basic URI to send events to with path variables.
	 * @return - the URI to send events to
	 */
	static String getPostEventBaseUri() {
		return String.format("/api/events/receiver/%1$s",
				ProductEndpoint.getEventTypeIdParameter(true));
	}

	/**
	 * This method returns the URI to post events to.
	 * @param eventTypeId - the event type id of the sent event
	 * @return - the URI to post events to
	 */
	static String getPostEventUri(long eventTypeId) {
		return getPostEventBaseUri().replaceAll(ProductEndpoint.getEventTypeIdParameter(true), Objects.toString(eventTypeId, "0"));
	}
}
