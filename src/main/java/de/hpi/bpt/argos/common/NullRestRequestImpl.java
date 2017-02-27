package de.hpi.bpt.argos.common;

import de.hpi.bpt.argos.api.response.ResponseFactory;

/**
 * {@inheritDoc}
 * This is a null implementation.
 */
public class NullRestRequestImpl implements RestRequest {

	protected String content;
	protected String response;

	/**
	 * This constructor initializes the members with default values.
	 */
	public NullRestRequestImpl() {
		response = "null request";
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
		return ResponseFactory.HTTP_SUCCESS_CODE;
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
		return true;
	}
}
