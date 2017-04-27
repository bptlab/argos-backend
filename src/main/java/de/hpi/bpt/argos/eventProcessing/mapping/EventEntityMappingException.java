package de.hpi.bpt.argos.eventProcessing.mapping;

/**
 * This exception is thrown whenever an eventEntityMapping fails.
 */
public class EventEntityMappingException extends Exception {

	private final String reason;

	/**
	 * This constructor sets the reason for this exception.
	 * @param reason - the reason to be set
	 */
	public EventEntityMappingException(String reason) {
		this.reason = reason;
	}

	/**
	 * This method returns the reason for the exception.
	 * @return - the reason for the exception
	 */
	public String getReason() {
		return reason;
	}

}
