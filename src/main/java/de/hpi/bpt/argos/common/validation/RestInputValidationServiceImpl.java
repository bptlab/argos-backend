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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int validateInteger(String inputValue, Function<Integer, Boolean> validateInputResult) {
		try {
			if (!validateInputResult.apply(Integer.parseInt(inputValue))) {
				throw new InputValidationException(inputValue, "Integer");
			}
		} catch (InputValidationException e) {
			logger.error(e.getMessage(), e);
			halt(RestInputValidationService.getHttpErrorCode(), e.getMessage());
		} catch (NumberFormatException e) {
			logger.error(e.getMessage());
			halt(RestInputValidationService.getHttpErrorCode(), e.getMessage());
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
		} catch (InputValidationException e) {
			logger.error(e.getMessage(), e);
			halt(RestInputValidationService.getHttpErrorCode(), e.getMessage());
		} catch (NumberFormatException e) {
			logger.error(e.getMessage());
			halt(RestInputValidationService.getHttpErrorCode(), e.getMessage());
		}
		return Long.parseLong(inputValue);
	}
}
