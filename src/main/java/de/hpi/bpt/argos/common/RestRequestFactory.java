package de.hpi.bpt.argos.common;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestRequestFactory implements IRestRequestFactory {
	private static final Logger logger = LoggerFactory.getLogger(RestRequestFactory.class);
	protected static final String DEFAULT_CONTENT_TYPE = "application/json";
	protected static final String DEFAULT_ACCEPT_TYPE = "text/plain";

	@Override
	public IRestRequest createPostRequest(String host, String uri, String contentType, String acceptType) {
		IRestRequest request = createBasicRequest(host, uri);

		if (request == null) {
			return null;
		}

		try {
			request.getConnection().setRequestMethod("POST");
		} catch (ProtocolException e) {
			logExceptionInRequestCreation(e);
			return null;
		}

		request.getConnection().setRequestProperty("Content-Type", contentType);
		request.getConnection().setRequestProperty("Accept", acceptType);

		return request;
	}

	@Override
	public IRestRequest createPostRequest(String host, String uri) {
		return createPostRequest(host, uri, DEFAULT_CONTENT_TYPE, DEFAULT_ACCEPT_TYPE);
	}

	private IRestRequest createBasicRequest(String host, String uri) {
		URL requestURL;
		IRestRequest request;

		try {
			requestURL = new URL(host + uri);
		} catch (MalformedURLException e) {
			logExceptionInRequestCreation(e);
			return null;
		}

		try {
			request = new RestRequest(requestURL);
		} catch (IOException e) {
			logExceptionInRequestCreation(e);
			return null;
		}

		return request;
	}

	private void logException(String head, Throwable exception) {
		logger.error(head, exception.toString());
	}

	private void logExceptionInRequestCreation(Throwable exception) {
		logException("can't create RestRequest: ", exception);
	}
}
