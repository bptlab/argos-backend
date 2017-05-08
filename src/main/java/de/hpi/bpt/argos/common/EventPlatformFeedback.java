package de.hpi.bpt.argos.common;

/**
 * This interface represents feedback which came from the event platform after a request was sent.
 */
public interface EventPlatformFeedback {

	/**
	 * This method indicates whether the request was successful or not.
	 * @return - true, if the request succeeded
	 */
	boolean isSuccessful();

	/**
	 * This method sets the state of the request.
	 * @param succeeded - the state to be set
	 */
	void setSuccessful(boolean succeeded);

	/**
	 * This method returns the response text of the event platform.
	 * @return - the response text of the event platform
	 */
	String getResponseText();

	/**
	 * This method sets the response text of the event platform.
	 * @param responseText - the response text to be set
	 */
	void setResponseText(String responseText);
}
