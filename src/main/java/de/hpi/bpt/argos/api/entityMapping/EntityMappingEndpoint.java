package de.hpi.bpt.argos.api.entityMapping;

import spark.Request;
import spark.Response;

/**
 * This interface represents the endpoint to receive event entity mappings.
 */
public interface EntityMappingEndpoint {

    /**
     * This method is called via API and creates a event entity mapping.
     * @param request - Spark defined parameter containing request object
     * @param response - Spark defined parameter containing response object
     * @return - the response for the creation process
     */
    String createEntityMapping(Request request, Response response);

    /**
     * This method is called via API and deletes the event entity mapping.
     * @param request - Spark defined parameter containing request object
     * @param response - Spark defined parameter containing response object
     * @return - the response for the deletion process
     */
    String deleteEntityMapping(Request request, Response response);

    /**
     * This method is called via API and edits the event entity mapping.
     * @param request - Spark defined parameter containing request object
     * @param response - Spark defined parameter containing response object
     * @return - the response for the editing process
     */
    String deleteEventQuery(Request request, Response response);
}
