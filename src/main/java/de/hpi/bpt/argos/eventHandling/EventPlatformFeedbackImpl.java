package de.hpi.bpt.argos.eventHandling;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class EventPlatformFeedbackImpl implements EventPlatformFeedback {

	protected boolean succeeded = false;
	protected String responseText = "";

	/**
	 * This constructor initializes all members according to the parameters.
	 * @param responseText - the response text from the event platform
	 * @param succeeded - the status of the event platform response
	 */
	public EventPlatformFeedbackImpl(String responseText, boolean succeeded) {
		this.responseText = responseText;
		this.succeeded = succeeded;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isSuccessful() {
		return succeeded;
	}

	/**
	 * {@inheritDoc}
	 * @param succeeded
	 */
	@Override
	public void setSuccessful(boolean succeeded) {
		this.succeeded = succeeded;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getResponseText() {
		return responseText;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setResponseText(String responseText) {
		this.responseText = responseText;
	}
}
