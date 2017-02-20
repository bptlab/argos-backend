package de.hpi.bpt.argos.common;

import de.hpi.bpt.argos.api.response.ResponseFactory;
import de.hpi.bpt.argos.properties.PropertyEditor;
import de.hpi.bpt.argos.properties.PropertyEditorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class RestRequestImpl implements RestRequest {
	protected static final Logger logger = LoggerFactory.getLogger(RestRequestImpl.class);
	protected static final PropertyEditor propertyEditor = new PropertyEditorImpl();

	protected HttpURLConnection connection;
	protected String content;
	protected String response;

	/**
	 * Constructor for RestRequest, instantiates new connection.
	 * @param url - URL object that should be requested
	 * @throws IOException - throws IOException in case of failure (e.g. network problems)
	 */
	public RestRequestImpl(URL url) throws IOException {
		connection = (HttpURLConnection)url.openConnection();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMethod(String method) {
		try {
			this.connection.setRequestMethod(method);
		} catch (ProtocolException e) {
			logger.error("cannot set request method to '" + method + "'", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setProperty(String key, String value) {
		this.connection.setRequestProperty(key, value);
	}

	@Override
	public String getContent() {
		return content;
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public void setContent(String requestContent) {

		try {
			connection.setDoOutput(true);

			DataOutputStream outputStream;

			outputStream = new DataOutputStream(connection.getOutputStream());
			outputStream.writeBytes(requestContent);
			outputStream.flush();
			outputStream.close();

			content = requestContent;
		} catch (IOException e) {
			logger.error("Cannot set content of rest request", e);
		}
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public int getResponseCode() {

		try {
			return connection.getResponseCode();
		} catch (IOException e) {
			logger.error("Cannot get response code of rest request", e);
			return 0;
		}
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public String getResponse() {

		if (response != null) {
			return response;
		}

		BufferedReader responseReader;
		StringBuilder restResponse = new StringBuilder();

		try {
			responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String responseString;

			while ((responseString = responseReader.readLine()) != null) {
				restResponse.append(responseString);
			}
			responseReader.close();

		} catch (IOException e) {
			logger.error("Cannot read response of rest request", e);
			return RestRequest.getErrorResponse();
		}

		this.response = restResponse.toString();
		return this.response;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public boolean isSuccessful() {
		return getResponseCode() == ResponseFactory.getHttpSuccessCode();
	}
}
