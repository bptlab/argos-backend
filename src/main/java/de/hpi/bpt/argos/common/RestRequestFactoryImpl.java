package de.hpi.bpt.argos.common;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class RestRequestFactoryImpl implements RestRequestFactory {
	private static final Logger logger = LoggerFactory.getLogger(RestRequestFactoryImpl.class);
	protected static final String DEFAULT_CONTENT_TYPE = "application/json";
	protected static final String DEFAULT_ACCEPT_TYPE = "text/plain";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RestRequest createRequest(String host, String uri, String requestMethod, String contentType, String acceptType) {
		RestRequest request = createBasicRequest(host, uri);

		if (request == null) {
			return null;
		}

		request.setMethod(requestMethod);
		request.setProperty("Content-Type", contentType);
		request.setProperty("Accept", acceptType);

		return request;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RestRequest createPostRequest(String host, String uri, String contentType, String acceptType) {
		return createRequest(host, uri, "POST", contentType, acceptType);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RestRequest createPostRequest(String host, String uri) {
		return createPostRequest(host, uri, DEFAULT_CONTENT_TYPE, DEFAULT_ACCEPT_TYPE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RestRequest createGetRequest(String host, String uri, String acceptType) {
		return createRequest(host, uri, "GET", DEFAULT_CONTENT_TYPE, acceptType);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RestRequest createGetRequest(String host, String uri) {
		return createGetRequest(host, uri, DEFAULT_ACCEPT_TYPE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RestRequest createDeleteRequest(String host, String uri) {
		return createDeleteRequest(host, uri, DEFAULT_CONTENT_TYPE, DEFAULT_ACCEPT_TYPE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RestRequest createDeleteRequest(String host, String uri, String contentType, String acceptType) {
		return createRequest(host, uri, "DELETE", contentType, acceptType);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RestRequest createPutRequest(String host, String uri) {
		return createPutRequest(host, uri, DEFAULT_CONTENT_TYPE, DEFAULT_ACCEPT_TYPE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RestRequest createPutRequest(String host, String uri, String contentType, String acceptType) {
		return createRequest(host, uri, "PUT", contentType, acceptType);
	}

	/**
	 * This method creates a basic RestRequest object and sets host and uri.
	 * @param host - host as a string to be set
	 * @param uri - uri as a string to be set
	 * @return - returns a RestRequest object to be worked with later on
	 */
	protected RestRequest createBasicRequest(String host, String uri) {

		URL requestURL;
		RestRequest request;

		try {
			requestURL = new URL(host + uri);
		} catch (MalformedURLException e) {
			logExceptionInRequestCreation(e);
			return new NullRestRequestImpl();
		}

		if (!isReachable(requestURL)) {
			return new NullRestRequestImpl();
		}

		try {
			request = new RestRequestImpl(requestURL);
		} catch (IOException e) {
			logExceptionInRequestCreation(e);
			return new NullRestRequestImpl();
		}

		return request;
	}

	/**
	 * This connection checks whether a host is reachable.
	 * @param host - the host URL to connect to
	 * @return - true if host is reachable
	 */
	protected boolean isReachable(URL host) {
		URLConnection hostConnection = null;
		try {
			hostConnection = host.openConnection();
			hostConnection.connect();

			return true;
		} catch (IOException e) {
			logger.error("unable to reach host at '" + host.toString() + "'", e);
			return false;
		}
	}

	/**
     * This method logs an exception on error level.
     * @param head - string message to be logged
     * @param exception - throwable exception to be logged
     */
	protected void logException(String head, Throwable exception) {
		logger.error(head, exception.toString());
	}

    /**
     * This method logs an exception while creating the request.
     * @param exception - throwable exception to be logged
     */
	protected void logExceptionInRequestCreation(Throwable exception) {
		logException("can't create RestRequest: ", exception);
	}
}
