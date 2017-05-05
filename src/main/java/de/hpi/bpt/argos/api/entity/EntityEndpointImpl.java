package de.hpi.bpt.argos.api.entity;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.hpi.bpt.argos.api.eventType.EventTypeEndpointImpl;
import de.hpi.bpt.argos.storage.PersistenceAdapterImpl;
import de.hpi.bpt.argos.storage.dataModel.attribute.Attribute;
import de.hpi.bpt.argos.storage.dataModel.attribute.type.TypeAttribute;
import de.hpi.bpt.argos.storage.dataModel.entity.Entity;
import de.hpi.bpt.argos.storage.dataModel.event.Event;
import de.hpi.bpt.argos.storage.dataModel.event.EventImpl;
import de.hpi.bpt.argos.storage.dataModel.event.type.EventType;
import de.hpi.bpt.argos.util.HttpStatusCodes;
import de.hpi.bpt.argos.util.LoggerUtilImpl;
import de.hpi.bpt.argos.util.RestEndpointUtil;
import de.hpi.bpt.argos.util.RestEndpointUtilImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Service;

import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

import static spark.Spark.halt;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class EntityEndpointImpl implements EntityEndpoint {

    private static final Logger logger = LoggerFactory.getLogger(EventTypeEndpointImpl.class);
    private static final Gson serializer = new Gson();
    private static final RestEndpointUtil endpointUtil = RestEndpointUtilImpl.getInstance();

    /**
     * {@inheritDoc}
     */
    @Override
    public void setup(Service sparkService) {
        sparkService.get(EntityEndpoint.getEntityBaseUri(), this::getEntity);
        sparkService.get(EntityEndpoint.getChildEntitiesBaseUri(), this::getChildEntities);
        sparkService.get(EntityEndpoint.getEventTypesOfEntityBaseUri(), this::getEventTypesOfEntity);
        sparkService.get(EntityEndpoint.getEventsOfEntityBaseUri(), this::getEventsOfEntity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEntity(Request request, Response response) {
        endpointUtil.logReceivedRequest(logger, request);

        long entityId = getEntityId(request);
        Entity entity = PersistenceAdapterImpl.getInstance().getEntity(entityId);

        if (entity == null) {
        	halt(HttpStatusCodes.NOT_FOUND, "cannot find entity");
		}

        List<Attribute> attributes = PersistenceAdapterImpl.getInstance().getAttributes(entityId);
        JsonObject jsonEventType = getEntityJson(entity, attributes);

        response.body(serializer.toJson(jsonEventType));
        endpointUtil.logSendingResponse(logger, request, response.status(), response.body());
        return response.body();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getChildEntities(Request request, Response response) {
        endpointUtil.logReceivedRequest(logger, request);

        long entityId = getEntityId(request);
        long entityTypeId = getEntityTypeId(request);
        List<Entity> childEntities = PersistenceAdapterImpl.getInstance().getEntities(entityId, entityTypeId);
        List<Long> attributesTypeIdsToInclude = getAttributesTypeIdsToInclude(request);
        JsonArray jsonEntities = getEntitiesJson(childEntities, attributesTypeIdsToInclude);

        response.body(serializer.toJson(jsonEntities));
        endpointUtil.logSendingResponse(logger, request, response.status(), response.body());
        return response.body();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEventTypesOfEntity(Request request, Response response) {
        endpointUtil.logReceivedRequest(logger, request);

        long entityId = getEntityId(request);
        List<EventType> allEventTypes = PersistenceAdapterImpl.getInstance().getEventTypes();

        List<EventType> eventTypes = new ArrayList<>();
        for (EventType eventType : allEventTypes) {
            String sql = getExistsEventQuery(entityId, eventType.getId());
            if (PersistenceAdapterImpl.getInstance().getExistsEvent(sql)) {
                eventTypes.add(eventType);
            }
        }

        JsonArray eventTypesJson = getEventTypesJson(eventTypes);

        response.body(serializer.toJson(eventTypesJson));
        endpointUtil.logSendingResponse(logger, request, response.status(), response.body());
        return response.body();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEventsOfEntity(Request request, Response response) {
        endpointUtil.logReceivedRequest(logger, request);

        long entityId = getEntityId(request);
        long eventTypeId = getEntityTypeId(request);
        int startIndex = getStartIndex(request);
        int endIndex = getEndIndex(request);

        if (endIndex < startIndex) {
        	int temp = endIndex;
        	endIndex = startIndex;
        	startIndex = temp;
		}

        List<Event> events = PersistenceAdapterImpl.getInstance().getEvents(entityId, eventTypeId, startIndex, endIndex);

        JsonArray eventTypesJson = getEventsJson(events);

        response.body(serializer.toJson(eventTypesJson));
        endpointUtil.logSendingResponse(logger, request, response.status(), response.body());
        return response.body();
    }

	/**
	 * This method returns an entity as a json object including the given attributes.
	 * @param entity the entity to be returned as json
	 * @param attributes the attributes to be returned in json
	 * @return json object that represents the given entity
	 */
	private JsonObject getEntityJson(Entity entity, List<Attribute> attributes) {
		try {
			JsonObject jsonEntity = new JsonObject();

			jsonEntity.addProperty("Id", entity.getId());
			jsonEntity.addProperty("TypeId", entity.getTypeId());
			jsonEntity.addProperty("ParentId", entity.getParentId());
			jsonEntity.addProperty("Name", entity.getName());
			jsonEntity.addProperty("Status", entity.getStatus());

			JsonArray attributesJson = getAttributesJson(attributes);
			jsonEntity.add("Attributes", attributesJson);

			return jsonEntity;
		} catch (Exception e) {
			LoggerUtilImpl.getInstance().error(logger, "cannot parse event type", e);
			return new JsonObject();
		}
	}


	/**
	 * This method returns a list of attributes, which belong to a specific entity.
	 * @param entity the owner entity of the attributes
	 * @param attributeTypeIds - a list of unique identifiers of typeAttributes, which are the attributes to include in the returned list
	 * @return a list of attributes, which belong to a specific entity
	 */
    private List<Attribute> getIncludedAttributes(Entity entity, List<Long> attributeTypeIds) {
		List<Attribute> entityAttributes = PersistenceAdapterImpl.getInstance().getAttributes(entity.getId());
		List<Attribute> attributesToInclude = new ArrayList<>();

		for (Attribute entityAttribute : entityAttributes) {
			if (attributeTypeIds.contains(entityAttribute.getTypeAttributeId())) {
				attributesToInclude.add(entityAttribute);
			}
		}

		return attributesToInclude;
    }

    /**
     * This method returns a list of entities as a json array including the given attributes.
     * @param entities the entities to be returned as json
     * @param attributesTypeIdsToInclude the unique identifiers of the typeAttributes to be returned in json
     * @return json array that represents the given entities
     */
    private JsonArray getEntitiesJson(List<Entity> entities, List<Long> attributesTypeIdsToInclude) {
        JsonArray entitiesJson = new JsonArray();
        for (Entity entity : entities) {
            entitiesJson.add(getEntityJson(entity, getIncludedAttributes(entity, attributesTypeIdsToInclude)));
        }
        return entitiesJson;
    }

    /**
     * This method returns an event type as a JsonObject.
     * @param eventType - the event type
     * @return - a json representation of the event type
     */
    private JsonObject getEventTypeJson(EventType eventType) {
        try {
            JsonObject jsonEventType = new JsonObject();

            jsonEventType.addProperty("Id", eventType.getId());
            jsonEventType.addProperty("Name", eventType.getName());
            jsonEventType.addProperty("NumberOfEvents",
                    PersistenceAdapterImpl.getInstance().getEventCountOfEventType(eventType.getId()));
            jsonEventType.addProperty("TimestampAttributeId", eventType.getTimeStampAttributeId());

            return jsonEventType;
        } catch (Exception e) {
            LoggerUtilImpl.getInstance().error(logger, "cannot parse event type", e);
            return new JsonObject();
        }
    }

    /**
     * This method returns event types as a JsonArray.
     * @param eventTypes - the event types
     * @return - a json representation of the event types
     */
    private JsonArray getEventTypesJson(List<EventType> eventTypes) {
        JsonArray eventTypesJson = new JsonArray();
        for (EventType eventType : eventTypes) {
            eventTypesJson.add(getEventTypeJson(eventType));
        }
        return eventTypesJson;
    }

    /**
     * This method returns events as a JsonArray.
     * @param events - the events
     * @return - a json representation of the events
     */
    private JsonArray getEventsJson(List<Event> events) {
        JsonArray eventsJson = new JsonArray();
        for (Event event : events) {
            List<Attribute> attributes = PersistenceAdapterImpl.getInstance().getAttributes(event.getId());
            JsonArray jsonAttributes = getAttributesJson(attributes);

            JsonObject eventJson = new JsonObject();
            eventJson.add("Attributes", jsonAttributes);

            eventsJson.add(eventJson);
        }
        return eventsJson;
    }

    /**
     * This method returns attributes as a JsonObject.
     * @param attributes - the attributes
     * @return - a json representation of the attributes
     */
    private JsonArray getAttributesJson(List<Attribute> attributes) {
        JsonArray jsonAttributes = new JsonArray();
        for (Attribute attribute : attributes) {
            JsonObject jsonAttribute = new JsonObject();
            TypeAttribute typeAttribute = PersistenceAdapterImpl.getInstance().getTypeAttribute(attribute.getTypeAttributeId());
            jsonAttribute.addProperty("Name", typeAttribute.getName());
            jsonAttribute.addProperty("Value", attribute.getValue());
            jsonAttributes.add(jsonAttribute);
        }
        return jsonAttributes;
    }

    /**
     * This method returns a query to query if there exists an event query for the given entity and event type.
     * @param entityId id of the entity
     * @param eventTypeId id of the vent type
     * @return query string to find out if event query exists
     */
    private String getExistsEventQuery(long entityId, long eventTypeId) {
        Table attributeTable = EventImpl.class.getAnnotation(Table.class);
        String eventTableName = "Event";

        if (attributeTable != null) {
            eventTableName = attributeTable.name();
        }
        return String.format(
                "SELECT CASE WHEN EXISTS ( "
                        + "SELECT * "
                        + "FROM %1$s "
                        + "WHERE TypeId = %2$d "
                        + "AND EntityId = %3$d "
                        + ") "
                        + "THEN \"True\" "
                        + "ELSE \"False\" END", eventTableName, eventTypeId, entityId);
    }

    /**
     * This method returns the id given in request.
     * @param request the request with entity id
     * @return id of the entity in request
     */
    private long getEntityId(Request request) {
        return endpointUtil.validateLong(
                request.params(EntityEndpoint.getEntityIdParameter(false)),
                (Long input) -> input != 0);
    }

    /**
     * This method returns the id given in request.
     * @param request the request with entity type id
     * @return id of the entity type in request
     */
    private long getEntityTypeId(Request request) {
        return endpointUtil.validateLong(
                request.params(EntityEndpoint.getTypeIdParameter(false)),
                (Long input) -> input != 0);
    }

    /**
     * This method returns the start index given in request.
     * @param request the request with start index
     * @return start index in request
     */
    private int getStartIndex(Request request) {
        return endpointUtil.validateInteger(
                request.params(EntityEndpoint.getIndexFromParameter(false)),
                (Integer input) -> input >= 0);
    }

    /**
     * This method returns the end index given in request.
     * @param request the request with end index
     * @return end index in request
     */
    private int getEndIndex(Request request) {
        return endpointUtil.validateInteger(
                request.params(EntityEndpoint.getIndexToParameter(false)),
                (Integer input) -> input >= 0);
    }

    /**
     * This method returns the attributes that are requested in the given request by a plus-separated list string.
     * @param request the request with attributes
     * @return requested attributes
     */
    private List<Long> getAttributesTypeIdsToInclude(Request request) {
        List<String> attributeNames =  endpointUtil.validateListOfString(
                request.params(EntityEndpoint.getAttributeNamesParameter(false)),
                (String input) -> input.matches("(\\w|-)+(\\s(\\w|-)+)*"));

        // check if entity type has all given attributes defined
        List<TypeAttribute> attributesOfEntityType = PersistenceAdapterImpl.getInstance().getTypeAttributes(getEntityTypeId(request));
        List<String> entityTypeAttributeNames = new ArrayList<>();
        List<Long> entityTypeAttributeIds = new ArrayList<>();
        for (TypeAttribute att : attributesOfEntityType) {
            entityTypeAttributeNames.add(att.getName());

            if (attributeNames.contains(att.getName())) {
				entityTypeAttributeIds.add(att.getId());
			}
        }
        if (!entityTypeAttributeNames.containsAll(attributeNames)) {
            halt(HttpStatusCodes.BAD_REQUEST, "invalid attribute name given");
        }
        return entityTypeAttributeIds;
    }
}
