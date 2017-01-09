package de.hpi.bpt.argos.common;

public interface RestRequestFactory {

	RestRequest createPostRequest(String host, String uri, String contentType, String acceptType);

	RestRequest createPostRequest(String host, String uri);
}
