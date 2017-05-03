package de.hpi.bpt.argos.api.entityMapping;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import de.hpi.bpt.argos.api.eventType.EventTypeEndpoint;
import de.hpi.bpt.argos.api.eventType.EventTypeEndpointImpl;
import de.hpi.bpt.argos.storage.PersistenceAdapterImpl;
import de.hpi.bpt.argos.storage.dataModel.mapping.EventEntityMapping;
import de.hpi.bpt.argos.storage.dataModel.mapping.EventEntityMappingImpl;
import de.hpi.bpt.argos.storage.dataModel.mapping.MappingCondition;
import de.hpi.bpt.argos.storage.dataModel.mapping.MappingConditionImpl;
import de.hpi.bpt.argos.util.HttpStatusCodes;
import de.hpi.bpt.argos.util.LoggerUtilImpl;
import de.hpi.bpt.argos.util.RestEndpointUtil;
import de.hpi.bpt.argos.util.RestEndpointUtilImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.HaltException;
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
public class EntityMappingEndpointImpl implements  EntityMappingEndpoint {

    private static final Logger logger = LoggerFactory.getLogger(EventTypeEndpointImpl.class);
    private static final RestEndpointUtil endpointUtil = RestEndpointUtilImpl.getInstance();
    private static final JsonParser jsonParser = new JsonParser();

    private static final String JSON_ENTITY_MAPPING_ATTRIBUTE = "EventEntityMapping";
    private static final String EVENT_TYPE_ID_ATTRIBUTE = "EventTypeId";
    private static final String ENTITY_TYPE_ID_ATTRIBUTE = "EntityTypeId";
    private static final String MAPPING_CONDITIONS_ATTRIBUTE = "EventEntityMappingCondition";
    private static final String EVENT_TYPE_ATTRIBUTE_ID_ATTRIBUTE = "EventTypeAttributeId";
    private static final String ENTITY_TYPE_ATTRIBUTE_ID_ATTRIBUTE = "EntityTypeAttributeId";
    private static final String TARGET_STATUS_ATTRIBUTE = "TargetStatus";

    private static final String JSON_PARSE_ERROR_MESSAGE = "cannot parse request body to event mapping";

    /**
     * {@inheritDoc}
     */
    @Override
    public void setup(Service sparkService) {
        sparkService.post(EntityMappingEndpoint.getCreateEntityMappingBaseUri(), this::createEntityMapping);
        sparkService.delete(EntityMappingEndpoint.getDeleteEntityMappingBaseUri(), this::deleteEntityMapping);
        sparkService.put(EntityMappingEndpoint.getEditEntityMappingBaseUri(), this::editEntityMapping);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String createEntityMapping(Request request, Response response) {
        endpointUtil.logReceivedRequest(logger, request);

        try {
            JsonObject jsonBody = jsonParser.parse(request.body()).getAsJsonObject();
            JsonObject jsonMapping = jsonBody.get(JSON_ENTITY_MAPPING_ATTRIBUTE).getAsJsonObject();

            if (jsonMapping == null) {
                halt(HttpStatusCodes.BAD_REQUEST, "no event mapping given in body");
            }

            createEventMappingFromJson(jsonMapping);

        } catch (HaltException halt) {
            LoggerUtilImpl.getInstance().error(logger,
                    String.format("cannot create event query: %1$s -> %2$s", halt.statusCode(), halt.body()), halt);
            throw halt;
        } catch (Exception e) {
            LoggerUtilImpl.getInstance().error(logger, JSON_PARSE_ERROR_MESSAGE, e);
            halt(HttpStatusCodes.ERROR, e.getMessage());
        }

        endpointUtil.logSendingResponse(logger, request, response.status(), response.body());
        return response.body();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String deleteEntityMapping(Request request, Response response) {
        endpointUtil.logReceivedRequest(logger, request);

        long mappingId = getMappingId(request);
        EventEntityMapping mapping = PersistenceAdapterImpl.getInstance().getEventEntityMapping(mappingId);
        if (mapping == null) {
            halt(HttpStatusCodes.BAD_REQUEST, "given mapping was not found");
        }

        // delete corresponding mapping conditions
        List<MappingCondition> conditions = PersistenceAdapterImpl.getInstance().getMappingConditions(mappingId);
        if (!PersistenceAdapterImpl.getInstance().deleteArtifacts(conditions.toArray(new MappingCondition[conditions.size()]))) {
            halt(HttpStatusCodes.ERROR, "could not delete mapping conditions");
        }

        if (!PersistenceAdapterImpl.getInstance().deleteArtifacts(mapping)) {
            halt(HttpStatusCodes.ERROR, "could not delete mapping");
        }

        endpointUtil.logSendingResponse(logger, request, response.status(), response.body());
        return response.body();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String editEntityMapping(Request request, Response response) {
        endpointUtil.logReceivedRequest(logger, request);

        JsonObject jsonBody = new JsonObject();
        try {
            jsonBody = jsonParser.parse(request.body()).getAsJsonObject();
        } catch (JsonSyntaxException e) {
            LoggerUtilImpl.getInstance().error(logger, JSON_PARSE_ERROR_MESSAGE, e);
            halt(HttpStatusCodes.BAD_REQUEST, "not a valid json");
        }

        JsonObject jsonMapping = jsonBody.get(JSON_ENTITY_MAPPING_ATTRIBUTE).getAsJsonObject();
        if (jsonMapping == null) {
            halt(HttpStatusCodes.BAD_REQUEST, "no event mapping given in body");
        }

        EventEntityMapping oldMapping = PersistenceAdapterImpl.getInstance().getEventEntityMapping(getMappingId(request));

        // update conditions
        List<MappingCondition> oldConditions = PersistenceAdapterImpl.getInstance().getMappingConditions(oldMapping.getId());
        if (!PersistenceAdapterImpl.getInstance().deleteArtifacts(oldConditions.toArray(new MappingCondition[oldConditions.size()]))) {
            halt(HttpStatusCodes.ERROR, "could not delete conditions");
        }

        JsonArray jsonConditions = jsonMapping.get(MAPPING_CONDITIONS_ATTRIBUTE).getAsJsonArray();
        List<MappingCondition> newConditions = getConditionsFromJson(jsonConditions, oldMapping.getId());
        if (!PersistenceAdapterImpl.getInstance().saveArtifacts(newConditions.toArray(new MappingCondition[newConditions.size()]))) {
            halt(HttpStatusCodes.ERROR, "could not create new conditions");
        }

        // update mapping
        EventEntityMapping newMapping = getEventMappingFromJson(jsonMapping);
        oldMapping.setEventTypeId(newMapping.getEventTypeId());
        oldMapping.setEntityTypeId(newMapping.getEntityTypeId());
        oldMapping.setTargetStatus(newMapping.getTargetStatus());

        String mappingsUri =  EventTypeEndpoint.getEventTypeEntityMappingsUri(oldMapping.getEventTypeId());
        if (!PersistenceAdapterImpl.getInstance().updateArtifact(oldMapping, mappingsUri)) {
            halt(HttpStatusCodes.ERROR, "could not update mapping");
        }

        endpointUtil.logSendingResponse(logger, request, response.status(), response.body());
        return response.body();
    }

    /**
     * This method returns the mapping given by json.
     * @param jsonMapping a json containing a mapping
     * @return the mapping described by json
     */
    private EventEntityMapping getEventMappingFromJson(JsonObject jsonMapping) {
        EventEntityMapping mapping = new EventEntityMappingImpl();
        mapping.setEventTypeId(jsonMapping.get(EVENT_TYPE_ID_ATTRIBUTE).getAsInt());
        mapping.setEntityTypeId(jsonMapping.get(ENTITY_TYPE_ID_ATTRIBUTE).getAsInt());
        mapping.setTargetStatus(jsonMapping.get(TARGET_STATUS_ATTRIBUTE).getAsString());
        return mapping;
    }

    /**
     * This method creates a mapping, including saving it to the database, from a json.
     * @param jsonMapping a json object including the description of a mapping
     * @return the created mapping
     */
    private EventEntityMapping createEventMappingFromJson(JsonObject jsonMapping) {
        EventEntityMapping newMapping = new EventEntityMappingImpl();

        try {
            newMapping = getEventMappingFromJson(jsonMapping);
            String mappingsUri =  EventTypeEndpoint.getEventTypeEntityMappingsUri(newMapping.getEventTypeId());
            if (!PersistenceAdapterImpl.getInstance().createArtifact(newMapping, mappingsUri)) {
                halt(HttpStatusCodes.ERROR, "could not save mapping into database");
            }

            JsonArray jsonConditions = jsonMapping.get(MAPPING_CONDITIONS_ATTRIBUTE).getAsJsonArray();
            List<MappingCondition> conditions = getConditionsFromJson(jsonConditions, newMapping.getId());
            if (!PersistenceAdapterImpl.getInstance().saveArtifacts(conditions.toArray(new MappingCondition[conditions.size()]))) {
                halt(HttpStatusCodes.ERROR, "could not save mapping into database");
            }

        } catch (ClassCastException | IllegalStateException e) {
            LoggerUtilImpl.getInstance().error(logger, JSON_PARSE_ERROR_MESSAGE, e);
            halt(HttpStatusCodes.BAD_REQUEST, e.getMessage());
        }

        return newMapping;
    }

    /**
     * This method returns conditions given as json in json array.
     * @param jsonConditions conditions as json array
     * @param mappingId id of the mapping, the conditions belong to
     * @return the conditions given in json array
     */
    private List<MappingCondition> getConditionsFromJson(JsonArray jsonConditions, long mappingId) {
        List<MappingCondition> conditions = new ArrayList<>();
        for (JsonElement jsonCondition : jsonConditions) {
            JsonObject jsonConditionObject = jsonCondition.getAsJsonObject();

            MappingCondition newCondition = new MappingConditionImpl();
            newCondition.setEventTypeAttributeId(jsonConditionObject.get(EVENT_TYPE_ATTRIBUTE_ID_ATTRIBUTE).getAsLong());
            newCondition.setEntityTypeAttributeId(jsonConditionObject.get(ENTITY_TYPE_ATTRIBUTE_ID_ATTRIBUTE).getAsLong());
            newCondition.setMappingId(mappingId);

            conditions.add(newCondition);
        }

        return conditions;
    }

    /**
     * This method returns the id of the mapping given in request.
     * @param request the request
     * @return id of the mapping given in request
     */
    private long getMappingId(Request request) {
        return endpointUtil.validateLong(
                request.params(EntityMappingEndpoint.getEntityMappingIdParameter(false)),
                (Long input) -> input > 0);
    }
}
