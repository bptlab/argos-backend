package de.hpi.bpt.argos.api.productFamily;

import de.hpi.bpt.argos.common.RestEndpoint;
import spark.Request;
import spark.Response;

import java.util.Objects;

/**
 *  This interface extends the RestEndpoint. It provides three routes as defined in the product interface and
 *  the associated methods.
 */
public interface ProductFamilyEndpoint extends RestEndpoint {

	/**
	 * This method is called via API and returns one specific product family.
	 * @param request - Spark defined parameter containing request object
	 * @param response - Spark defined parameter containing response object
	 * @return - returns a JSON string of the product family
	 */
	String getProductFamily(Request request, Response response);

    /**
     * This method is called via API and returns all product families currently registered in the persistence.
     * @param request - Spark defined parameter containing request object
     * @param response - Spark defined parameter containing response object
     * @return - returns a JSON string of a list of product families
     */
    String getProductFamilies(Request request, Response response);

	/**
	 * This method returns the basic URI to retrieve product families with path variables.
	 * @return - the URI to retrieve product families from
	 */
	static String getProductFamiliesBaseUri() {
		return "/api/productfamilies";
	}

	/**
	 * This method returns the basic URI to retrieve one specific product family with path variables.
	 * @return - the URI to retrieve one specific product family from
	 */
	static String getProductFamilyBaseUri() {
		return String.format("/api/productfamilies/%1$s", getProductFamilyIdParameter(true));
	}

	/**
	 * This method returns the URI to retrieve one specific product family.
	 * @param productFamilyId - the product family id
	 * @return - the URI to retrieve one specific product family from
	 */
	static String getProductFamilyUri(long productFamilyId) {
		return getProductFamilyBaseUri()
				.replaceAll(getProductFamilyIdParameter(true), Objects.toString(productFamilyId, "0"));
	}

	/**
	 * This method returns the product family id path parameter.
	 * @param includePrefix - if a prefix should be included
	 * @return - the product family id path parameter as a string
	 */
	static String getProductFamilyIdParameter(boolean includePrefix) {
		return RestEndpoint.getParameter("productFamilyId", includePrefix);
	}
}
