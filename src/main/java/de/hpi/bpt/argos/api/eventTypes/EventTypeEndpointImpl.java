package de.hpi.bpt.argos.api.eventTypes;

import de.hpi.bpt.argos.api.response.ResponseFactory;
import de.hpi.bpt.argos.common.RestEndpointImpl;
import de.hpi.bpt.argos.persistence.database.PersistenceEntityManager;
import spark.Request;
import spark.Response;
import spark.Service;


/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class EventTypeEndpointImpl extends RestEndpointImpl implements EventTypeEndpoint {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setup(ResponseFactory responseFactory, PersistenceEntityManager entityManager, Service sparkService) {
		super.setup(responseFactory, entityManager, sparkService);
		sparkService.get(EventTypeEndpoint.getEventTypesBaseUri(), this::getEventTypes);
		sparkService.get(EventTypeEndpoint.getEventTypeBaseUri(), this::getEventType);
		sparkService.post(EventTypeEndpoint.getCreateEventTypeBaseUri(), this::createEventType);
		sparkService.delete(EventTypeEndpoint.getDeleteEventTypeBaseUri(), this::deleteEventType);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEventTypes(Request request, Response response) {
		logInfoForReceivedRequest(request);

		String json = responseFactory.getAllEventTypes();

		logInfoForSendingResponse(request);
		return json;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEventType(Request request, Response response) {
		logInfoForReceivedRequest(request);

		long eventTypeId = inputValidation.validateLong(
				request.params(EventTypeEndpoint.getEventTypeIdParameter(false)),
				(Long input) -> input > 0);
		String json = responseFactory.getEventType(eventTypeId);

		logInfoForSendingResponse(request);
		return json;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String createEventType(Request request, Response response) {
		logInfoForReceivedRequest(request);

		responseFactory.createEventType(request.body());

		logInfoForSendingResponse(request);
		return responseFactory.finishRequest();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String deleteEventType(Request request, Response response) {
		logInfoForReceivedRequest(request);

		long eventTypeId = inputValidation.validateLong(
				request.params(EventTypeEndpoint.getEventTypeIdParameter(false)),
				(Long input) -> input > 0);
		String json = responseFactory.deleteEventType(eventTypeId);

		logInfoForSendingResponse(request);

		if (json.length() == 0) {
			// successful response
			return responseFactory.finishRequest();
		} else {
			response.status(ResponseFactory.httpErrorCode);
			return json;
		}
	}
}
