package de.hpi.bpt.argos.api;

import de.hpi.bpt.argos.common.RestEndpoint;
import spark.Request;
import spark.Response;

/**
 *  This interface extends the RestEndpoint. It provides three routes as defined in the product interface and
 *  the associated methods.
 */
public interface ProductFamilyEndpoint extends RestEndpoint {

    /**
     * This method is called via API and returns all product families currently registered in the persistence.
     * @param request - Spark defined parameter containing request object
     * @param response - Spark defined parameter containing response object
     * @return - returns a JSON string of a list of product families
     */
    String getProductFamilies(Request request, Response response);

    /**
     * This method is called via API and returns details of the specified product family (url parameter).
     * @param request - Spark defined parameter containing request object
     * @param response - Spark defined parameter containing response object
     * @return - returns a JSON string of details of the specified product family
     */
	String getProductOverview(Request request, Response response);

    /**
     * This method is called via API and returns all events for a specified product family currently registered
     * in the persistence.
     * @param request - Spark defined parameter containing request object
     * @param response - Spark defined parameter containing response object
     * @return - returns a JSON string of a list of all events for a specified product family
     */
	String getEventsForProduct(Request request, Response response);
}
