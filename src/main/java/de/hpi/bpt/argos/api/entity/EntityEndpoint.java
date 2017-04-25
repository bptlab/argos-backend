package de.hpi.bpt.argos.api.entity;

import spark.Request;
import spark.Response;

/**
 * This interface represents the endpoint to receive entities.
 */
public interface EntityEndpoint {

    /**
     * This method returns the requested entity.
     * @param request - Spark defined parameter containing request object
     * @param response - Spark defined parameter containing response object
     * @return - returns a JSON String of the requested entity
     */
    String getEntity(Request request, Response response);

    /**
     * This method returns the child entities of the given entity.
     * @param request - Spark defined parameter containing request object
     * @param response - Spark defined parameter containing response object
     * @return - returns a JSON String of the children of the given entity
     */
    String getChildEntities(Request request, Response response);

    /**
     * This method returns the event types of the given entity.
     * @param request - Spark defined parameter containing request object
     * @param response - Spark defined parameter containing response object
     * @return - returns a JSON String of the event types of the given entity
     */
    String getEventTypes(Request request, Response response);

    /**
     * This method returns the events of the given entity withing given range.
     * @param request - Spark defined parameter containing request object
     * @param response - Spark defined parameter containing response object
     * @return - returns a JSON String of the events of the given entity
     */
    String getEvents(Request request, Response response);
}
