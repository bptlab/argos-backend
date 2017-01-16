package de.hpi.bpt.argos.common;

import java.net.HttpURLConnection;

/**
 * This interface is a basic REST request and will be created and setup by the RestRequestFactory. It can be fully
 * parametrized in header and body.
 */
public interface RestRequest {
    /**
     * This method is a getter for the protected instance variable "connection".
     * @return - protected instance variable "connection" of type HttpURLConnection
     */
	HttpURLConnection getConnection();

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
}
