package de.hpi.bpt.argos.eventHandling;

import de.hpi.bpt.argos.api.eventType.EventTypeEndpoint;
import de.hpi.bpt.argos.api.productConfiguration.ProductConfigurationEndPoint;
import de.hpi.bpt.argos.common.RestEndpoint;
import de.hpi.bpt.argos.persistence.model.product.ProductState;
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
	 * This method is responsible for receiving status update events by reacting to the spark request sent from the event
	 * processing platform.
	 * @param request - spark request to be used
	 * @param response - spark request to be used
	 * @return - returns a response for the event platform
	 */
	String receiveStatusUpdateEvent(Request request, Response response);

	/**
	 * This method returns the basic URI to send events to with path variables.
	 * @return - the URI to send events to
	 */
	static String getReceiveEventBaseUri() {
		return String.format("/api/events/receiver/%1$s",
				EventTypeEndpoint.getEventTypeIdParameter(true));
	}

	/**
	 * This method returns the basic URI to send status change events to with path variables.
	 * @return - the URI to send status update events to
	 */
	static String getReceiveStatusUpdateEventBaseUri() {
		return String.format("/api/events/statuschange/%1$s/%2$s",
				ProductConfigurationEndPoint.getProductConfigurationIdParameter(true),
				ProductConfigurationEndPoint.getNewProductStatusParameter(true));
	}

	/**
	 * This method returns the URI to post events to.
	 * @param eventTypeId - the event type id of the sent event
	 * @return - the URI to post events to
	 */
	static String getReceiveEventUri(long eventTypeId) {
		return getReceiveEventBaseUri().replaceAll(EventTypeEndpoint.getEventTypeIdParameter(true),
				Objects.toString(eventTypeId, "0"));
	}

	/**
	 * This method returns the URI to post status update events to.
	 * @param productConfigurationId - the product configuration id, which status has changed
	 * @param newProductState - the new state of the product
	 * @return - the URI to post status update events to
	 */
	static String getReceiveStatusUpdateEventUri(long productConfigurationId, ProductState newProductState) {
		return getReceiveStatusUpdateEventBaseUri()
				.replaceAll(ProductConfigurationEndPoint.getProductConfigurationIdParameter(true), Objects.toString(productConfigurationId, "0"))
				.replaceAll(ProductConfigurationEndPoint.getNewProductStatusParameter(true), Objects.toString(newProductState, ""));
	}
}
