package de.hpi.bpt.argos.eventHandling;

import de.hpi.bpt.argos.api.eventType.EventTypeEndpoint;
import de.hpi.bpt.argos.api.product.ProductEndpoint;
import de.hpi.bpt.argos.api.productConfiguration.ProductConfigurationEndPoint;
import de.hpi.bpt.argos.api.response.ResponseFactory;
import de.hpi.bpt.argos.common.RestEndpointImpl;
import de.hpi.bpt.argos.persistence.database.PersistenceEntityManager;
import de.hpi.bpt.argos.persistence.model.event.Event;
import de.hpi.bpt.argos.persistence.model.product.ProductState;
import spark.Request;
import spark.Response;
import spark.Service;

import static spark.Spark.halt;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class EventReceiverImpl extends RestEndpointImpl implements EventReceiver {

    /**
     * {@inheritDoc}
     */
	@Override
	public void setup(ResponseFactory responseFactory, PersistenceEntityManager entityManager, Service sparkService) {
		super.setup(responseFactory, entityManager, sparkService);
		sparkService.post(EventReceiver.getReceiveEventBaseUri(), this::receiveEvent);
		sparkService.post(EventReceiver.getReceiveStatusUpdateEventBaseUri(), this::receiveStatusUpdateEvent);
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public String receiveEvent(Request request, Response response) {
		logInfoForReceivedRequest(request);

		long eventTypeId = inputValidation.validateLong(
				request.params(EventTypeEndpoint.getEventTypeIdParameter(false)),
				(Long input) -> input > 0);

		Event event = entityManager.createEvent(eventTypeId, request.body());

		if (event == null) {
			logger.error("cannot find event type '" + eventTypeId + "'");
			halt(ResponseFactory.HTTP_ERROR_CODE, "unable to create event");
		}

		logInfoForReceivedEvent(event);
		return responseFactory.finishRequest();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String receiveStatusUpdateEvent(Request request, Response response) {
		logInfoForReceivedRequest(request);

		long productConfigurationId = inputValidation.validateLong(
				request.params(ProductConfigurationEndPoint.getProductConfigurationIdParameter(false)),
				(Long input) -> input > 0);
		ProductState newState = inputValidation.validateEnum(
				ProductState.class,
				request.params(ProductEndpoint.getNewProductStatusParameter(false)));

		if (newState == null) {
			halt(ResponseFactory.HTTP_ERROR_CODE, "unable to parse new status");
			return "";
		}

		Event event = entityManager.createStatusUpdateEvent(productConfigurationId, newState, request.body());

		if (event == null) {
			logger.error("unable to update product configuration '" + productConfigurationId + "' to state '" + newState.toString() + "'");
			halt(ResponseFactory.HTTP_ERROR_CODE, "unable to create event");
		}

		return responseFactory.finishRequest();
	}

	/**
	 * This method logs an info when a new event is received.
	 * @param event - the new event
	 */
	protected void logInfoForReceivedEvent(Event event) {
		logger.info(String.format("received event : (event type id) %1$s  (event id) %2$s", event.getEventType().getId(), event.getId()));
	}
}
