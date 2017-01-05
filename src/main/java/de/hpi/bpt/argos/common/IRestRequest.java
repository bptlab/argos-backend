package de.hpi.bpt.argos.common;

import java.net.HttpURLConnection;

public interface IRestRequest {
	HttpURLConnection getConnection();

	void setContent(String requestContent);
}
