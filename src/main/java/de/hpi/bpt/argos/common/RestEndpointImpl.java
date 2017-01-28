package de.hpi.bpt.argos.common;

import spark.Request;
import spark.Response;
import spark.Service;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public abstract class RestEndpointImpl implements RestEndpoint {
	protected static final String REQUEST_HANDLED = "request handled.";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String finishRequest() {
		return REQUEST_HANDLED;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void before(Request request, Response response) {
		response.header("Access-Control-Allow-Origin", "*");
		response.header("Access-Control-Request-Method", "*");
		response.header("Access-Control-Allow-Headers", "*");
	}
}
