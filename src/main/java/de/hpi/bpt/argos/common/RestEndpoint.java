package de.hpi.bpt.argos.common;

import spark.Request;
import spark.Response;
import spark.Service;

/**
 * This interface is a base class for endpoint implementations, it provides setup and finishing methods.
 */
public interface RestEndpoint {
	/**
	* The setup method adds the defined routes to spark's routes and associates them with methods.
	* @param sparkService - Instance of Spark service started in Argos
     */
	void setup(Service sparkService);

	/**
	 * This method is called at the end of a request handling and returns a string.
	 * @return - String that notifies about the successful handling of a request
	 */
	//TODO: Do we really need this method?
	String finishRequest();

	void enableCORS(String origin, String methods, String headers);
}
