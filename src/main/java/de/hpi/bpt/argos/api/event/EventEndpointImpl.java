package de.hpi.bpt.argos.api.event;

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
public class EventEndpointImpl extends RestEndpointImpl implements EventEndpoint {

    /**
     * {@inheritDoc}
     */
    @Override
    public void setup(ResponseFactory responseFactory, PersistenceEntityManager entityManager, Service sparkService) {
        super.setup(responseFactory, entityManager, sparkService);
        sparkService.get(EventEndpoint.getSingleEventBaseUri(), this::getSingleEvent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSingleEvent(Request request, Response response) {
        logInfoForReceivedRequest(request);

        long eventId = inputValidation.validateLong(request.params("eventId"), (Long input) -> input > 0);
        String json = responseFactory.getEvent(eventId);

        logInfoForSendingResponse(request);
        return json;
    }
}
