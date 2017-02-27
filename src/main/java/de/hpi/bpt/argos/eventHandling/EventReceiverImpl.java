package de.hpi.bpt.argos.eventHandling;

import de.hpi.bpt.argos.api.eventTypes.EventTypeEndpoint;
import de.hpi.bpt.argos.api.product.ProductEndpoint;
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
			halt(ResponseFactory.httpNotFoundCode, "cannot find event type");
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

		int externalProductId = inputValidation.validateInteger(
				request.params(ProductEndpoint.getProductIdParameter(false)),
				(Integer input) -> input > 0);
		ProductState newState = inputValidation.validateEnum(
				ProductState.class,
				request.params(ProductEndpoint.getNewProductStatusParameter(false)));

		if (newState == null) {
			halt(ResponseFactory.httpErrorCode, "unable to parse new status");
			return "";
		}

		Event event = entityManager.createStatusUpdateEvent(externalProductId, newState, request.body());

		if (event == null) {
			logger.error("unable to update product '" + externalProductId + "' to state '" + newState.toString() + "'");
			halt(ResponseFactory.httpNotFoundCode, "unable to update product state");
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
