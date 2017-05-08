package de.hpi.bpt.argos.common;

/**
 * This interface is a basic REST request and will be created and setup by the RestRequestFactory. It can be fully
 * parametrized in header and body.
 */
public interface RestRequest {

	String ERROR_RESPONSE = "error while receiving response";

	/**
	 * This method sets the requests method.
	 * @param method - the method to be set
	 */
	void setMethod(String method);

	/**
	 * This method sets a property specified by its key.
	 * @param key - the property key
	 * @param value - the property value
	 */
	void setProperty(String key, String value);

	/**
	 * This method returns the content body of the rest request.
	 * @return - the content body of the rest request
	 */
	String getContent();

    /**
     * This method sets the content body of the rest request.
     * @param requestContent - content body to be set as a string
     */
	void setContent(String requestContent);

    /**
     * This method returns the response code.
     * @return - returns the response code of the rest request as an integer
     */
	int getResponseCode();

    /**
     * This method returns the response body.
     * @return - returns the response body of the rest request as a string
     */
    String getResponse();

    /**
     * This method checks if the sent request was successful in the context of the request.
     * @return - boolean determining success or failure
     */
	boolean isSuccessful();

	/**
	 * This method returns whether the target host is reachable.
	 * @return - true, if the target host is reachable
	 */
	boolean isHostReachable();
}
