package de.hpi.bpt.argos.common;

import java.net.HttpURLConnection;

public interface RestRequest {
	HttpURLConnection getConnection();

	void setContent(String requestContent);
}
