package de.hpi.bpt.argos.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;
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

	protected void logException(String head, Throwable exception) {
		logger.error(head, exception);
	}

	protected void logExceptionInContentSetting(Throwable exception) {
		logException("can't set content: ", exception);
	}
}
