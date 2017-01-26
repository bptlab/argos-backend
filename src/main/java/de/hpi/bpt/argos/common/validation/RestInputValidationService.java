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
	 * @return - returns a
	 */
	int validateInteger(String inputValue, Function<Integer, Boolean> validateInputResult);
}
