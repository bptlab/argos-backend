package de.hpi.bpt.argos.api.entityType;

import spark.Request;
import spark.Response;

/**
 * This interface represents the endpoint to receive entity types.
 */
public interface EntityTypeEndpoint {
    
    /**
     * This method returns the hierarchy overview of all entityTypes.
     * @param request - Spark defined parameter containing request object
     * @param response - Spark defined parameter containing response object
     * @return - returns a JSON String of the hierarchy
     */
    String getHierarchy(Request request, Response response);

    /**
     * This method returns all attributes of an eventType
     * @param request - Spark defined parameter containing request object
     * @param response - Spark defined parameter containing response object
     * @return - returns a JSON String of the attributes
     */
    String getAttributes(Request request, Response response);

    /**
     * This method returns all entityMappings of an eventType
     * @param request - Spark defined parameter containing request object
     * @param response - Spark defined parameter containing response object
     * @return - returns a JSON String of the attributes
     */
    String getEntityMappings(Request request, Response response);
}
