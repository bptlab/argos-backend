package de.hpi.bpt.argos.common.validation;

public class InputValidationException extends Exception {
    protected String inputValue;
    protected String expectedInputType;

    public InputValidationException(String inputValue, String expectedInputType) {
        this.inputValue = inputValue;
        this.expectedInputType = expectedInputType;
    }

    @Override
    public String getMessage() {
        return String.format("cannot cast '%1$s' to %2$s", inputValue, expectedInputType);
    }
}
