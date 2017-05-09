package de.hpi.bpt.argos.core;

/**
 * This exception is thrown whenever the argos instance is not running, but this would be required.
 */
public class ArgosNotRunningException extends Exception {

	/**
	 * This constructor initializes the super class with the corresponding message.
	 * @param message - the message to set
	 */
	public ArgosNotRunningException(String message) {
		super(message);
	}
}
