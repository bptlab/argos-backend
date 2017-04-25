package de.hpi.bpt.argos.api.entityMapping;

import de.hpi.bpt.argos.core.Argos;
import de.hpi.bpt.argos.util.RestUriUtil;
import spark.Request;
import spark.Response;

/**
 * This interface represents the endpoint to receive event entity mappings.
 */
public interface EntityMappingEndpoint {
    String ENTITY_MAPPING_BASE_URI = String.format("%1$s/api/entitymapping", Argos.getRoutePrefix());

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
    String editEntityMapping(Request request, Response response);

    /**
     * This method returns the basic URI to create an entity mapping.
     * @return - the URI to create an entity mapping
     */
    static String getCreateEventQueryBaseUri() {
        return  String.format("%1$s/create", ENTITY_MAPPING_BASE_URI);
    }

    /**
     * This method returns the basic URI to delete an entity mapping.
     * @return - the URI to delete an entity mapping
     */
    static String getDeleteEventQueryBaseUri() {
        return  String.format("%1$s/%2$s/delete", ENTITY_MAPPING_BASE_URI, getEntityMappingIdParameter(true));
    }

    /**
     * This method returns the basic URI to edit an entity mapping.
     * @return - the URI to edit an entity mapping
     */
    static String getEditEventQueryBaseUri() {
        return  String.format("%1$s/%2$s/edit", ENTITY_MAPPING_BASE_URI, getEntityMappingIdParameter(true));
    }
    /**
     * This method returns the entity type id path parameter.
     * @param includePrefix - if a prefix should be included
     * @return - entity type id path parameter as a string
     */
    static String getEntityMappingIdParameter(boolean includePrefix) {
        return RestUriUtil.getParameter("mappingId", includePrefix);
    }
}
