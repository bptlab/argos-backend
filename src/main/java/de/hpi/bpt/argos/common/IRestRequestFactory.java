package de.hpi.bpt.argos.common;

public interface IRestRequestFactory {

	IRestRequest createPostRequest(String host, String uri, String contentType, String acceptType);

	IRestRequest createPostRequest(String host, String uri);
}
