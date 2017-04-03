package de.hpi.bpt.argos.api.dataType;

import de.hpi.bpt.argos.common.RestEndpoint;
import spark.Request;
import spark.Response;

/**
 * This interface represents the endpoint for retrieving supported data types.
 */
public interface DataTypeEndpoint extends RestEndpoint {

	/**
	 * This method gets called via API and returns a list of all supported data types.
	 * @param request - Spark defined parameter containing request object
	 * @param response - Spark defined parameter containing response object
	 * @return - returns a JSON string of all supported data types.
	 */
	String getDataTypes(Request request, Response response);

	/**
	 * This method returns the basic URI to retrieve all supported data types from.
	 * @return - the basic URI to retrieve all supported data types from
	 */
	static String getDataTypesBaseUri() {
		return "/api/datatypes";
	}
}
