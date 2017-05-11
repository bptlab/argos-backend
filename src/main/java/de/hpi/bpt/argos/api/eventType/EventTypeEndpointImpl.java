package de.hpi.bpt.argos.api.eventType;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.hpi.bpt.argos.api.RestEndpointCommon;
import de.hpi.bpt.argos.common.EventPlatformFeedback;
import de.hpi.bpt.argos.common.EventProcessingPlatformUpdaterImpl;
import de.hpi.bpt.argos.storage.PersistenceAdapter;
import de.hpi.bpt.argos.storage.PersistenceAdapterImpl;
import de.hpi.bpt.argos.storage.dataModel.attribute.type.TypeAttribute;
import de.hpi.bpt.argos.storage.dataModel.attribute.type.TypeAttributeImpl;
import de.hpi.bpt.argos.storage.dataModel.event.Event;
import de.hpi.bpt.argos.storage.dataModel.event.query.EventQuery;
import de.hpi.bpt.argos.storage.dataModel.event.type.EventType;
import de.hpi.bpt.argos.storage.dataModel.event.type.EventTypeImpl;
import de.hpi.bpt.argos.storage.dataModel.mapping.EventEntityMapping;
import de.hpi.bpt.argos.storage.dataModel.mapping.MappingCondition;
import de.hpi.bpt.argos.util.HttpStatusCodes;
import de.hpi.bpt.argos.util.RestEndpointUtil;
import de.hpi.bpt.argos.util.RestEndpointUtilImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Service;

import java.util.ArrayList;
import java.util.List;

import static spark.Spark.halt;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class EventTypeEndpointImpl implements EventTypeEndpoint {

    private static final Logger logger = LoggerFactory.getLogger(EventTypeEndpointImpl.class);
    private static final Gson serializer = new Gson();
    private static final RestEndpointUtil endpointUtil = RestEndpointUtilImpl.getInstance();
    private static final JsonParser jsonParser = new JsonParser();

    private static final String JSON_NAME_ATTRIBUTE = "Name";
    private static final String JSON_ATTRIBUTES_ATTRIBUTE = "TypeAttributes";
    private static final String JSON_TIMESTAMP_ATTRIBUTE = "TimestampAttributeName";

    /**
     * {@inheritDoc}
     */
    @Override
    public void setup(Service sparkService) {
        sparkService.get(EventTypeEndpoint.getEventTypesBaseUri(),
                (Request request, Response response) ->
                        endpointUtil.executeRequest(logger, request, response, this::getEventTypes));

        sparkService.get(EventTypeEndpoint.getEventTypeBaseUri(),
				(Request request, Response response) ->
						endpointUtil.executeRequest(logger, request, response, this::getEventType));

        sparkService.post(EventTypeEndpoint.getCreateEventTypeBaseUri(),
				(Request request, Response response) ->
						endpointUtil.executeRequest(logger, request, response, this::createEventType));

        sparkService.delete(EventTypeEndpoint.getDeleteEventTypeBaseUri(),
				(Request request, Response response) ->
						endpointUtil.executeRequest(logger, request, response, this::deleteEventType));

        sparkService.get(EventTypeEndpoint.getEventTypeAttributesBaseUri(),
				(Request request, Response response) ->
						endpointUtil.executeRequest(logger, request, response, this::getEventTypeAttributes));

        sparkService.get(EventTypeEndpoint.getEventTypeQueriesBaseUri(),
				(Request request, Response response) ->
						endpointUtil.executeRequest(logger, request, response, this::getEventTypeQueries));

        sparkService.get(EventTypeEndpoint.getEventTypeEntityMappingsBaseUri(),
				(Request request, Response response) ->
						endpointUtil.executeRequest(logger, request, response, this::getEventTypeEntityMappings));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEventTypes(Request request, Response response) {
        List<EventType> eventTypes = PersistenceAdapterImpl.getInstance().getEventTypes();
        JsonArray jsonEventTypesArray = new JsonArray();

        for (EventType eventType : eventTypes) {
            jsonEventTypesArray.add(RestEndpointCommon.getEventTypeJson(eventType));
        }

        return serializer.toJson(jsonEventTypesArray);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEventType(Request request, Response response) {
        long eventTypeId = getEventTypeId(request);
        EventType eventType = PersistenceAdapterImpl.getInstance().getEventType(eventTypeId);
        JsonObject jsonEventType = RestEndpointCommon.getEventTypeJson(eventType);

        return serializer.toJson(jsonEventType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String createEventType(Request request, Response response) {
		JsonObject jsonEventType = jsonParser.parse(request.body()).getAsJsonObject();

		if (jsonEventType == null) {
			halt(HttpStatusCodes.BAD_REQUEST, "no event type given in body");
		}

		EventType eventType = createEventTypeFromJson(jsonEventType);

		if (eventType == null) {
			halt(HttpStatusCodes.BAD_REQUEST, "event type name already in use, or failed to parse event type");
		}

		PersistenceAdapterImpl.getInstance().createArtifact(eventType, EventTypeEndpoint.getEventTypeUri(eventType.getId()));

		return "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String deleteEventType(Request request, Response response) {
        long eventTypeId = getEventTypeId(request);

        EventType eventType = PersistenceAdapterImpl.getInstance().getEventType(eventTypeId);
        String blockingEventTypes;
        if (eventType == null) {
            halt(HttpStatusCodes.BAD_REQUEST, "cannot find event type");
        }
		if (!eventType.isDeletable()) {
			halt(HttpStatusCodes.FORBIDDEN, "you must not delete this event type");
		}

		blockingEventTypes = getBlockingEventTypes(eventType);
		if (blockingEventTypes.isEmpty()) {
			// delete queries
			List<EventQuery> queries = PersistenceAdapterImpl.getInstance().getEventQueries(eventTypeId);
			EventQuery[] queryArray = new EventQuery[queries.size()];
			if (!PersistenceAdapterImpl.getInstance().deleteArtifacts(queries.toArray(queryArray))) {
				halt(HttpStatusCodes.ERROR, "could not delete corresponding queries of event type");
			}

			// delete attributes
			List<TypeAttribute> attributes = PersistenceAdapterImpl.getInstance().getTypeAttributes(eventTypeId);
			TypeAttribute[] attributeArray = new TypeAttribute[attributes.size()];
			if (!PersistenceAdapterImpl.getInstance().deleteArtifacts(attributes.toArray(attributeArray))) {
				halt(HttpStatusCodes.ERROR, "could not delete corresponding attributes of event type");
			}

			// delete events
			List<Event> events = PersistenceAdapterImpl.getInstance().getEventsOfEventType(eventTypeId);
			Event[] eventArray = new Event[events.size()];
			if (!PersistenceAdapterImpl.getInstance().deleteArtifacts(events.toArray(eventArray))) {
				halt(HttpStatusCodes.ERROR, "could not delete corresponding events of event type");
			}

			// delete mappings
			List<EventEntityMapping> mappings = PersistenceAdapterImpl.getInstance().getEventEntityMappingsForEventType(eventTypeId);
			EventEntityMapping[] mappingArray = new EventEntityMapping[mappings.size()];
			if (!PersistenceAdapterImpl.getInstance().deleteArtifacts(mappings.toArray(mappingArray))) {
				halt(HttpStatusCodes.ERROR, "could not delete corresponding mappings of event type");
			}

			deleteMappingConditions(mappings);

			// delete event type
			if (!PersistenceAdapterImpl.getInstance().deleteArtifact(eventType,
					EventTypeEndpoint.getDeleteEventTypeUri(eventType.getId()))) {
				halt(HttpStatusCodes.ERROR, "could not delete event type");
			}

			response.status(HttpStatusCodes.SUCCESS);
		} else {
			response.status(HttpStatusCodes.ERROR);
			return blockingEventTypes;
		}

        return "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEventTypeAttributes(Request request, Response response) {
		long eventTypeId = getEventTypeId(request);
		List<TypeAttribute> typeAttributes = PersistenceAdapterImpl.getInstance().getTypeAttributes(eventTypeId);

		return serializer.toJson(RestEndpointCommon.getTypeAttributesJson(typeAttributes));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEventTypeQueries(Request request, Response response) {
        JsonArray jsonTypeQueriesArray = new JsonArray();

		long eventTypeId = getEventTypeId(request);
		List<EventQuery> queries = PersistenceAdapterImpl.getInstance().getEventQueries(eventTypeId);

		for (EventQuery query : queries) {
			JsonObject jsonTypeAttribute = new JsonObject();

			jsonTypeAttribute.addProperty("Id", query.getId());
			jsonTypeAttribute.addProperty("Description", query.getDescription());
			jsonTypeAttribute.addProperty("Query", query.getQuery());

			jsonTypeQueriesArray.add(jsonTypeAttribute);
		}

		return serializer.toJson(jsonTypeQueriesArray);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEventTypeEntityMappings(Request request, Response response) {
        long eventTypeId = getEventTypeId(request);
        List<EventEntityMapping> entityMappings = PersistenceAdapterImpl.getInstance().getEventEntityMappingsForEventType(eventTypeId);

        return serializer.toJson(RestEndpointCommon.getEventEntityMappingsJson(entityMappings));
    }

    /**
     * This method creates an event type out of a json.
     * @param jsonEventType the json the event type should be created from
     * @return the resulting event type
     */
    private EventType createEventTypeFromJson(JsonObject jsonEventType) {

        EventType eventType = new EventTypeImpl();
		String eventTypeName = jsonEventType.get(JSON_NAME_ATTRIBUTE).getAsString();
        if (eventTypeName == null) {
            return null;
        }
        eventType.setName(eventTypeName);

        // event types should be deletable
        eventType.setDeletable(true);

        // but they must not be registered, since they do use INSERT INTO in their queries
        eventType.setShouldBeRegistered(false);

        List<EventType> existingEventTypes = PersistenceAdapterImpl.getInstance().getEventTypes();
        for (EventType existingEventType : existingEventTypes) {
            if (existingEventType.getName().equals(eventType.getName())) {
                return null;
            }
        }

		String timestampAttributeName = jsonEventType.get(JSON_TIMESTAMP_ATTRIBUTE).getAsString();
		if (timestampAttributeName == null) {
			return null;
		}

        JsonArray jsonAttributes = jsonEventType.getAsJsonArray(JSON_ATTRIBUTES_ATTRIBUTE);
        if (jsonAttributes == null) {
            return null;
        }

        // check that there are no doubled names
        List<String> usedNames = new ArrayList<>();
        for (JsonElement jsonAttribute : jsonAttributes) {
            String attributeName = jsonAttribute.getAsJsonObject().get(JSON_NAME_ATTRIBUTE).getAsString();
            if (usedNames.contains(attributeName)) {
                return null;
            }
            usedNames.add(attributeName);
        }

		PersistenceAdapterImpl.getInstance().saveArtifacts(eventType);
        List<TypeAttribute> typeAttributes = new ArrayList<>();
        TypeAttribute timestampAttribute = createEventTypeAttribute(eventType, timestampAttributeName);
        typeAttributes.add(timestampAttribute);
		for (JsonElement jsonAttribute : jsonAttributes) {
            String attributeName = jsonAttribute.getAsJsonObject().get(JSON_NAME_ATTRIBUTE).getAsString();

            if (attributeName.equals(timestampAttributeName)) {
            	continue;
			}

            typeAttributes.add(createEventTypeAttribute(eventType, attributeName));
        }

        PersistenceAdapterImpl.getInstance().saveArtifacts(typeAttributes.toArray(new TypeAttribute[typeAttributes.size()]));
		eventType.setTimeStampAttributeId(timestampAttribute.getId());

        return eventType;
    }


    /**
     * This method creates a new event attribute and attaches it to an existing event type.
     * @param eventType - the type which should get expanded
     * @param attributeName - the name of the new attribute
     * @return type attribute with given event type and name
     */
    private TypeAttribute createEventTypeAttribute(EventType eventType, String attributeName) {
        TypeAttribute attribute = new TypeAttributeImpl();
        attribute.setName(attributeName);
        attribute.setTypeId(eventType.getId());
        return attribute;
    }

    /**
     * This method returns all event types with queries that need the given event type.
     * @param eventType the event type blocking event types are searched for
     * @return a json string of blocking event types, empty if none
     */
    private String getBlockingEventTypes(EventType eventType) {
        List<EventType> eventTypes = PersistenceAdapterImpl.getInstance().getEventTypes();

        JsonArray blockingEventTypeIds = new JsonArray();
        for (EventType type : eventTypes) {
            if (type.getId() == eventType.getId()) {
                continue;
            }

            List<EventQuery> eventQueries = PersistenceAdapterImpl.getInstance().getEventQueries(type.getId());
            for (EventQuery query : eventQueries) {
                if (query.getQuery().contains(eventType.getName())) {
                    blockingEventTypeIds.add(type.getId());
                }
            }
        }
        if (blockingEventTypeIds.size() == 0) {
            return "";
        } else {
            return serializer.toJson(blockingEventTypeIds);
        }
    }

    /**
     * This method returns the id given in request.
     * @param request the request with event type id
     * @return id of the event type in request
     */
    private long getEventTypeId(Request request) {
        return endpointUtil.validateLong(
                request.params(EventTypeEndpoint.getEventTypeIdParameter(false)),
                (Long input) -> input > 0);
    }

    /**
     * This method deletes all mapping conditions associated with the given mappings.
     * @param mappings mappings to delete the conditions for
     */
    private void deleteMappingConditions(List<EventEntityMapping> mappings) {
        for (EventEntityMapping mapping : mappings) {
            List<MappingCondition> conditions = PersistenceAdapterImpl.getInstance().getMappingConditions(mapping.getId());
            MappingCondition[] conditionArray = new MappingCondition[mappings.size()];
            if (!PersistenceAdapterImpl.getInstance().deleteArtifacts(conditions.toArray(conditionArray))) {
                halt(HttpStatusCodes.ERROR, "could not delete corresponding mapping conditions of event type");
            }
        }
    }
}
