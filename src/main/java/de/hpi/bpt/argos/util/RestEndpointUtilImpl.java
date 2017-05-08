package de.hpi.bpt.argos.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.HaltException;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static spark.Spark.halt;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public final class RestEndpointUtilImpl implements RestEndpointUtil {
	private static final Logger logger = LoggerFactory.getLogger(RestEndpointUtilImpl.class);

	private static RestEndpointUtil instance;

	/**
	 * This constructor hides the default public one to implement the singleton pattern.
	 */
	private RestEndpointUtilImpl() {

	}

	/**
	 * This method returns the singleton instance of this class.
	 * @return - the singleton instance of this class
	 */
	public static RestEndpointUtil getInstance() {
		if (instance == null) {
			instance = new RestEndpointUtilImpl();
		}

		return instance;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int validateInteger(String inputValue, Function<Integer, Boolean> validateInputResult) {
		try {
			if (!validateInputResult.apply(Integer.parseInt(inputValue))) {
				throw new InputValidationException(inputValue, "Integer");
			}
		} catch (InputValidationException | NumberFormatException e) {
			LoggerUtilImpl.getInstance().error(logger, e.getMessage(), e);
			halt(HttpStatusCodes.BAD_REQUEST, e.getMessage());
		}
		return Integer.parseInt(inputValue);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long validateLong(String inputValue, Function<Long, Boolean> validateInputResult) {
		try {
			if (!validateInputResult.apply(Long.parseLong(inputValue))) {
				throw new InputValidationException(inputValue, "Long");
			}
		} catch (InputValidationException | NumberFormatException e) {
			LoggerUtilImpl.getInstance().error(logger, e.getMessage(), e);
			halt(HttpStatusCodes.BAD_REQUEST, e.getMessage());
		}
		return Long.parseLong(inputValue);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> validateListOfString(String inputValue, Function<String, Boolean> validateInputResult) {
		try {
			if (!validateInputResult.apply(inputValue)) {
				throw new InputValidationException(inputValue, "String");
			}
		} catch (InputValidationException | NumberFormatException e) {
			LoggerUtilImpl.getInstance().error(logger, e.getMessage(), e);
			halt(HttpStatusCodes.BAD_REQUEST, e.getMessage());
		}
		String[] splits = inputValue.split("\\s");
		return Arrays.asList(splits);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getParameter(String parameterName, boolean includePrefix) {
		String updatedParameterName = parameterName;
		if (includePrefix) {
			updatedParameterName = ":" + updatedParameterName;
		}
		return updatedParameterName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String executeRequest(Logger logger, Request request, Response response, Route route) {
		logReceivedRequest(logger, request);

		try {
			response.body((String) route.handle(request, response));
		} catch (HaltException e) {
			logSendingResponse(logger, request, e.statusCode(), e.body());
			throw e;
		} catch (Exception e) {
			LoggerUtilImpl.getInstance().error(logger, String.format("error while executing request: '%1$s'", request.uri()), e);
			response.status(HttpStatusCodes.ERROR);
			response.body(e.getMessage());
		}

		logSendingResponse(logger, request, response.status(), response.body());
		if (response.body() == null) {
			return "";
		} else {
			return response.body();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void logReceivedRequest(Logger logger, Request request) {
		logger.info(String.format("received request: '%1$s' -> %2$d bytes of content", request.uri(), request.contentLength()));
		logger.trace(String.format("request body: '%1$s'", request.body()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void logSendingResponse(Logger logger, Request request, int responseStatus, String responseMessage) {
		String message = "";

		if (responseMessage != null) {
			message = responseMessage;
		}

		logger.info(String.format("sending response: '%1$s' -> %2$d -> %3$d bytes of response",
				request.uri(),
				responseStatus,
				message.length()));
		logger.trace(String.format("response body: '%1$s'", message));
	}
}
