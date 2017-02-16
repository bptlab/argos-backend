package de.hpi.bpt.argos.common;

import de.hpi.bpt.argos.api.response.ResponseFactory;
import de.hpi.bpt.argos.persistence.database.PersistenceEntityManager;
import spark.Service;

/**
 * This interface is a base class for endpoint implementations, it provides setup and finishing methods.
 */
@FunctionalInterface
public interface RestEndpoint {

	/**
	 * This method sets up the rest endpoint.
	 * @param responseFactory - the factory to retrieve responses from
	 * @param entityManager - the entity manager to access persistence entities
	 * @param sparkService - the spark service to register routes to
	 */
	void setup(ResponseFactory responseFactory, PersistenceEntityManager entityManager, Service sparkService);

	/**
	 * This method returns a parameter included with a a prefix or not.
	 * @param parameterName - the parameter to update
	 * @param includePrefix - if the prefix should be included
	 * @return - the updated parameter
	 */
	static String getParameter(String parameterName, boolean includePrefix) {
		String updatedParameterName = parameterName;
		if (includePrefix) {
			updatedParameterName = ":" + updatedParameterName;
		}
		return updatedParameterName;
	}

	/**
	 * This method returns the http success status code.
	 * @return - the http success status code
	 */
	static int getHttpSuccessCode() {
		return 200;
	}
}
