package de.hpi.bpt.argos.common;

public abstract class RestEndpointImpl implements RestEndpoint {
	protected static String REQUEST_HANDLED = "request handled.";

	@Override
	public String finishRequest() {
		return REQUEST_HANDLED;
	}
}
