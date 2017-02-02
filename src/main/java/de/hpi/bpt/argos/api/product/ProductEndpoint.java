package de.hpi.bpt.argos.api.product;

import de.hpi.bpt.argos.common.RestEndpoint;
import spark.Request;
import spark.Response;

import java.util.Objects;

/**
 * This interface represents the endpoint for retrieving products.
 */
public interface ProductEndpoint extends RestEndpoint {

	/**
	 * This method is called via API and returns details of the specified product.
	 * @param request - Spark defined parameter containing request object
	 * @param response - Spark defined parameter containing response object
	 * @return - a json string representation of this product
	 */
	String getProduct(Request request, Response response);

	/**
	 * This method is called via API and returns a list of event types which are defined for this product.
	 * @param request - Spark defined parameter containing request object
	 * @param response - Spark defined parameter containing response object
	 * @return - a json string representation of all event types for this product
	 */
	String getEventTypesForProduct(Request request, Response response);

	/**
	 * This method is called via API and returns all events for a specified product family currently registered
	 * in the persistence.
	 * @param request - Spark defined parameter containing request object
	 * @param response - Spark defined parameter containing response object
	 * @return - returns a JSON string of a list of all events for a specified product family
	 */
	String getEventsForProduct(Request request, Response response);

	/**
	 * This method returns the basic URI to retrieve a product with path variables.
	 * @return - the URI to retrieve a product from
	 */
	static String getProductBaseUri() {
		return String.format("/api/products/%1$s", getProductIdParameter(true));
	}

	/**
	 * This method returns the basic URI to retrieve all event types for a product with path variables.
	 * @return - the URI to retrieve all event types for a product from
	 */
	static String getEventTypesForProductBaseUri() {
		return String.format("/api/products/%1$s/eventtypes", getProductIdParameter(true));
	}

	/**
	 * This method returns the basic URI to retrieve events for a product with path variables.
	 * @return - the URI to retrieve events for a product from
	 */
	static String getEventsForProductBaseUri() {
		return String.format("/api/products/%1$s/events/%2$s/%3$s/%4$s",
				getProductIdParameter(true),
				getEventTypeIdParameter(true),
				getIndexFromParameter(true),
				getIndexToParameter(true));
	}

	/**
	 * This method returns the URI to retrieve a product.
	 * @param productId - the product id
	 * @return - the URI to retrieve a product from
	 */
	static String getProductUri(long productId) {
		return getProductBaseUri().replaceAll(getProductIdParameter(true), Objects.toString(productId, "0"));
	}

	/**
	 * This method returns the URI to retrieve all event types for a product.
	 * @param productId - the product id
	 * @return - the URI to retrieve all event types for a product from
	 */
	static String getEventTypesForProductUri(long productId) {
		return getEventTypesForProductBaseUri()
				.replaceAll(getProductIdParameter(true), Objects.toString(productId, "0"));
	}

	/**
	 * This method returns the URI to retrieve events within a range for product.
	 * @param productId - the product id
	 * @param eventTypeId - the event type id
	 * @param indexFrom - the start index for the event range
	 * @param indexTo - the end index for the event range
	 * @return - the URI to retrieve events within a range for products from
	 */
	static String getEventsForProductUri(long productId, long eventTypeId, int indexFrom, int indexTo) {
		return getEventsForProductBaseUri()
				.replaceAll(getProductIdParameter(true), Objects.toString(productId, "0"))
				.replaceAll(getEventTypeIdParameter(true), Objects.toString(eventTypeId, "0"))
				.replaceAll(getIndexFromParameter(true), Objects.toString(indexFrom, "-1"))
				.replaceAll(getIndexToParameter(true), Objects.toString(indexTo, "-2"));
	}

	/**
	 * This method returns the product id path parameter.
	 * @param includePrefix - if a prefix should be included
	 * @return - the product id path parameter as a string
	 */
	static String getProductIdParameter(boolean includePrefix) {
		return RestEndpoint.getParameter("productId", includePrefix);
	}

	/**
	 * This method returns the eventtype id path parameter.
	 * @param includePrefix - if a prefix should be included
	 * @return - the eventtype id path parameter as a string
	 */
	static String getEventTypeIdParameter(boolean includePrefix) {
		return RestEndpoint.getParameter("eventTypeId", includePrefix);
	}

	/**
	 * This method returns the index from path parameter.
	 * @param includePrefix - if a prefix should be included
	 * @return - the index from path parameter as a string
	 */
	static String getIndexFromParameter(boolean includePrefix) {
		return RestEndpoint.getParameter("indexFrom", includePrefix);
	}

	/**
	 * This method returns the index to path parameter.
	 * @param includePrefix - if a prefix should be included
	 * @return - the index to path parameter as a string
	 */
	static String getIndexToParameter(boolean includePrefix) {
		return RestEndpoint.getParameter("indexTo", includePrefix);
	}
}
