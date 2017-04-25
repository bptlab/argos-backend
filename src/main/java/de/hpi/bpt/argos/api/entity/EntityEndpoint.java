package de.hpi.bpt.argos.api.entity;

import de.hpi.bpt.argos.core.Argos;
import de.hpi.bpt.argos.util.RestUriUtil;
import spark.Request;
import spark.Response;

/**
 * This interface represents the endpoint to receive entities.
 */
public interface EntityEndpoint {
    String ENTITY_BASE_URI = String.format("%1$s/api/entity", Argos.getRoutePrefix());

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
    String getEventTypesOfEntity(Request request, Response response);

    /**
     * This method returns the events of the given entity withing given range.
     * @param request - Spark defined parameter containing request object
     * @param response - Spark defined parameter containing response object
     * @return - returns a JSON String of the events of the given entity
     */
    String getEventsOfEntity(Request request, Response response);

    /**
     * This method returns the basic URI to retrieve child entities.
     * @return - the URI to retrieve child entities from
     */
    static String getChildEntitiesUri() {
        return  String.format("%1$s/%2$s/children/type/%3$s/%4$s", ENTITY_BASE_URI,
                getEntityIdParameter(true),
                getEntityTypeIdParameter(true),
                getAttributeNamesParameter(true));
    }

    /**
     * This method returns the basic URI to retrieve an entity.
     * @return - the URI to retrieve the entity from
     */
    static String getEntityUri() {
        return  String.format("%1$s/%2$s", ENTITY_BASE_URI, getEntityIdParameter(true));
    }

    /**
     * This method returns the basic URI to retrieve the event types of an entity.
     * @return - the URI to retrieve event types from
     */
    static String getEventTypesOfEntityUri() {
        return  String.format("%1$s/%2$s/eventtypes", ENTITY_BASE_URI, getEntityIdParameter(true));
    }

    /**
     * This method returns the basic URI to retrieve the events of an entity.
     * @return - the URI to retrieve events from
     */
    static String getEventsOfEntityUri() {
        return  String.format("%1$s/%2$s/eventtype/%3$s/events/%4$s/%5$s", ENTITY_BASE_URI,
                getEntityIdParameter(true),
                getEntityTypeIdParameter(true),
                getIndexFromParameter(true),
                getIndexToParameter(true));
    }

    /**
     * This method returns the entity type id path parameter.
     * @param includePrefix - if a prefix should be included
     * @return - entity type id path parameter as a string
     */
    static String getEntityIdParameter(boolean includePrefix) {
        return RestUriUtil.getParameter("entityId", includePrefix);
    }

    /**
     * This method returns the entity type id path parameter.
     * @param includePrefix - if a prefix should be included
     * @return - entity type id path parameter as a string
     */
    static String getEntityTypeIdParameter(boolean includePrefix) {
        return RestUriUtil.getParameter("typeId", includePrefix);
    }

    /**
     * This method returns the entity type id path parameter.
     * @param includePrefix - if a prefix should be included
     * @return - entity type id path parameter as a string
     */
    static String getAttributeNamesParameter(boolean includePrefix) {
        return RestUriUtil.getParameter("attributeNames", includePrefix);
    }

    /**
     * This method returns the entity type id path parameter.
     * @param includePrefix - if a prefix should be included
     * @return - entity type id path parameter as a string
     */
    static String getIndexFromParameter(boolean includePrefix) {
        return RestUriUtil.getParameter("startIndex", includePrefix);
    }

    /**
     * This method returns the entity type id path parameter.
     * @param includePrefix - if a prefix should be included
     * @return - entity type id path parameter as a string
     */
    static String getIndexToParameter(boolean includePrefix) {
        return RestUriUtil.getParameter("endIndex", includePrefix);
    }
}
