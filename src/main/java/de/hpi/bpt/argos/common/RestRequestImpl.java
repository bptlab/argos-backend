package de.hpi.bpt.argos.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class RestRequestImpl implements RestRequest {
	protected static final Logger logger = LoggerFactory.getLogger(RestRequestImpl.class);
	protected static final int HTTP_SUCCESS_CODE = 200;

	public static final String ERROR_RECEIVING_RESPONSE = "error while receiving response";

	protected HttpURLConnection connection;
	protected String content;
	protected String response;

	/**
	 * Constructor for RestRequest, instantiates new connection.
	 * @param url - URL object that should be requested
	 * @throws IOException - throws IOException in case of failure (e.g. network problems)
	 */
	public RestRequestImpl(URL url) throws IOException {
		connection = (HttpURLConnection) url.openConnection();
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public HttpURLConnection getConnection() {
		return connection;
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
		connection.setDoOutput(true);

		DataOutputStream outputStream;
		try {
			outputStream = new DataOutputStream(connection.getOutputStream());
			outputStream.writeBytes(requestContent);
			outputStream.flush();
			outputStream.close();

			content = requestContent;
		} catch (IOException e) {
			logExceptionInContentSetting(e);
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
			logExceptionWhileReceivingResponse(e);
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

		try {
			responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		} catch (IOException e) {
			logExceptionWhileReceivingResponse(e);
			return ERROR_RECEIVING_RESPONSE;
		}

		String responseString;
		StringBuilder response = new StringBuilder();

		try {
			while ((responseString = responseReader.readLine()) != null) {
				response.append(responseString);
			}
			responseReader.close();
		} catch (IOException e) {
			//logExceptionWhileReceivingResponse(e);
			return ERROR_RECEIVING_RESPONSE;
		}

		this.response = response.toString();
		return this.response;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public boolean isSuccessful() {
		return getResponseCode() == HTTP_SUCCESS_CODE;
	}

	/**
     * This method logs an exception on error level.
     * @param head - string message to be logged
     * @param exception - throwable exception to be logged
     */
	protected void logException(String head, Throwable exception) {
		logger.error(head, exception);
	}

    /**
     * This method logs an exception in content setting.
     * @param exception - throwable exception to be logged
     */
	protected void logExceptionInContentSetting(Throwable exception) {
		logException("can't set content: ", exception);
	}

    /**
     * This method logs an exception while receiving the response.
     * @param exception - throwable exception to be logged
     */
	protected void logExceptionWhileReceivingResponse(Throwable exception) {
		logException("can't receive response: ", exception);
	}
}
