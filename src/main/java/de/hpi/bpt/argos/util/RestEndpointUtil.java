package de.hpi.bpt.argos.util;

import org.slf4j.Logger;
import spark.Request;
import spark.Response;

/**
 * This interface helps to set parameters in URIs.
 */
public interface RestEndpointUtil {

    /**
     * This method returns a parameter included with a a prefix or not.
     * @param parameterName - the parameter to update
     * @param includePrefix - if the prefix should be included
     * @return - the updated parameter
     */
    String getParameter(String parameterName, boolean includePrefix);

	/**
	 * This method logs an info, whenever a request was received.
	 * @param logger - the logger to use for logging
	 * @param request - the received request
	 */
	void logReceivedRequest(Logger logger, Request request);

	/**
	 * This method logs an info, whenever a request gets answered.
	 * @param logger - the logger to use for logging
	 * @param request - the received request
	 * @param response - the generated response
	 */
    void logSendingResponse(Logger logger, Request request, Response response);
}
