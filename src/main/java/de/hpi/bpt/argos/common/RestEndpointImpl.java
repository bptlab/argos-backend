package de.hpi.bpt.argos.common;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public abstract class RestEndpointImpl implements RestEndpoint {
	protected static final String REQUEST_HANDLED = "request handled.";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String finishRequest() {
		return REQUEST_HANDLED;
	}
}
