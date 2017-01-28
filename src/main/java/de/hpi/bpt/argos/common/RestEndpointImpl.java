package de.hpi.bpt.argos.common;

import spark.Request;
import spark.Response;
import spark.Service;

import static spark.Spark.before;
import static spark.Spark.options;

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

	@Override
	public void enableCORS() {
		options("/*", (request, response) -> {

			String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
			if (accessControlRequestHeaders != null) {
				response.header("Access-Control-Allow-Headers",
						accessControlRequestHeaders);
			}

			String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
			if (accessControlRequestMethod != null) {
				response.header("Access-Control-Allow-Methods",
						accessControlRequestMethod);
			}

			return "OK";
		});

		before((request, response) -> {
			response.header("Access-Control-Allow-Origin", "*");
		});
	}
}
