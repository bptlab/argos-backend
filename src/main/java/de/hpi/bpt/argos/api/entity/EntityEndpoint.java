package de.hpi.bpt.argos.api.entity;

import de.hpi.bpt.argos.common.RestEndpoint;
import de.hpi.bpt.argos.core.Argos;
import de.hpi.bpt.argos.util.RestEndpointUtilImpl;
import spark.Request;
import spark.Response;

import java.util.List;
import java.util.Objects;

/**
 * This interface represents the endpoint to receive entities.
 */
public interface EntityEndpoint extends RestEndpoint {

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
     * This method returns the base uri for the entityEndpoint.
     * @return - the base uri for the entityEndpoint
     */
    static String getEntityPointBaseUri() {
        return String.format("%1$s/api/entity", Argos.getRoutePrefix());
    }

    /**
     * This method returns the basic URI to retrieve child entities.
     * @return - the URI to retrieve child entities from
     */
    static String getChildEntitiesBaseUri() {
        return  String.format("%1$s/%2$s/children/type/%3$s/%4$s", getEntityPointBaseUri(),
                getEntityIdParameter(true),
                getTypeIdParameter(true),
                getAttributeNamesParameter(true));
    }

    /**
     * This method returns the basic URI to retrieve an entity.
     * @return - the URI to retrieve the entity from
     */
    static String getEntityBaseUri() {
        return  String.format("%1$s/%2$s", getEntityPointBaseUri(), getEntityIdParameter(true));
    }

    /**
     * This method returns the basic URI to retrieve the event types of an entity.
     * @return - the URI to retrieve event types from
     */
    static String getEventTypesOfEntityBaseUri() {
        return  String.format("%1$s/%2$s/eventtypes/%3$s",
				getEntityPointBaseUri(),
				getEntityIdParameter(true),
				getIncludeChildEventsParameter(true));
    }

    /**
     * This method returns the basic URI to retrieve the events of an entity.
     * @return - the URI to retrieve events from
     */
    static String getEventsOfEntityBaseUri() {
        return  String.format("%1$s/%2$s/eventtype/%3$s/events/%4$s/%5$s/%6$s",
				getEntityPointBaseUri(),
                getEntityIdParameter(true),
                getTypeIdParameter(true),
                getIncludeChildEventsParameter(true),
                getIndexFromParameter(true),
                getIndexToParameter(true));
    }

    /**
     * This method returns the basic URI to retrieve child entities.
     * @param entityId - the id of the entity to be searched for
     * @param entityTypeId - the id of the entity type to be searched for
     * @param attributeNames - the names of the attributes to be searched for
     * @return - the URI to retrieve child entities from
     */
    static String getChildEntitiesUri(long entityId, long entityTypeId, List<String> attributeNames) {
        StringBuilder attributeNamesAsString = new StringBuilder();

        for (int i = 0; i < attributeNames.size(); i++) {
			attributeNamesAsString.append(attributeNames.get(i));
            if (i != attributeNames.size() - 1) {
            	attributeNamesAsString.append("+");
            }
        }
        return getChildEntitiesBaseUri()
                .replaceAll(getEntityIdParameter(true), Objects.toString(entityId, "0"))
                .replaceAll(getTypeIdParameter(true), Objects.toString(entityTypeId, "0"))
                .replaceAll(getAttributeNamesParameter(true), attributeNamesAsString.toString());
    }

    /**
     * This method returns the basic URI to retrieve an entity.
     * @param entityId - the id of the entity to be searched for
     * @return - the URI to retrieve the entity from
     */
    static String getEntityUri(long entityId) {
        return  getEntityBaseUri().replaceAll(getEntityIdParameter(true), Objects.toString(entityId, "0"));
    }

    /**
     * This method returns the basic URI to retrieve the event types of an entity.
     * @param entityId - the id of the entity to be searched for
	 * @param includeChildEvents - indicates, whether child events should be considered
     * @return - the URI to retrieve event types from
     */
    static String getEventTypesOfEntityUri(long entityId, boolean includeChildEvents) {
        return  getEventTypesOfEntityBaseUri()
				.replaceAll(getEntityIdParameter(true), Objects.toString(entityId, "0"))
				.replaceAll(getIncludeChildEventsParameter(true), Objects.toString(includeChildEvents, "false"));
    }

    /**
     * This method returns the basic URI to retrieve the events of an entity.
     * @param entityId - the id of the entity to be searched for
     * @param eventTypeId - the id of the entity type to be searched for
	 * @param includeChildEvents - indicates whether the child events should be included
     * @param fromIndex - the index of the start of the entities to be searched for
     * @param toIndex - the index of the end of the entities to be searched for
     * @return - the URI to retrieve events from
     */
    static String getEventsOfEntityUri(long entityId, long eventTypeId, boolean includeChildEvents, int fromIndex, int toIndex) {
        return getEventsOfEntityBaseUri()
                .replaceAll(getEntityIdParameter(true), Objects.toString(entityId, "0"))
                .replaceAll(getTypeIdParameter(true), Objects.toString(eventTypeId, "0"))
				.replaceAll(getIncludeChildEventsParameter(true), Objects.toString(includeChildEvents, "false"))
                .replaceAll(getIndexFromParameter(true), Objects.toString(fromIndex, "0"))
                .replaceAll(getIndexToParameter(true), Objects.toString(toIndex, "0"));
    }

    /**
     * This method returns the entity id path parameter.
     * @param includePrefix - if a prefix should be included
     * @return - entity id path parameter as a string
     */
    static String getEntityIdParameter(boolean includePrefix) {
        return RestEndpointUtilImpl.getInstance().getParameter("entityId", includePrefix);
    }

    /**
     * This method returns the entity type id path parameter.
     * @param includePrefix - if a prefix should be included
     * @return - entity type id path parameter as a string
     */
    static String getTypeIdParameter(boolean includePrefix) {
        return RestEndpointUtilImpl.getInstance().getParameter("typeId", includePrefix);
    }

    /**
     * This method returns the entity attribute names path parameter.
     * @param includePrefix - if a prefix should be included
     * @return - entity attribute names path parameter as a string
     */
    static String getAttributeNamesParameter(boolean includePrefix) {
        return RestEndpointUtilImpl.getInstance().getParameter("attributeNames", includePrefix);
    }

	/**
	 * This method returns the include child events path parameter.
	 * @param includePrefix - if a prefix should be included
	 * @return - include child events path parameter as a string
	 */
    static String getIncludeChildEventsParameter(boolean includePrefix) {
    	return RestEndpointUtilImpl.getInstance().getParameter("includeChildEvents", includePrefix);
	}

    /**
     * This method returns the from index path parameter.
     * @param includePrefix - if a prefix should be included
     * @return - from index path parameter as a string
     */
    static String getIndexFromParameter(boolean includePrefix) {
        return RestEndpointUtilImpl.getInstance().getParameter("startIndex", includePrefix);
    }

    /**
     * This method returns the to index path parameter.
     * @param includePrefix - if a prefix should be included
     * @return - to index path parameter as a string
     */
    static String getIndexToParameter(boolean includePrefix) {
        return RestEndpointUtilImpl.getInstance().getParameter("endIndex", includePrefix);
    }
}
