package de.hpi.bpt.argos.util;

/**
 * This exception is thrown whenever an inputValidation fails.
 */
public class InputValidationException extends Exception {
    private final String inputValue;
    private final String expectedInputType;

    /**
     * This constructor initializes all members.
     * @param inputValue - the input value
     * @param expectedInputType - the expected input type (e.g. Integer)
     */
    public InputValidationException(String inputValue, String expectedInputType) {
        this.inputValue = inputValue;
        this.expectedInputType = expectedInputType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        return String.format("cannot cast '%1$s' to %2$s", inputValue, expectedInputType);
    }
}
