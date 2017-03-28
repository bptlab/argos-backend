package de.hpi.bpt.argos.api.productConfiguration;

import de.hpi.bpt.argos.api.product.ProductEndpoint;
import de.hpi.bpt.argos.common.RestEndpoint;
import de.hpi.bpt.argos.persistence.model.product.ProductState;
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
	 * This method is called via API and returns a list of event types which are defined for this product configuration.
	 * @param request - Spark defined parameter containing request object
	 * @param response - Spark defined parameter containing response object
	 * @return - a json string representation of all event types for this product configuration
	 */
	String getEventTypesForProductConfiguration(Request request, Response response);

	/**
	 * This method is called via API and returns all events within a certain index range for a specified product configuration currently registered
	 * in the persistence.
	 * @param request - Spark defined parameter containing request object
	 * @param response - Spark defined parameter containing response object
	 * @return - returns a JSON string of a list of all events for a specified product configuration within a certain index range
	 */
	String getEventsForProductConfiguration(Request request, Response response);

	/**
	 * This method is called via API and updates a specific status change query for a specified product.
	 * @param request - Spark defined parameter containing request object
	 * @param response - Spark defined parameter containing response object
	 * @return - a success message
	 */
	String updateStatusQuery(Request request, Response response);

	/**
	 * This method returns the basic uri to retrieve product configurations from including path parameters.
	 * @return - the basic uri to retrieve product configurations from including path parameters
	 */
	static String getProductConfigurationBaseUri() {
		return String.format("/api/productconfigurations/%1$s", getProductConfigurationIdParameter(true));
	}

	/**
	 * This method returns the basic URI to retrieve all event types for a product configuration with path variables.
	 * @return - the URI to retrieve all event types for a product configuration from
	 */
	static String getEventTypesForProductConfigurationBaseUri() {
		return String.format("/api/productconfigurations/%1$s/eventtypes", getProductConfigurationIdParameter(true));
	}

	/**
	 * This method returns the basic URI to retrieve events for a product configuration with path variables.
	 * @return - the URI to retrieve events for a product configuration from
	 */
	static String getEventsForProductConfigurationBaseUri() {
		return String.format("/api/productconfigurations/%1$s/events/%2$s/%3$s/%4$s",
				getProductConfigurationIdParameter(true),
				ProductEndpoint.getEventTypeIdParameter(true),
				ProductEndpoint.getIndexFromParameter(true),
				ProductEndpoint.getIndexToParameter(true));
	}

	/**
	 * This method returns the basic URI to post product status change query updates to with path variables.
	 * @return - the basic URI to post product status change query updates to with path variables
	 */
	static String getUpdateStatusQueryBaseUri() {
		return String.format("/api/products/%1$s/update/statuschange/%2$s",
				ProductConfigurationEndPoint.getProductConfigurationIdParameter(true),
				getNewProductStatusParameter(true));
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
	 * This method returns the URI to retrieve all event types for a product configuration.
	 * @param productConfigurationId - the product configuration id
	 * @return - the URI to retrieve all event types for a product configuration from
	 */
	static String getEventTypesForProductConfigurationUri(long productConfigurationId) {
		return getEventTypesForProductConfigurationBaseUri()
				.replaceAll(getProductConfigurationIdParameter(true), Objects.toString(productConfigurationId, "0"));
	}

	/**
	 * This method returns the URI to retrieve events within a range for a product configuration.
	 * @param productConfigurationId - the product configuration id
	 * @param eventTypeId - the event type id
	 * @param indexFrom - the start index for the event range
	 * @param indexTo - the end index for the event range
	 * @return - the URI to retrieve events within a range for product configurations from
	 */
	static String getEventsForProductConfigurationUri(long productConfigurationId, long eventTypeId, int indexFrom, int indexTo) {
		return getEventsForProductConfigurationBaseUri()
				.replaceAll(getProductConfigurationIdParameter(true), Objects.toString(productConfigurationId, "0"))
				.replaceAll(ProductEndpoint.getEventTypeIdParameter(true), Objects.toString(eventTypeId, "0"))
				.replaceAll(ProductEndpoint.getIndexFromParameter(true), Objects.toString(indexFrom, "-1"))
				.replaceAll(ProductEndpoint.getIndexToParameter(true), Objects.toString(indexTo, "-2"));
	}

	/**
	 * This method returns the URI to post product status change query updates to.
	 * @param productConfigurationId - the product configuration id
	 * @param newState - the new state of the product configuration, after an event of this query arrived
	 * @return - the URI to post product status change query updates to
	 */
	static String getUpdateStatusQueryUri(long productConfigurationId, ProductState newState) {
		return getUpdateStatusQueryBaseUri()
				.replaceAll(getProductConfigurationIdParameter(true), Objects.toString(productConfigurationId, "0"))
				.replaceAll(getNewProductStatusParameter(true), newState.toString());
	}

	/**
	 * This method returns the product configuration id path parameter.
	 * @param includePrefix - if a prefix should be included
	 * @return - the product configuration id parameter as a string
	 */
	static String getProductConfigurationIdParameter(boolean includePrefix) {
		return RestEndpoint.getParameter("productConfigurationId", includePrefix);
	}

	/**
	 * This method returns the new product status path parameter.
	 * @param includePrefix - if a prefix should be included
	 * @return - new product status path parameter as a string
	 */
	static String getNewProductStatusParameter(boolean includePrefix) {
		return RestEndpoint.getParameter("newProductStatus", includePrefix);
	}
}
