package de.hpi.bpt.argos.api.entity;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.hpi.bpt.argos.api.eventType.EventTypeEndpointImpl;
import de.hpi.bpt.argos.storage.PersistenceAdapterImpl;
import de.hpi.bpt.argos.storage.dataModel.attribute.Attribute;
import de.hpi.bpt.argos.storage.dataModel.attribute.type.TypeAttribute;
import de.hpi.bpt.argos.storage.dataModel.entity.Entity;
import de.hpi.bpt.argos.storage.dataModel.entity.VirtualRoot;
import de.hpi.bpt.argos.storage.dataModel.event.Event;
import de.hpi.bpt.argos.storage.dataModel.event.type.EventType;
import de.hpi.bpt.argos.storage.hierarchy.EntityHierarchyNode;
import de.hpi.bpt.argos.storage.hierarchy.HierarchyBuilderImpl;
import de.hpi.bpt.argos.util.HttpStatusCodes;
import de.hpi.bpt.argos.util.LoggerUtilImpl;
import de.hpi.bpt.argos.util.ObjectWrapper;
import de.hpi.bpt.argos.util.Pair;
import de.hpi.bpt.argos.util.PairImpl;
import de.hpi.bpt.argos.util.RestEndpointUtil;
import de.hpi.bpt.argos.util.RestEndpointUtilImpl;
import de.hpi.bpt.argos.util.performance.WatchImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        sparkService.get(EntityEndpoint.getEntityBaseUri(),
				(Request request, Response response) ->
						endpointUtil.executeRequest(logger, request, response, this::getEntity));

        sparkService.get(EntityEndpoint.getChildEntitiesBaseUri(),
				(Request request, Response response) ->
						endpointUtil.executeRequest(logger, request, response, this::getChildEntities));

        sparkService.get(EntityEndpoint.getEventTypesOfEntityBaseUri(),
				(Request request, Response response) ->
						endpointUtil.executeRequest(logger, request, response, this::getEventTypesOfEntity));

        sparkService.get(EntityEndpoint.getEventsOfEntityBaseUri(),
				(Request request, Response response) ->
						endpointUtil.executeRequest(logger, request, response, this::getEventsOfEntity));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEntity(Request request, Response response) {
        long entityId = getEntityId(request);
		Entity entity;

        if (entityId < 0) {
			entity = VirtualRoot.getInstance();
		} else {
        	entity = PersistenceAdapterImpl.getInstance().getEntity(entityId);
		}

        if (entity == null) {
        	halt(HttpStatusCodes.NOT_FOUND, "cannot find entity");
		}

		ObjectWrapper<List<Attribute>> attributes = new ObjectWrapper<>();
        WatchImpl.measure("get attributes",
				() -> attributes.set(PersistenceAdapterImpl.getInstance().getAttributes(entityId)));

        ObjectWrapper<List<TypeAttribute>> typeAttributes = new ObjectWrapper<>();
        WatchImpl.measure("get type attributes",
				() -> typeAttributes.set(PersistenceAdapterImpl.getInstance().getTypeAttributes(entity.getTypeId())));

        ObjectWrapper<List<Pair<TypeAttribute, Attribute>>> joinedAttributes = new ObjectWrapper<>();
        WatchImpl.measure("join attributes", () -> joinedAttributes.set(joinAttributes(typeAttributes.get(), attributes.get())));

        ObjectWrapper<JsonObject> jsonEntity = new ObjectWrapper<>();
        WatchImpl.measure("get entity json", () -> jsonEntity.set(getEntityJson(entity, joinedAttributes.get())));

        return serializer.toJson(jsonEntity.get());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getChildEntities(Request request, Response response) {
        long entityId = getEntityId(request);
        long entityTypeId = getEntityTypeId(request);
        List<Entity> childEntities = PersistenceAdapterImpl.getInstance().getEntities(entityId, entityTypeId);
        List<TypeAttribute> typeAttributesToInclude = getAttributesTypeIdsToInclude(request);

		ObjectWrapper<JsonArray> jsonEntities = new ObjectWrapper<>();
		WatchImpl.measure("getEntitiesJson", () -> jsonEntities.set(getEntitiesJson(childEntities, typeAttributesToInclude)));

        return serializer.toJson(jsonEntities.get());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEventTypesOfEntity(Request request, Response response) {
        long entityId = getEntityId(request);
        boolean includeChildEvents = getIncludeChildEvents(request);

        EntityHierarchyNode entityNode = HierarchyBuilderImpl.getInstance().getEntityHierarchyRootNode().findChildEntity(entityId);

        if (entityNode == null) {
        	halt(HttpStatusCodes.NOT_FOUND, "cannot find entity in hierarchy");
		}

		List<Long> entityIds = new ArrayList<>();

        if (includeChildEvents) {
		 	entityIds.addAll(entityNode.getChildIds());
		} else {
        	entityIds.add(entityId);
		}

		Map<EventType, Long> eventTypesAndEventCount = PersistenceAdapterImpl.getInstance()
				.getEventTypesAndEventCount(entityIds.toArray(new Long[entityIds.size()]));

        JsonArray eventTypesJson = getEventTypesJson(eventTypesAndEventCount);

        return serializer.toJson(eventTypesJson);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEventsOfEntity(Request request, Response response) {
        long entityId = getEntityId(request);
        long eventTypeId = getEntityTypeId(request);
        boolean includeChildEvents = getIncludeChildEvents(request);
        int startIndex = getStartIndex(request);
        int endIndex = getEndIndex(request);

        if (endIndex < startIndex) {
        	int temp = endIndex;
        	endIndex = startIndex;
        	startIndex = temp;
		}

		EntityHierarchyNode entityNode = HierarchyBuilderImpl.getInstance().getEntityHierarchyRootNode().findChildEntity(entityId);

        if (entityNode == null) {
        	halt(HttpStatusCodes.NOT_FOUND, "cannot find entity in hierarchy");
		}

		List<Long> entityIds = new ArrayList<>();

		if (includeChildEvents) {
			entityIds.addAll(entityNode.getChildIds());
		} else {
			entityIds.add(entityId);
		}

        List<Event> events = PersistenceAdapterImpl.getInstance()
				.getEvents(eventTypeId, startIndex, endIndex, entityIds.toArray(new Long[entityIds.size()]));
        JsonArray eventTypesJson = getEventsJson(eventTypeId, events);

        return serializer.toJson(eventTypesJson);
    }

	/**
	 * This method returns an entity as a json object including the given attributes.
	 * @param entity the entity to be returned as json
	 * @param attributes the attributes to be returned in json
	 * @return json object that represents the given entity
	 */
	private JsonObject getEntityJson(Entity entity, List<Pair<TypeAttribute, Attribute>> attributes) {
		try {
			JsonObject jsonEntity = new JsonObject();

			jsonEntity.addProperty("Id", entity.getId());
			jsonEntity.addProperty("TypeId", entity.getTypeId());
			jsonEntity.addProperty("ParentId", entity.getParentId());
			jsonEntity.addProperty("Name", entity.getName());
			jsonEntity.addProperty("Status", entity.getStatus());
			jsonEntity.addProperty("HasChildren",
					HierarchyBuilderImpl.getInstance().getEntityHierarchyRootNode().findChildEntity(entity.getId()).hasChildren());

			JsonArray attributesJson = getAttributesJson(attributes);
			jsonEntity.add("Attributes", attributesJson);

			return jsonEntity;
		} catch (Exception e) {
			LoggerUtilImpl.getInstance().error(logger, "cannot parse event type", e);
			return new JsonObject();
		}
	}

    /**
     * This method returns a list of entities as a json array including the given attributes.
     * @param entities the entities to be returned as json
     * @param typeAttributesToInclude the unique identifiers of the typeAttributes to be returned in json
     * @return json array that represents the given entities
     */
    private JsonArray getEntitiesJson(List<Entity> entities, List<TypeAttribute> typeAttributesToInclude) {
        JsonArray entitiesJson = new JsonArray();

		Map<Long, List<Attribute>> entityAttributes = PersistenceAdapterImpl.getInstance()
				.getAttributes(typeAttributesToInclude, entities.toArray(new Entity[entities.size()]));

		for (Entity entity : entities) {
			if (!entityAttributes.containsKey(entity.getId())) {
				continue;
			}

			List<Pair<TypeAttribute, Attribute>> joinedAttributes = joinAttributes(typeAttributesToInclude, entityAttributes.get(entity.getId()));
			entitiesJson.add(getEntityJson(entity, joinedAttributes));
		}

        return entitiesJson;
    }

    /**
     * This method returns event types as a JsonArray.
     * @param eventTypes - the event types and their number of events
     * @return - a json representation of the event types
     */
    private JsonArray getEventTypesJson(Map<EventType, Long> eventTypes) {
        JsonArray eventTypesJson = new JsonArray();
        for (Map.Entry<EventType, Long> eventTypeEntry : eventTypes.entrySet()) {
            eventTypesJson.add(getEventTypeJson(eventTypeEntry.getKey(), eventTypeEntry.getValue()));
        }
        return eventTypesJson;
    }

	/**
	 * This method returns the json representation of a given eventType.
	 * @param eventType - the eventType to serialize
	 * @param numberOfEvents - the number of events for this eventType
	 * @return - the json representation of the given eventType
	 */
    private JsonObject getEventTypeJson(EventType eventType, long numberOfEvents) {
		try {
			JsonObject jsonEventType = new JsonObject();

			jsonEventType.addProperty("Id", eventType.getId());
			jsonEventType.addProperty("Name", eventType.getName());
			jsonEventType.addProperty("NumberOfEvents", numberOfEvents);
			jsonEventType.addProperty("TimestampAttributeId", eventType.getTimeStampAttributeId());

			return jsonEventType;
		} catch (Exception e) {
			LoggerUtilImpl.getInstance().error(logger, "cannot parse event type", e);
			return new JsonObject();
		}
	}

    /**
     * This method returns events as a JsonArray.
	 * @param eventTypeId - the typeId of the events
	 * @param events - the events
	 * @return - a json representation of the events
     */
    private JsonArray getEventsJson(long eventTypeId, List<Event> events) {
        JsonArray eventsJson = new JsonArray();

        List<TypeAttribute> eventTypeAttributes = PersistenceAdapterImpl.getInstance().getTypeAttributes(eventTypeId);
        Map<Long, List<Attribute>> eventAttributes = PersistenceAdapterImpl.getInstance()
				.getAttributes(eventTypeAttributes, events.toArray(new Event[events.size()]));

        for (Event event : events) {
        	if (!eventAttributes.containsKey(event.getId())) {
        		continue;
			}

			List<Pair<TypeAttribute, Attribute>> joinedAttributes = joinAttributes(eventTypeAttributes, eventAttributes.get(event.getId()));
            JsonArray jsonAttributes = getAttributesJson(joinedAttributes);

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
    private JsonArray getAttributesJson(List<Pair<TypeAttribute, Attribute>> attributes) {
        JsonArray jsonAttributes = new JsonArray();
        for (Pair<TypeAttribute, Attribute> attribute : attributes) {
            JsonObject jsonAttribute = new JsonObject();
            jsonAttribute.addProperty("Name", attribute.getKey().getName());
            jsonAttribute.addProperty("Value", attribute.getValue().getValue());
            jsonAttributes.add(jsonAttribute);
        }
        return jsonAttributes;
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
	 * This method returns the include child events boolean given in the request.
	 * @param request - the request with include child events parameter
	 * @return - the boolean
	 */
	private Boolean getIncludeChildEvents(Request request) {
    	return endpointUtil.validateBoolean(request.params(EntityEndpoint.getIncludeChildEventsParameter(false)));
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
    private List<TypeAttribute> getAttributesTypeIdsToInclude(Request request) {
        List<String> attributeNames =  endpointUtil.validateListOfString(
                request.params(EntityEndpoint.getAttributeNamesParameter(false)),
                (String input) -> input.matches("(\\w|-)+(\\s(\\w|-)+)*"));

        // check if entity type has all given attributes defined
        List<TypeAttribute> attributesOfEntityType = PersistenceAdapterImpl.getInstance().getTypeAttributes(getEntityTypeId(request));
        List<String> entityTypeAttributeNames = new ArrayList<>();
        List<TypeAttribute> typeAttributes = new ArrayList<>();
        for (TypeAttribute att : attributesOfEntityType) {
            entityTypeAttributeNames.add(att.getName());

            if (attributeNames.contains(att.getName())) {
				typeAttributes.add(att);
			}
        }
        if (!entityTypeAttributeNames.containsAll(attributeNames)) {
            halt(HttpStatusCodes.BAD_REQUEST, "invalid attribute name given");
        }
        return typeAttributes;
    }

	/**
	 * This method joins a list of typeAttributes and a list of attributes.
	 * @param typeAttributes - a list of typeAttributes
	 * @param attributes - a list of attributes
	 * @return - a list of joined pairs
	 */
    private List<Pair<TypeAttribute, Attribute>> joinAttributes(List<TypeAttribute> typeAttributes, List<Attribute> attributes) {
    	List<Pair<TypeAttribute, Attribute>> join = new ArrayList<>();

    	for (TypeAttribute typeAttribute : typeAttributes) {
    		for (Attribute attribute : attributes) {
    			if (attribute.getTypeAttributeId() == typeAttribute.getId()) {
    				join.add(new PairImpl<>(typeAttribute, attribute));
				}
			}
		}

		return join;
	}
}
