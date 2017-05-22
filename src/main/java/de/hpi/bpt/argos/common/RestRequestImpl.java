package de.hpi.bpt.argos.common;

import de.hpi.bpt.argos.util.HttpStatusCodes;
import de.hpi.bpt.argos.util.LoggerUtilImpl;
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
	private static final Logger logger = LoggerFactory.getLogger(RestRequestImpl.class);

	private HttpURLConnection connection;
	private String content;
	private String response;
	private boolean hostReachable;

	/**
	 * Constructor for RestRequest, instantiates new connection.
	 * @param url - URL object that should be requested
	 * @throws IOException - throws IOException in case of failure (e.g. network problems)
	 */
	public RestRequestImpl(URL url) throws IOException {
		connection = (HttpURLConnection) url.openConnection();
		hostReachable = true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMethod(String method) {
		try {
			this.connection.setRequestMethod(method);
		} catch (ProtocolException e) {
			LoggerUtilImpl.getInstance().error(logger, String.format("cannot set method of rest request: '$1%s'", method), e);
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
			LoggerUtilImpl.getInstance().error(logger, "cannot set content of rest request", e);
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
			LoggerUtilImpl.getInstance().error(logger, "cannot get response code of rest request", e);
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

			InputStreamReader inputStreamReader;

			if (isSuccessful()) {
				inputStreamReader = new InputStreamReader(connection.getInputStream());
			} else {
				inputStreamReader = new InputStreamReader(connection.getErrorStream());
			}

			responseReader = new BufferedReader(inputStreamReader);
			String responseString;

			while ((responseString = responseReader.readLine()) != null) {
				restResponse.append(responseString);
			}
			responseReader.close();

		} catch (IOException e) {
			LoggerUtilImpl.getInstance().error(logger, "cannot read response of rest request", e);
			return RestRequest.ERROR_RESPONSE;
		}

		response = restResponse.toString();
		return response;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public boolean isSuccessful() {
		return getResponseCode() == HttpStatusCodes.SUCCESS;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isHostReachable() {
		return hostReachable;
	}
}
