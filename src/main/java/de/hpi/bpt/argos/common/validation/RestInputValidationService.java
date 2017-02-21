package de.hpi.bpt.argos.common.validation;

import java.util.function.Function;

/**
 * This interface represents a service object which is used to validate rest parameters.
 */
public interface RestInputValidationService {

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
	 * This method validates the input as an enum that is given as a string.
	 * @param clazz - the enum class, which should contain the value
	 * @param inputValue - the input value to validate
	 * @param <T> - the generic type for the enum class
	 * @return - the enum value
	 */
	<T extends Enum<T>> T validateEnum(Class<T> clazz, String inputValue);
}
