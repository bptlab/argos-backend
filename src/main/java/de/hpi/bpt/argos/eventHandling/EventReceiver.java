package de.hpi.bpt.argos.eventHandling;

import de.hpi.bpt.argos.api.product.ProductEndpoint;
import de.hpi.bpt.argos.common.RestEndpoint;
import spark.Request;
import spark.Response;

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
	 * This method returns the basic URI to send events to path variables.
	 * @return - the URI to send events to
	 */
	static String getPostEventBaseUri() {
		return String.format("/api/events/receiver/%1$s",
				ProductEndpoint.getEventTypeIdParameter(true));
	}


}
