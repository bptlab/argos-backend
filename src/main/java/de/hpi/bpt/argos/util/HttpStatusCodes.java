package de.hpi.bpt.argos.util;

/**
 * This class is a collection of important http status codes.
 */
public final class HttpStatusCodes {

	public static final int SUCCESS = 200;
	public static final int REQUEST_TIMEOUT = 408;

	/**
	 * This constructor hides the public one and hinders anyone to create a concrete instance.
	 */
	private HttpStatusCodes() {
		// this class should not be initialized
	}
}
