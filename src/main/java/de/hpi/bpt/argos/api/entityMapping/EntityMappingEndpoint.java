package de.hpi.bpt.argos.api.entityMapping;

import de.hpi.bpt.argos.common.RestEndpoint;
import de.hpi.bpt.argos.core.Argos;
import de.hpi.bpt.argos.util.RestEndpointUtilImpl;
import spark.Request;
import spark.Response;

import java.util.Objects;

/**
 * This interface represents the endpoint to receive event entity mappings.
 */
public interface EntityMappingEndpoint extends RestEndpoint {

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
	 * This method returns the base uri for the entityMappingEndpoint.
	 * @return - the base uri for the entityMappingEndpoint
	 */
	static String getEntityMappingEndpointBaseUri() {
        return String.format("%1$s/api/entitymapping", Argos.getRoutePrefix());
    }

    /**
     * This method returns the basic URI to create an entity mapping.
     * @return - the URI to create an entity mapping
     */
    static String getCreateEntityMappingBaseUri() {
        return  String.format("%1$s/create", getEntityMappingEndpointBaseUri());
    }

    /**
     * This method returns the basic URI to delete an entity mapping.
     * @return - the URI to delete an entity mapping
     */
    static String getDeleteEntityMappingBaseUri() {
        return  String.format("%1$s/%2$s/delete", getEntityMappingEndpointBaseUri(), getEntityMappingIdParameter(true));
    }

    /**
     * This method returns the basic URI to edit an entity mapping.
     * @return - the URI to edit an entity mapping
     */
    static String getEditEntityMappingBaseUri() {
        return  String.format("%1$s/%2$s/edit", getEntityMappingEndpointBaseUri(), getEntityMappingIdParameter(true));
    }

    /**
     * This method returns the basic URI to delete an entity mapping.
     * @param entityMappingId - the id of the entity mapping to be searched for
     * @return - the URI to delete an entity mapping
     */
    static String getDeleteEntityMappingUri(long entityMappingId) {
        return  getDeleteEntityMappingBaseUri().replaceAll(getEntityMappingIdParameter(true), Objects.toString(entityMappingId, "0"));
    }

    /**
     * This method returns the basic URI to edit an entity mapping.
     * @param entityMappingId - the id of the entity mapping to be searched for
     * @return - the URI to edit an entity mapping
     */
    static String getEditEntityMappingUri(long entityMappingId) {
        return  getEditEntityMappingBaseUri().replaceAll(getEntityMappingIdParameter(true), Objects.toString(entityMappingId, "0"));
    }

    /**
     * This method returns the entity type id path parameter.
     * @param includePrefix - if a prefix should be included
     * @return - entity type id path parameter as a string
     */
    static String getEntityMappingIdParameter(boolean includePrefix) {
        return RestEndpointUtilImpl.getInstance().getParameter("mappingId", includePrefix);
    }
}
