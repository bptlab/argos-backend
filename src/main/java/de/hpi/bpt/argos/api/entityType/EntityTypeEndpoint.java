package de.hpi.bpt.argos.api.entityType;

import de.hpi.bpt.argos.core.Argos;
import de.hpi.bpt.argos.util.RestUriUtil;
import spark.Request;
import spark.Response;

import java.util.Objects;

/**
 * This interface represents the endpoint to receive entity types.
 */
public interface EntityTypeEndpoint {
    String ENTITY_TYPE_BASE_URI = String.format("%1$s/api/entitytype", Argos.getRoutePrefix());

    /**
     * This method returns the hierarchy overview of all entityTypes.
     * @param request - Spark defined parameter containing request object
     * @param response - Spark defined parameter containing response object
     * @return - returns a JSON String of the hierarchy
     */
    String getEntityTypeHierarchy(Request request, Response response);

    /**
     * This method returns all attributes of an eventType.
     * @param request - Spark defined parameter containing request object
     * @param response - Spark defined parameter containing response object
     * @return - returns a JSON String of the attributes
     */
    String getEntityTypeAttributes(Request request, Response response);

    /**
     * This method returns all entityMappings of an eventType.
     * @param request - Spark defined parameter containing request object
     * @param response - Spark defined parameter containing response object
     * @return - returns a JSON String of the attributes
     */
    String getEntityTypeEntityMappings(Request request, Response response);

    /**
     * This method returns the basic URI to retrieve the hierarchy.
     * @return - the URI to retrieve the hierarchy from
     */
    static String getEntityTypeHierarchyBaseUri() {
        return  String.format("%1$s/hierarchy", ENTITY_TYPE_BASE_URI);
    }

    /**
     * This method returns the basic URI to retrieve the attributes of an event type.
     * @return - the URI to retrieve attributes from
     */
    static String getEntityTypeAttributesBaseUri() {
        return  String.format("%1$s/%2$s/attributes", ENTITY_TYPE_BASE_URI, getEntityTypeIdParameter(true));
    }

    /**
     * This method returns the basic URI to retrieve the event entity mappings of an event type.
     * @return - the URI to retrieve entity mappings from
     */
    static String getEntityTypeEntityMappingsBaseUri() {
        return  String.format("%1$s/%2$s/entitymappings", ENTITY_TYPE_BASE_URI, getEntityTypeIdParameter(true));
    }

    /**
     * This method returns the basic URI to retrieve the attributes of an event type.
     * @param entityTypeId - the id of the entity type to be searched for
     * @return - the URI to retrieve attributes from
     */
    static String getEntityTypeAttributesUri(long entityTypeId) {
        return  getEntityTypeAttributesBaseUri().replaceAll(getEntityTypeIdParameter(true), Objects.toString(entityTypeId, "0"));
    }

    /**
     * This method returns the basic URI to retrieve the event entity mappings of an event type.
     * @param entityTypeId - the id of the entity type to be searched for
     * @return - the URI to retrieve entity mappings from
     */
    static String getEntityTypeEntityMappingsUri(long entityTypeId) {
        return  getEntityTypeEntityMappingsBaseUri().replaceAll(getEntityTypeIdParameter(true), Objects.toString(entityTypeId, "0"));
    }

    /**
     * This method returns the entity type id path parameter.
     * @param includePrefix - if a prefix should be included
     * @return - entity type id path parameter as a string
     */
    static String getEntityTypeIdParameter(boolean includePrefix) {
        return RestUriUtil.getParameter("typeId", includePrefix);
    }
}
