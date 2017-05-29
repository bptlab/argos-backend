package de.hpi.bpt.argos.common;

import de.hpi.bpt.argos.util.HttpStatusCodes;

/**
 * {@inheritDoc}
 * This is a null implementation.
 */
public class NullRestRequestImpl implements RestRequest {

	private int responseCode;
	private String content;
	private String response;

	/**
	 * This constructor initializes the members with default values.
	 * @param responseCode - the response code to return in this request
	 */
	public NullRestRequestImpl(int responseCode) {
		this(responseCode, "null request");
	}

	/**
	 * This constructor initializes this nullRestRequest according to the given parameters.
	 * @param responseCode - the response code to return in this request
	 * @param response - the response text to return in this request
	 */
	public NullRestRequestImpl(int responseCode, String response) {
		this.responseCode = responseCode;
		this.response = response;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMethod(String method) {
		// empty, since it has no effect
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setProperty(String key, String value) {
		// empty, since it has no effect
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getContent() {
		return content;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setContent(String requestContent) {
		content = requestContent;
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
	public String getResponse() {
		return response;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isSuccessful() {
		return getResponseCode() == HttpStatusCodes.SUCCESS;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isHostReachable() {
		return false;
	}
}
