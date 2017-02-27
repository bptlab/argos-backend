package de.hpi.bpt.argos.common.validation;

import de.hpi.bpt.argos.api.response.ResponseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;
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
			halt(ResponseFactory.httpErrorCode, e.getMessage());
		} catch (NumberFormatException e) {
			logger.error(e.getMessage());
			halt(ResponseFactory.httpErrorCode, e.getMessage());
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
			halt(ResponseFactory.httpErrorCode, e.getMessage());
		} catch (NumberFormatException e) {
			logger.error(e.getMessage());
			halt(ResponseFactory.httpErrorCode, e.getMessage());
		}
		return Long.parseLong(inputValue);
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
			logger.error(e.getMessage(), e);
			halt(ResponseFactory.httpErrorCode, e.getMessage());
		}

		return null;
	}
}
