package de.hpi.bpt.argos.api.eventQuery;

import de.hpi.bpt.argos.api.eventType.EventTypeEndpoint;
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
public class EventQueryEndpointImpl extends RestEndpointImpl implements EventQueryEndpoint {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setup(ResponseFactory responseFactory, PersistenceEntityManager entityManager, Service sparkService) {
		super.setup(responseFactory, entityManager, sparkService);
		sparkService.post(EventQueryEndpoint.getUpdateEventQueryBaseUri(), this::updateEventQuery);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String updateEventQuery(Request request, Response response) {
		logInfoForReceivedRequest(request);

		long eventTypeId = inputValidation.validateLong(
				request.params(EventTypeEndpoint.getEventTypeIdParameter(false)),
				(Long input) -> input > 0);

		responseFactory.updateEventQuery(eventTypeId, request.body());

		logInfoForSendingResponse(request);
		return responseFactory.finishRequest();
	}


}
