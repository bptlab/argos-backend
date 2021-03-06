package de.hpi.bpt.argos.util;

import de.hpi.bpt.argos.util.performance.WatchImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.HaltException;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Arrays;
import java.util.EnumSet;
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
	public <T extends Enum<T>> T validateEnum(Class<T> clazz, String inputValue) {
		try {
			for (T value : EnumSet.allOf(clazz)) {
				if (value.name().equals(inputValue)) {
					return value;
				}
			}
			throw new InputValidationException(inputValue, "Enum<" + clazz.getName() + ">");

		} catch (Exception e) {
			LoggerUtilImpl.getInstance().error(logger, e.getMessage(), e);
			halt(HttpStatusCodes.BAD_REQUEST, e.getMessage());
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean validateBoolean(String inputValue) {
		try {
			return Boolean.parseBoolean(inputValue);
		} catch (Exception e) {
			LoggerUtilImpl.getInstance().error(logger, e.getMessage(), e);
			halt(HttpStatusCodes.BAD_REQUEST, e.getMessage());
		}

		return false;
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
		long startMs = System.currentTimeMillis();
		logReceivedRequest(logger, request);

		WatchImpl.measure(String.format("execute request: [%1$s] '%2$s'", request.requestMethod(), request.uri()),
				() -> executeRequest(request, response, route));

		logSendingResponse(logger, request, response.status(), response.body(), System.currentTimeMillis() - startMs);
		WatchImpl.printResult(logger);

		return response.body();
	}

	/**
	 * This method actually executes the executeRequest-method.
	 * @param request - the spark request
	 * @param response - the spark response
	 * @param route - the route to execute
	 */
	private void executeRequest(Request request, Response response, Route route) {
		try {
			response.body((String) route.handle(request, response));
		} catch (HaltException e) {
			LoggerUtilImpl.getInstance().error(logger, String.format("halt while executing request: '%1$s' -> '%2$s'", request.uri(), e.body()), e);
			response.status(e.statusCode());
			response.body(e.body());
		} catch (Exception e) {
			LoggerUtilImpl.getInstance().error(logger, String.format("error while executing request: '%1$s'", request.uri()), e);
			response.status(HttpStatusCodes.ERROR);
			response.body(e.getMessage());
		}

		if (response.status() != HttpStatusCodes.SUCCESS || response.body() == null) {
			response.type("text/plain");
		}

		if ("application/json".equalsIgnoreCase(response.type()) && (response.body() == null || response.body().isEmpty())) {
			response.body("{}");
		} else if (response.body() == null) {
			response.body("");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void logReceivedRequest(Logger logger, Request request) {
		logger.info(String.format("%1$s\t->\t[%2$s]\t'%3$s' -> %4$d bytes of content",
				request.ip(),
				request.requestMethod(),
				request.uri(),
				request.contentLength()));
		logger.trace(String.format("request body: '%1$s'", request.body()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void logSendingResponse(Logger logger, Request request, int responseStatus, String responseMessage, long responseTimeInMs) {
		String message = "";

		if (responseMessage != null) {
			message = responseMessage;
		}

		logger.info(String.format("%1$s\t<-\t[%2$s]\t'%3$s' -> %4$d -> %5$d bytes of response after %6$d ms",
				request.ip(),
				request.requestMethod(),
				request.uri(),
				responseStatus,
				message.length(),
				responseTimeInMs));
		logger.trace(String.format("response body: '%1$s'", message));
	}
}
