package de.hpi.bpt.argos.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RestRequestImpl implements RestRequest {
	protected static final Logger logger = LoggerFactory.getLogger(RestRequestImpl.class);

	protected HttpURLConnection connection;

	public RestRequestImpl(URL url) throws IOException {
		connection = (HttpURLConnection)url.openConnection();
	}

	@Override
	public HttpURLConnection getConnection() {
		return connection;
	}

	@Override
	public void setContent(String requestContent) {
		connection.setDoOutput(true);

		DataOutputStream outputStream;
		try {
			outputStream = new DataOutputStream(connection.getOutputStream());
			outputStream.writeBytes(requestContent);
			outputStream.flush();
			outputStream.close();
		} catch (IOException e) {
			logExceptionInContentSetting(e);
		}
	}

	@Override
	public int getResponseCode() {
		try {
			return connection.getResponseCode();
		} catch (IOException e) {
			logExceptionWhileReceivingResponse(e);
			return 0;
		}
	}

	@Override
	public String getResponse() {
		BufferedReader responseReader;

		try {
			responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		} catch (IOException e) {
			logExceptionWhileReceivingResponse(e);
			return "";
		}

		String responseString;
		StringBuilder response = new StringBuilder();

		try {
			while ((responseString = responseReader.readLine()) != null) {
				response.append(responseString);
			}
			responseReader.close();
		} catch (IOException e) {
			logExceptionWhileReceivingResponse(e);
			return "";
		}

		return response.toString();
	}

	protected void logException(String head, Throwable exception) {
		logger.error(head, exception);
	}

	protected void logExceptionInContentSetting(Throwable exception) {
		logException("can't set content: ", exception);
	}

	protected void logExceptionWhileReceivingResponse(Throwable exception) {
		logException("can't receive response: ", exception);
	}
}
