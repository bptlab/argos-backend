package de.hpi.bpt.argos.eventHandling;

import de.hpi.bpt.argos.api.response.ResponseFactory;
import de.hpi.bpt.argos.common.RestEndpointImpl;
import de.hpi.bpt.argos.persistence.database.PersistenceEntityManager;
import de.hpi.bpt.argos.persistence.model.event.Event;
import de.hpi.bpt.argos.persistence.model.event.type.EventType;
import spark.Request;
import spark.Response;
import spark.Service;

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
		sparkService.post(EventReceiver.getPostEventBaseUri(), this::receiveEvent);
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public String receiveEvent(Request request, Response response) {
		logInfoForReceivedRequest(request);

		long eventTypeId = inputValidation.validateLong(request.params("eventTypeId"), (Long input) -> input > 0);

		EventType eventType = entityManager.getEventType(eventTypeId);
		Event event = entityManager.createEvent(eventType, request.body());

		logInfoForReceivedEvent(event);
		return responseFactory.finishRequest();
	}

	/**
	 * This method logs an info when a new event is received.
	 * @param event - the new event
	 */
	protected void logInfoForReceivedEvent(Event event) {
		logger.info(String.format("received event : (event type id) %1$s  (event id) %2$s", event.getEventType().getId
				(), event.getId()));
	}
}
