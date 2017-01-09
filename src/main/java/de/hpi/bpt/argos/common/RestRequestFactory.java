package de.hpi.bpt.argos.common;

public interface RestRequestFactory {

	RestRequest createRequest(String host, String uri, String requestMethod, String contentType, String acceptType);

	RestRequest createPostRequest(String host, String uri, String contentType, String acceptType);

	RestRequest createPostRequest(String host, String uri);
}
