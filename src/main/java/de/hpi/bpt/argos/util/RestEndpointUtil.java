package de.hpi.bpt.argos.util;

import org.slf4j.Logger;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.List;
import java.util.function.Function;

/**
 * This interface helps to set parameters in URIs.
 */
public interface RestEndpointUtil {

	/**
	 * This method validates the input as an integer that is given as a string with a generic validation function.
	 * @param inputValue - string to be tested
	 * @param validateInputResult - function to be tested on the parsed integer as validation
	 * @return - returns the integer representation of the input value
	 */
	int validateInteger(String inputValue, Function<Integer, Boolean> validateInputResult);

	/**
	 * This method validates the input as a long that is given as a string with a generic validation function.
	 * @param inputValue - string to be tested
	 * @param validateInputResult - function to be tested on the parsed long as validation
	 * @return - returns the long representation of the input value
	 */
	long validateLong(String inputValue, Function<Long, Boolean> validateInputResult);

	/**
	 * This method validates the input as a plus-separated string with a generic validation function.
	 * @param inputValue - string to be tested
	 * @param validateInputResult - function to be tested on the parsed string list as validation
	 * @return - returns the lone strings of the input value
	 */
	List<String> validateListOfString(String inputValue, Function<String, Boolean> validateInputResult);

	/**
	 * This method validates the input as an enum that is given as a string.
	 * @param clazz - the enum class, which should contain the value
	 * @param inputValue - the input value to validate
	 * @param <T> - the generic type for the enum class
	 * @return - the enum value
	 */
	<T extends Enum<T>> T validateEnum(Class<T> clazz, String inputValue);

	/**
	 * This method validates the input as boolean that is given as a string.
	 * @param inputValue - the input value to validate
	 * @return - the boolean
	 */
	boolean validateBoolean(String inputValue);

	/**
     * This method returns a parameter included with a a prefix or not.
     * @param parameterName - the parameter to update
     * @param includePrefix - if the prefix should be included
     * @return - the updated parameter
     */
    String getParameter(String parameterName, boolean includePrefix);

	/**
	 * This method executes a route and catching all exceptions for easier logging.
	 * @param logger - the logger to use
	 * @param request - the spark request
	 * @param response - the spark response
	 * @param route - the route to execute
	 * @return - the response to the request
	 */
	String executeRequest(Logger logger, Request request, Response response, Route route);

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
	 * @param responseStatus - the http status code of the response
	 * @param responseMessage - the generated response
	 * @param responseTimeInMs - time it took to answer the request
	 */
    void logSendingResponse(Logger logger, Request request, int responseStatus, String responseMessage, long responseTimeInMs);
}
