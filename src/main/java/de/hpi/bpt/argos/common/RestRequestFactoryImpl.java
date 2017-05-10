package de.hpi.bpt.argos.common;


import de.hpi.bpt.argos.core.Argos;
import de.hpi.bpt.argos.util.HttpStatusCodes;
import de.hpi.bpt.argos.util.LoggerUtilImpl;
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
public final class RestRequestFactoryImpl implements RestRequestFactory {
	private static final Logger logger = LoggerFactory.getLogger(RestRequestFactoryImpl.class);
	private static final String DEFAULT_CONTENT_TYPE = "application/json";
	private static final String DEFAULT_ACCEPT_TYPE = "text/plain";

	private static RestRequestFactory instance;

	/**
	 * This constructor hides the default public one to implement the singleton pattern.
	 */
	private RestRequestFactoryImpl() {

	}

	/**
	 * This method returns the singleton instance of this class.
	 * @return - the singleton instance of this class
	 */
	public static RestRequestFactory getInstance() {
		if (instance == null) {
			instance = new RestRequestFactoryImpl();
		}

		return instance;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RestRequest createRequest(String host, String uri, String requestMethod, String contentType, String acceptType) {
		RestRequest request = createBasicRequest(host, uri);

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
	private RestRequest createBasicRequest(String host, String uri) {
		URL requestURL;
		RestRequest request;

		try {
			requestURL = new URL(host + uri);
		} catch (MalformedURLException e) {
			LoggerUtilImpl.getInstance().error(logger, "cannot create rest request: url malformed", e);
			return new NullRestRequestImpl(HttpStatusCodes.REQUEST_TIMEOUT);
		}

		if (!isReachable(requestURL)) {
			if (Argos.isInTestMode()) {
				return new NullRestRequestImpl(HttpStatusCodes.SUCCESS);
			}
			else {
				return new NullRestRequestImpl(HttpStatusCodes.REQUEST_TIMEOUT);
			}
		}

		try {
			request = new RestRequestImpl(requestURL);
		} catch (IOException e) {
			LoggerUtilImpl.getInstance().error(logger, "cannot create rest request", e);
			return new NullRestRequestImpl(HttpStatusCodes.REQUEST_TIMEOUT);
		}

		return request;
	}

	/**
	 * This connection checks whether a host is reachable.
	 * @param host - the host URL to connect to
	 * @return - true, if host is reachable
	 */
	private boolean isReachable(URL host) {
		URLConnection hostConnection;
		try {
			hostConnection = host.openConnection();
			hostConnection.connect();

			return true;
		} catch (IOException e) {
			LoggerUtilImpl.getInstance().error(logger, String.format("unable to reach host: '%1$s'", host.toString()), e);
			return false;
		}
	}
}
