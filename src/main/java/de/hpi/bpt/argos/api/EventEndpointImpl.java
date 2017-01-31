package de.hpi.bpt.argos.api;

import de.hpi.bpt.argos.api.response.ResponseFactory;
import de.hpi.bpt.argos.api.response.ResponseFactoryImpl;
import de.hpi.bpt.argos.common.RestEndpointImpl;
import de.hpi.bpt.argos.common.validation.RestInputValidationService;
import de.hpi.bpt.argos.common.validation.RestInputValidationServiceImpl;
import de.hpi.bpt.argos.persistence.database.DatabaseConnection;
import spark.Request;
import spark.Response;
import spark.Service;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class EventEndpointImpl extends RestEndpointImpl implements EventEndpoint {
    protected final String GET_SINGLE_EVENT = "/api/event/:eventId";

    protected ResponseFactory responseFactory;
    protected DatabaseConnection databaseConnection;
    protected RestInputValidationService inputValidation;

    public EventEndpointImpl(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
        responseFactory = new ResponseFactoryImpl(databaseConnection);
        inputValidation = new RestInputValidationServiceImpl();
    }

    @Override
    public void setup(Service sparkService) {
        sparkService.get(GET_SINGLE_EVENT, this::getSingleEvent);
    }

    @Override
    public String getSingleEvent(Request request, Response response) {
        int eventId = inputValidation.validateInteger(request.params("eventId"), (Integer input) -> input > 0);
        String json = responseFactory.getSingleEvent(eventId);
        return json;
    }
}
