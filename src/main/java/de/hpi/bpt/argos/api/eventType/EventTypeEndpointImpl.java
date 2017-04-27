package de.hpi.bpt.argos.api.eventType;

import com.google.gson.*;
import de.hpi.bpt.argos.common.EventPlatformFeedback;
import de.hpi.bpt.argos.common.EventProcessingPlatformUpdaterImpl;
import de.hpi.bpt.argos.storage.PersistenceAdapterImpl;
import de.hpi.bpt.argos.storage.dataModel.attribute.type.TypeAttribute;
import de.hpi.bpt.argos.storage.dataModel.attribute.type.TypeAttributeImpl;
import de.hpi.bpt.argos.storage.dataModel.event.type.EventType;
import de.hpi.bpt.argos.storage.dataModel.event.type.EventTypeImpl;
import de.hpi.bpt.argos.util.HttpStatusCodes;
import de.hpi.bpt.argos.util.RestEndpointUtil;
import de.hpi.bpt.argos.util.RestEndpointUtilImpl;
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

    @Override
    public String getEventTypes(Request request, Response response) {
        List<EventType> eventTypes = PersistenceAdapterImpl.getInstance().getEventTypes();
        JsonArray jsonEventTypes = new JsonArray();
        // TODO where does the key for each event type come from? ("EventType{...}")
        for (EventType eventType : eventTypes) {
            jsonEventTypes.add(getEventTypeJson(eventType));
        }

        return serializer.toJson(jsonEventTypes);
    }

    @Override
    public String getEventType(Request request, Response response) {
        endpointUtil.logReceivedRequest(logger, request);

        long eventTypeId = getEventTypeId(request);
        EventType eventType = PersistenceAdapterImpl.getInstance().getEventType(eventTypeId);
        JsonObject jsonEventTypes = getEventTypeJson(eventType);

        return serializer.toJson(jsonEventTypes);
    }

    @Override
    public String createEventType(Request request, Response response) {
        endpointUtil.logReceivedRequest(logger, request);

        try {
            JsonObject jsonBody = jsonParser.parse(request.body()).getAsJsonObject();
            JsonObject jsonEventType = jsonBody.get(JSON_EVENT_TYPE_ATTRIBUTE).getAsJsonObject();

            if (jsonEventType == null) {
                halt(HttpStatusCodes.ERROR, "no event type given in body");
            }

            EventType eventType = createEventTypeFromJson(jsonEventType);

            if (eventType == null) {
                halt(HttpStatusCodes.ERROR, "event type name already in use, or failed to parse event type");
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
            logger.error("cannot parse request body to event type '" + request.body() + "'");
            logTrace(e);
            halt(HttpStatusCodes.ERROR, e.getMessage());
        }
        return null;
    }

    @Override
    public String deleteEventType(Request request, Response response) {
        return null;
    }

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
            logger.error("cannot parse request body to type attributes '" + request.body() + "'");
            logTrace(e);
            halt(HttpStatusCodes.ERROR, e.getMessage());
        }
        return serializer.toJson(jsonTypeAttributes);
    }

    @Override
    public String getEventTypeQueries(Request request, Response response) {
        return null;
    }

    @Override
    public String getEventTypeEntityMappings(Request request, Response response) {
        return null;
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
            logger.error("cannot parse event type");
            return new JsonObject();
        }
    }

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

    private long getEventTypeId(Request request) {
        return endpointUtil.validateLong(
                request.params(EventTypeEndpoint.getEventTypeIdParameter(false)),
                (Long input) -> input > 0);
    }

    /**
     * Logs an exception on log level trace.
     * @param e - exception to be logged.
     */
    private void logTrace(Exception e) {
        logger.trace("Reason: ", e);
    }
}
