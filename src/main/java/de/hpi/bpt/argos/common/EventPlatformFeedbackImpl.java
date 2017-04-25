package de.hpi.bpt.argos.common;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class EventPlatformFeedbackImpl implements EventPlatformFeedback {

	private boolean succeeded = false;
	private String responseText = "";

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
	 * This constructor creates an instance of this class, based on a restRequest.
	 * @param baseRequest
	 */
	public EventPlatformFeedbackImpl(RestRequest baseRequest) {
		this(baseRequest.getResponse(), baseRequest.isSuccessful());
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
