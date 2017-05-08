package de.hpi.bpt.argos.util;

/**
 * This class is a collection of important http status codes.
 */
public final class HttpStatusCodes {

	public static final int SUCCESS = 200;

	public static final int BAD_REQUEST = 400;
	public static final int FORBIDDEN = 403;
	public static final int NOT_FOUND = 404;
	public static final int REQUEST_TIMEOUT = 408;

	public static final int ERROR = 500;

	/**
	 * This constructor hides the public one and hinders anyone to create a concrete instance.
	 */
	private HttpStatusCodes() {
		// this class should not be initialized
	}
}
