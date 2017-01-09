package de.hpi.bpt.argos.common;

import java.net.HttpURLConnection;

public interface RestRequest {
	HttpURLConnection getConnection();

	void setContent(String requestContent);

	int getResponseCode();

	String getResponse();

	boolean isSuccessful();
}
