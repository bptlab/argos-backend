package de.hpi.bpt.argos.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;

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
	public void logReceivedRequest(Logger logger, Request request) {
		logger.info(String.format("received request: '%1$s' -> %2$d bytes of content", request.uri(), request.contentLength()));
		logger.trace(String.format("request body: '%1$s'", request.body()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void logSendingResponse(Logger logger, Request request, int responseStatus, String responseMessage) {
		logger.info(String.format("sending response: '%1$s' -> %2$d -> %3$d bytes of response",
				request.uri(),
				responseStatus,
				responseMessage.length()));
		logger.trace(String.format("response body: '%1$s'", responseMessage));
	}
}
