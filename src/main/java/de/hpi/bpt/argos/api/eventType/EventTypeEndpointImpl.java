package de.hpi.bpt.argos.api.eventType;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.Gson;
import de.hpi.bpt.argos.common.EventPlatformFeedback;
import de.hpi.bpt.argos.common.EventProcessingPlatformUpdaterImpl;
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
import de.hpi.bpt.argos.util.LoggerUtilImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.HaltException;
import spark.Request;
import spark.Response;
import spark.Service;
import static spark.Spark.halt;

import java.util.List;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class EventTypeEndpointImpl implements EventTypeEndpoint {

    private static final Logger logger = LoggerFactory.getLogger(EventTypeEndpointImpl.class);
    private static final Gson serializer = new Gson();
    private static final RestEndpointUtil endpointUtil = RestEndpointUtilImpl.getInstance();
    private static final JsonParser jsonParser = new JsonParser();

    private static final String JSON_EVENT_TYPE_ATTRIBUTE = "EventType";
    private static final String JSON_NAME_ATTRIBUTE = "Name";
    private static final String JSON_ATTRIBUTES_ATTRIBUTE = "TypeAttributes";
    private static final String JSON_TIMESTAMP_ATTRIBUTE = "TimeStampAttributeName";

    /**
     * {@inheritDoc}
     */
    @Override
    public void setup(Service sparkService) {
        sparkService.get(EventTypeEndpoint.getEventTypesBaseUri(), this::getEventType);
        sparkService.get(EventTypeEndpoint.getEventTypeBaseUri(), this::getEventTypes);
        sparkService.post(EventTypeEndpoint.getCreateEventTypeBaseUri(), this::createEventType);
        sparkService.delete(EventTypeEndpoint.getDeleteEventTypeBaseUri(), this::deleteEventType);
        sparkService.get(EventTypeEndpoint.getEventTypeAttributesBaseUri(), this::getEventTypeAttributes);
        sparkService.get(EventTypeEndpoint.getEventTypeQueriesBaseUri(), this::getEventTypeQueries);
        sparkService.get(EventTypeEndpoint.getEventTypeEntityMappingsBaseUri(), this::getEventTypeEntityMappings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEventTypes(Request request, Response response) {
        endpointUtil.logReceivedRequest(logger, request);

        List<EventType> eventTypes = PersistenceAdapterImpl.getInstance().getEventTypes();
        JsonArray jsonEventTypes = new JsonArray();
        // TODO where does the key for each event type come from? ("EventType{...}")
        for (EventType eventType : eventTypes) {
            jsonEventTypes.add(getEventTypeJson(eventType));
        }

        endpointUtil.logSendingResponse(logger, request, response.status(), serializer.toJson(jsonEventTypes));
        return serializer.toJson(jsonEventTypes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEventType(Request request, Response response) {
        endpointUtil.logReceivedRequest(logger, request);

        long eventTypeId = getEventTypeId(request);
        EventType eventType = PersistenceAdapterImpl.getInstance().getEventType(eventTypeId);
        JsonObject jsonEventType = getEventTypeJson(eventType);

        endpointUtil.logSendingResponse(logger, request, response.status(), serializer.toJson(jsonEventType));
        return serializer.toJson(jsonEventType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String createEventType(Request request, Response response) {
        endpointUtil.logReceivedRequest(logger, request);

        try {
            JsonObject jsonBody = jsonParser.parse(request.body()).getAsJsonObject();
            JsonObject jsonEventType = jsonBody.get(JSON_EVENT_TYPE_ATTRIBUTE).getAsJsonObject();

            if (jsonEventType == null) {
                halt(HttpStatusCodes.BAD_REQUEST, "no event type given in body");
            }

            EventType eventType = createEventTypeFromJson(jsonEventType);

            if (eventType == null) {
                halt(HttpStatusCodes.BAD_REQUEST, "event type name already in use, or failed to parse event type");
            } else {
                EventPlatformFeedback feedback = EventProcessingPlatformUpdaterImpl.getInstance().registerEventType(eventType);

                if (!feedback.isSuccessful()) {
                    halt(HttpStatusCodes.ERROR, String.format("cannot register event type: %1$s", feedback.getResponseText()));
                }

                PersistenceAdapterImpl.getInstance().createArtifact(eventType, EventTypeEndpoint.getEventTypeUri(eventType.getId()));
            }

        } catch (HaltException halt) {
            logger.info(String.format("cannot create event type: %1$s -> %2$s", halt.statusCode(), halt.body()));
            throw halt;
        } catch (Exception e) {
            LoggerUtilImpl.getInstance().error(logger, "cannot parse request body to event type", e);
            halt(HttpStatusCodes.ERROR, e.getMessage());
        }
        endpointUtil.logSendingResponse(logger, request, response.status(), "");
        return "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String deleteEventType(Request request, Response response) {
        endpointUtil.logReceivedRequest(logger, request);

        long eventTypeId = getEventTypeId(request);

        EventType eventType = PersistenceAdapterImpl.getInstance().getEventType(eventTypeId);
        String blockingEventTypes;
        if (eventType == null) {
            halt(HttpStatusCodes.BAD_REQUEST, "cannot find event type");
        } else {
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
        }
        endpointUtil.logSendingResponse(logger, request, response.status(), "");
        return "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEventTypeAttributes(Request request, Response response) {
        endpointUtil.logReceivedRequest(logger, request);
        JsonArray jsonTypeAttributes = new JsonArray();

        try {
            long eventTypeId = getEventTypeId(request);
            List<TypeAttribute> attributes = PersistenceAdapterImpl.getInstance().getTypeAttributes(eventTypeId);

            for (TypeAttribute attribute : attributes) {
                JsonObject jsonTypeAttribute = new JsonObject();

                jsonTypeAttribute.addProperty("Id", attribute.getId());
                jsonTypeAttribute.addProperty("Name", attribute.getName());

                jsonTypeAttributes.add(jsonTypeAttribute);
            }
        } catch (Exception e) {
            LoggerUtilImpl.getInstance().error(logger, "cannot parse request body to type attributes", e);
            halt(HttpStatusCodes.BAD_REQUEST, e.getMessage());
        }
        endpointUtil.logSendingResponse(logger, request, response.status(), serializer.toJson(jsonTypeAttributes));
        return serializer.toJson(jsonTypeAttributes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEventTypeQueries(Request request, Response response) {
        endpointUtil.logReceivedRequest(logger, request);
        JsonArray jsonTypeQueries = new JsonArray();

        try {
            long eventTypeId = getEventTypeId(request);
            List<EventQuery> queries = PersistenceAdapterImpl.getInstance().getEventQueries(eventTypeId);

            for (EventQuery query : queries) {
                JsonObject jsonTypeAttribute = new JsonObject();

                jsonTypeAttribute.addProperty("Id", query.getId());
                jsonTypeAttribute.addProperty("Description", query.getDescription());
                jsonTypeAttribute.addProperty("Query", query.getQuery());

                jsonTypeQueries.add(jsonTypeAttribute);
            }
        } catch (Exception e) {
            LoggerUtilImpl.getInstance().error(logger, "cannot parse request body to event queries", e);
            halt(HttpStatusCodes.ERROR, e.getMessage());
        }
        endpointUtil.logSendingResponse(logger, request, response.status(), serializer.toJson(jsonTypeQueries));
        return serializer.toJson(jsonTypeQueries);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEventTypeEntityMappings(Request request, Response response) {
        endpointUtil.logReceivedRequest(logger, request);
        JsonArray jsonEntityMappings = new JsonArray();

        try {
            long eventTypeId = getEventTypeId(request);
            List<EventEntityMapping> entityMappings = PersistenceAdapterImpl.getInstance().getEventEntityMappingsForEntityType(eventTypeId);

            for (EventEntityMapping entityMapping : entityMappings) {
                JsonObject jsonEntityMapping = new JsonObject();

                jsonEntityMapping.addProperty("Id", entityMapping.getId());
                jsonEntityMapping.addProperty("EventTypeId", entityMapping.getEventTypeId());
                jsonEntityMapping.addProperty("EntityTypeId", entityMapping.getEntityTypeId());
                jsonEntityMapping.addProperty("TargetStatus", entityMapping.getTargetStatus());

                // add mapping conditions as array
                JsonArray jsonMappingConditions = new JsonArray();
                List<MappingCondition> mappingConditions = PersistenceAdapterImpl.getInstance()
                        .getMappingConditionsForMapping(entityMapping.getId());
                for (MappingCondition condition : mappingConditions) {
                    JsonObject jsonCondition = new JsonObject();
                    jsonCondition.addProperty("EventTypeAttributeId", condition.getEventTypeAttributeId());
                    jsonCondition.addProperty("EntityTypeAttributeId", condition.getEntityTypeAttributeId());
                    jsonMappingConditions.add(jsonCondition);
                }
                jsonEntityMapping.add("EventEntityMappingConditions", jsonMappingConditions);

                jsonEntityMappings.add(jsonEntityMapping);
            }
        } catch (Exception e) {
            LoggerUtilImpl.getInstance().error(logger, "event type entity mappings returned", e);
            halt(HttpStatusCodes.ERROR, e.getMessage());
        }
        endpointUtil.logSendingResponse(logger, request, response.status(), serializer.toJson(jsonEntityMappings));
        return serializer.toJson(jsonEntityMappings);
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
     * This method creates an event type out of a json.
     * @param json the json the event type should be created from
     * @return the resulting event type
     */
    private EventType createEventTypeFromJson(JsonObject json) {
        JsonObject jsonEventType = json.get(JSON_EVENT_TYPE_ATTRIBUTE).getAsJsonObject();
        if (jsonEventType == null) {
            return null;
        }

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

        JsonArray jsonAttributes = jsonEventType.getAsJsonArray(JSON_ATTRIBUTES_ATTRIBUTE);
        if (jsonAttributes == null) {
            return null;
        }
        for (JsonElement jsonAttribute : jsonAttributes) {
            String attributeName = jsonAttribute.getAsJsonObject().get(JSON_NAME_ATTRIBUTE).getAsString();
            createEventTypeAttribute(eventType, attributeName);
        }

        String timestampAttributeName = jsonEventType.get(JSON_TIMESTAMP_ATTRIBUTE).getAsString();
        if (timestampAttributeName == null) {
            return null;
        }
        TypeAttribute timestampAttribute = createEventTypeAttribute(eventType, timestampAttributeName);
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

        if (!PersistenceAdapterImpl.getInstance().saveArtifacts(attribute)) {
            halt(HttpStatusCodes.ERROR, "event type attribute could not be saved: " + attribute);
        }
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
}
