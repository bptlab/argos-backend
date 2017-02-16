package de.hpi.bpt.argos.common;

/**
 * This interface defines the factory used for creating RestRequest objects of different flavour. This implements the
 * factory pattern.
 */
public interface RestRequestFactory {
    /**
     * This method returns a basic RestRequest object and is fully parameterizable in header and body.
     * @param host - the host to be requested as a string
     * @param uri - the uri to be requested as a string
     * @param requestMethod - the request method to be used as a string
     * @param contentType - the content type value to be used as a string
     * @param acceptType - the accept type value to be used as a string
     * @return - returns a RestRequest object
     */
	RestRequest createRequest(String host, String uri, String requestMethod, String contentType, String acceptType);

    /**
     * This method returns a RestRequest object using POST request method. Other headers can be parametrized.
     * @param host - the host to be requested as a string
     * @param uri - the uri to be requested as a string
     * @param contentType - the content type value to be used as a string
     * @param acceptType - the accept type value to be used as a string
     * @return - returns a RestRequest object using the request method POST
     */
	RestRequest createPostRequest(String host, String uri, String contentType, String acceptType);

    /**
     * This method returns a RestRequest object using POST request method and default content and accept types. Other
     * headers can be parameterized.
     * @param host - the host to be requested as a string
     * @param uri - the uri to be requested as a string
     * @return - returns a RestRequest object using the request method POST and default content and accept types.
     */
    RestRequest createPostRequest(String host, String uri);

	/**
	 * This method returns a RestRequst object using GET request method.
	 * @param host - the host to be requested as a string
	 * @param uri - the uri to be requested as a string
	 * @param acceptType - the content type value to be used as a string
	 * @return - returns a RestRequest object using the request method GET
	 */
    RestRequest createGetRequest(String host, String uri, String acceptType);

	/**
	 * This method returns a RestRequest object using GET request method using the default accept type.
	 * @param host - the host to be requested as a string
	 * @param uri - the uri to be requested as a string
	 * @return - returns a RestRequest object using the request method GET
	 */
	RestRequest createGetRequest(String host, String uri);

	/**
	 * This method returns a RestRequest object using DELETE request method using the default accept type.
	 * @param host - the host to be requested as a string
	 * @param uri - the uri to be requested as a string
	 * @return - returns a RestRequest object using the request method DELETE
	 */
	RestRequest createDeleteRequest(String host, String uri);

	/**
	 * This method returns a RestRequest object using DELETE request method. Other headers can be parametrized.
	 * @param host - the host to be requested as a string
	 * @param uri - the uri to be requested as a string
	 * @param contentType - the content type value to be used as a string
	 * @param acceptType - the accept type value to be used as a string
	 * @return - returns a RestRequest object using the request method DELETE
	 */
	RestRequest createDeleteRequest(String host, String uri, String contentType, String acceptType);
}
