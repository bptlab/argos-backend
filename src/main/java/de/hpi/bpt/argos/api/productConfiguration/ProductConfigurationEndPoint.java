package de.hpi.bpt.argos.api.productConfiguration;

import de.hpi.bpt.argos.common.RestEndpoint;
import spark.Request;
import spark.Response;

import java.util.Objects;

/**
 * This interface represents an rest endpoint to retrieve product configurations.
 */
public interface ProductConfigurationEndPoint extends RestEndpoint {

	/**
	 * This method gets called via API and returns a specific product configuration.
	 * @param request - Spark defined parameter containing request object
	 * @param response - Spark defined parameter containing response object
	 * @return - the json representation of the requested product configuration
	 */
	String getProductConfiguration(Request request, Response response);

	/**
	 * This method returns the basic uri to retrieve product configurations from including path parameters.
	 * @return - the basic uri to retrieve product configurations from including path parameters
	 */
	static String getProductConfigurationBaseUri() {
		return String.format("/api/productconfigurations/%1$s", getProductConfigurationIdParameter(true));
	}

	/**
	 * This method returns the uri to retrieve a specific product configuration from.
	 * @param productConfigurationId - the unique id of the product configuration
	 * @return - the uri to retrieve a specific product configuration from
	 */
	static String getProductConfigurationUri(long productConfigurationId) {
		return getProductConfigurationBaseUri()
				.replaceAll(getProductConfigurationIdParameter(true), Objects.toString(productConfigurationId, "0"));
	}

	/**
	 * This method returns the product configuration id path parameter.
	 * @param includePrefix - if a prefix should be included
	 * @return - the product configuration id parameter as a string
	 */
	static String getProductConfigurationIdParameter(boolean includePrefix) {
		return RestEndpoint.getParameter("productConfigurationId", includePrefix);
	}
}
