package de.hpi.bpt.argos.common.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

import static spark.Spark.halt;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class RestInputValidationServiceImpl implements RestInputValidationService {
	protected Logger logger = LoggerFactory.getLogger(RestInputValidationServiceImpl.class);

	protected static final int HTTP_ERROR = 500;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int validateInteger(String inputValue, Function<Integer, Boolean> validateInputResult) {
		try {
			if (!validateInputResult.apply(Integer.parseInt(inputValue))) {
				throw new Exception("input did not pass validation");
			}
		} catch (Exception e) {
			logErrorWhileInputValidation(inputValue, "Integer");
			halt(HTTP_ERROR, e.getMessage());
		}
		return Integer.parseInt(inputValue);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long validateLong(String inputValue, Function<Long, Boolean> validateInputResult) {
		try {
			if (validateInputResult.apply(Long.parseLong(inputValue))) {
				throw new Exception("input did not pass validation");
			}
		} catch (Exception e) {
			logErrorWhileInputValidation(inputValue, "Long");
			halt(HTTP_ERROR, e.getMessage());
		}
		return Long.parseLong(inputValue);
	}

	/**
	 * This method logs a string on error level.
	 * @param head - string to be logged
	 */
	protected void logError(String head) {
		logger.error(head);
	}

	/**
	 * This methods logs an error, if the input validation can't cast the inputValue eventType.
	 * @param inputValue - inputValue from url
	 * @param expectedInputType - expected inputType (must be a Java Class
	 */
	protected void logErrorWhileInputValidation(String inputValue, String expectedInputType) {
		logError(String.format("tried to cast (input) \"%1$s\" to %2$s", inputValue, expectedInputType));
	}
}
