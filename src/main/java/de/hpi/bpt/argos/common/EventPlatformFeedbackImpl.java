package de.hpi.bpt.argos.common;

import de.hpi.bpt.argos.util.HttpStatusCodes;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class EventPlatformFeedbackImpl implements EventPlatformFeedback {

	private boolean succeeded = false;
	private String responseText = "";
	private int responseCode = HttpStatusCodes.ERROR;

	/**
	 * This constructor initializes all members according to the parameters.
	 * @param responseText - the response text from the event platform
	 * @param responseCode - the response code of the initial request
	 */
	public EventPlatformFeedbackImpl(String responseText, int responseCode) {
		this.responseText = responseText;
		this.responseCode = responseCode;
		succeeded = responseCode == HttpStatusCodes.SUCCESS;
	}

	/**
	 * This constructor creates an instance of this class, based on a restRequest.
	 * @param baseRequest - the base restRequest to create this feedback from
	 */
	public EventPlatformFeedbackImpl(RestRequest baseRequest) {
		this(baseRequest.getResponse(), baseRequest.getResponseCode());
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getResponseCode() {
		return responseCode;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
}
