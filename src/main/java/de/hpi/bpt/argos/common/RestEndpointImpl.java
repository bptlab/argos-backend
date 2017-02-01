package de.hpi.bpt.argos.common;

import de.hpi.bpt.argos.api.response.ResponseFactory;
import de.hpi.bpt.argos.common.validation.RestInputValidationService;
import de.hpi.bpt.argos.common.validation.RestInputValidationServiceImpl;
import de.hpi.bpt.argos.persistence.database.PersistenceEntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Service;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public abstract class RestEndpointImpl implements RestEndpoint {
	protected static final Logger logger = LoggerFactory.getLogger(RestEndpointImpl.class);
	protected static final String REQUEST_HANDLED = "request handled.";

	protected RestInputValidationService inputValidation;
	protected ResponseFactory responseFactory;
	protected PersistenceEntityManager entityManager;

	/**
	 * This constructor initializes all members with default values.
	 */
	public RestEndpointImpl() {
		inputValidation = new RestInputValidationServiceImpl();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setup(ResponseFactory responseFactory, PersistenceEntityManager entityManager, Service sparkService) {
		this.responseFactory = responseFactory;
		this.entityManager = entityManager;
	}

	/**
	 * This method logs a string on info level.
	 * @param head - string to be logged
	 */
	protected void logInfo(String head) {
		logger.info(head);
	}

	/**
	 * This method logs a string on error level.
	 * @param head - string to be logged
	 */
	protected void logError(String head) {
		logger.error(head);
	}

	/**
	 * This method logs an info, if a request is received via url and the associated method was called.
	 * @param request - Spark request object to be logged
	 */
	protected void logInfoForReceivedRequest(Request request) {
		logInfo("received request : (uri) " + request.uri() + "    (body) " + request.body());
	}

	/**
	 * This method logs an info, if a response is send.
	 * @param request - the initial request
	 */
	protected void logInfoForSendingResponse(Request request) {
		logInfo("sending response for request (" + request.uri() + ")");
	}
}
