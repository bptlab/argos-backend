package de.hpi.bpt.argos.api.entityMapping;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import de.hpi.bpt.argos.api.eventType.EventTypeEndpoint;
import de.hpi.bpt.argos.api.eventType.EventTypeEndpointImpl;
import de.hpi.bpt.argos.storage.PersistenceAdapterImpl;
import de.hpi.bpt.argos.storage.dataModel.attribute.type.TypeAttribute;
import de.hpi.bpt.argos.storage.dataModel.event.type.StatusUpdatedEventType;
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
    private static final String EVENT_TYPE_ID_ATTRIBUTE = "EventTypeId";
    private static final String ENTITY_TYPE_ID_ATTRIBUTE = "EntityTypeId";
    private static final String MAPPING_CONDITIONS_ATTRIBUTE = "EventEntityMappingConditions";
    private static final String EVENT_TYPE_ATTRIBUTE_ID_ATTRIBUTE = "EventTypeAttributeId";
    private static final String ENTITY_TYPE_ATTRIBUTE_ID_ATTRIBUTE = "EntityTypeAttributeId";
    private static final String TARGET_STATUS_ATTRIBUTE = "TargetStatus";

    private static final String JSON_PARSE_ERROR_MESSAGE = "cannot parse request body to event mapping";

    /**
     * {@inheritDoc}
     */
    @Override
    public void setup(Service sparkService) {
        sparkService.post(EntityMappingEndpoint.getCreateEntityMappingBaseUri(),
				(Request request, Response response) ->
						endpointUtil.executeRequest(logger, request, response, this::createEntityMapping));

        sparkService.delete(EntityMappingEndpoint.getDeleteEntityMappingBaseUri(),
				(Request request, Response response) ->
						endpointUtil.executeRequest(logger, request, response, this::deleteEntityMapping));

        sparkService.put(EntityMappingEndpoint.getEditEntityMappingBaseUri(),
				(Request request, Response response) ->
						endpointUtil.executeRequest(logger, request, response, this::editEntityMapping));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String createEntityMapping(Request request, Response response) {
		JsonObject jsonMapping = jsonParser.parse(request.body()).getAsJsonObject();

		if (jsonMapping == null) {
			halt(HttpStatusCodes.BAD_REQUEST, "no event mapping given in body");
		}

		createEventMappingFromJson(jsonMapping);
		return "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String deleteEntityMapping(Request request, Response response) {
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

        String mappingsUri =  EventTypeEndpoint.getEventTypeEntityMappingsUri(mapping.getEventTypeId());
        if (!PersistenceAdapterImpl.getInstance().deleteArtifact(mapping, mappingsUri)) {
            halt(HttpStatusCodes.ERROR, "could not delete mapping");
        }

        return "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String editEntityMapping(Request request, Response response) {
        JsonObject jsonMapping = new JsonObject();
        try {
			jsonMapping = jsonParser.parse(request.body()).getAsJsonObject();
        } catch (JsonSyntaxException e) {
            LoggerUtilImpl.getInstance().error(logger, JSON_PARSE_ERROR_MESSAGE, e);
            halt(HttpStatusCodes.BAD_REQUEST, "not a valid json");
        }

        if (jsonMapping == null) {
            halt(HttpStatusCodes.BAD_REQUEST, "no event mapping given in body");
        }

		EventEntityMapping newMapping = getEventMappingFromJson(jsonMapping);
		JsonArray jsonConditions = jsonMapping.get(MAPPING_CONDITIONS_ATTRIBUTE).getAsJsonArray();
		List<MappingCondition> newConditions = getConditionsFromJson(jsonConditions, newMapping);

		// set mappingIds for new conditions
		for (MappingCondition newCondition : newConditions) {
			newCondition.setMappingId(getMappingId(request));
		}

        EventEntityMapping oldMapping = PersistenceAdapterImpl.getInstance().getEventEntityMapping(getMappingId(request));

        // update conditions
        List<MappingCondition> oldConditions = PersistenceAdapterImpl.getInstance().getMappingConditions(oldMapping.getId());
        if (!PersistenceAdapterImpl.getInstance().deleteArtifacts(oldConditions.toArray(new MappingCondition[oldConditions.size()]))) {
            halt(HttpStatusCodes.ERROR, "could not delete conditions");
        }

        if (!PersistenceAdapterImpl.getInstance().saveArtifacts(newConditions.toArray(new MappingCondition[newConditions.size()]))) {
            halt(HttpStatusCodes.ERROR, "could not create new conditions");
        }

        // update mapping
        oldMapping.setEventTypeId(newMapping.getEventTypeId());
        oldMapping.setEntityTypeId(newMapping.getEntityTypeId());
        oldMapping.setTargetStatus(newMapping.getTargetStatus());

        String mappingsUri =  EventTypeEndpoint.getEventTypeEntityMappingsUri(oldMapping.getEventTypeId());
        if (!PersistenceAdapterImpl.getInstance().updateArtifact(oldMapping, mappingsUri)) {
            halt(HttpStatusCodes.ERROR, "could not update mapping");
        }

        return "";
    }

    /**
     * This method returns the mapping given by json.
     * @param jsonMapping a json containing a mapping
     * @return the mapping described by json
     */
    private EventEntityMapping getEventMappingFromJson(JsonObject jsonMapping) {
        EventEntityMapping mapping = new EventEntityMappingImpl();
        mapping.setEventTypeId(jsonMapping.get(EVENT_TYPE_ID_ATTRIBUTE).getAsLong());
        mapping.setEntityTypeId(jsonMapping.get(ENTITY_TYPE_ID_ATTRIBUTE).getAsLong());
        mapping.setTargetStatus(jsonMapping.get(TARGET_STATUS_ATTRIBUTE).getAsString());

		checkValidMapping(mapping);

        return mapping;
    }

	/**
	 * This method checks whether a eventEntityMapping is valid.
	 * @param mapping - the eventEntityMapping to check
	 */
	private void checkValidMapping(EventEntityMapping mapping) {
		if (mapping.getEventTypeId() == StatusUpdatedEventType.getInstance().getId()) {
			halt(HttpStatusCodes.FORBIDDEN, "you may not create mappings for this event type");
		}

		if (PersistenceAdapterImpl.getInstance().getEntityType(mapping.getEntityTypeId()) == null) {
			halt(HttpStatusCodes.BAD_REQUEST, "the given entityType does not exist");
		}

		if (PersistenceAdapterImpl.getInstance().getEventType(mapping.getEventTypeId()) == null) {
			halt(HttpStatusCodes.BAD_REQUEST, "the given eventType does not exist");
		}
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
            if (!PersistenceAdapterImpl.getInstance().saveArtifacts(newMapping)) {
            	halt(HttpStatusCodes.ERROR, "could not save mapping into database");
			}

            JsonArray jsonConditions = jsonMapping.get(MAPPING_CONDITIONS_ATTRIBUTE).getAsJsonArray();
            List<MappingCondition> conditions = getConditionsFromJson(jsonConditions, newMapping);

            checkValidMappingConditions(newMapping, conditions);

            if (!PersistenceAdapterImpl.getInstance().saveArtifacts(conditions.toArray(new MappingCondition[conditions.size()]))) {
				PersistenceAdapterImpl.getInstance().deleteArtifacts(newMapping);
                halt(HttpStatusCodes.ERROR, "could not save mapping conditions into database");
            }

			String mappingsUri =  EventTypeEndpoint.getEventTypeEntityMappingsUri(newMapping.getEventTypeId());
			PersistenceAdapterImpl.getInstance().createArtifact(newMapping, mappingsUri);

        } catch (ClassCastException | IllegalStateException e) {
            LoggerUtilImpl.getInstance().error(logger, JSON_PARSE_ERROR_MESSAGE, e);

            if (newMapping.getId() != 0) {
            	PersistenceAdapterImpl.getInstance().deleteArtifacts(newMapping);
			}

            halt(HttpStatusCodes.BAD_REQUEST, e.getMessage());
        }

        return newMapping;
    }

    /**
     * This method returns conditions given as json in json array.
     * @param jsonConditions conditions as json array
     * @param mapping the owner of the mappingConditions
     * @return the conditions given in json array
     */
    private List<MappingCondition> getConditionsFromJson(JsonArray jsonConditions, EventEntityMapping mapping) {
        List<MappingCondition> conditions = new ArrayList<>();
        for (JsonElement jsonCondition : jsonConditions) {
            JsonObject jsonConditionObject = jsonCondition.getAsJsonObject();

            MappingCondition newCondition = new MappingConditionImpl();
            newCondition.setEventTypeAttributeId(jsonConditionObject.get(EVENT_TYPE_ATTRIBUTE_ID_ATTRIBUTE).getAsLong());
            newCondition.setEntityTypeAttributeId(jsonConditionObject.get(ENTITY_TYPE_ATTRIBUTE_ID_ATTRIBUTE).getAsLong());
            newCondition.setMappingId(mapping.getId());

            conditions.add(newCondition);
        }

		checkValidMappingConditions(mapping, conditions);

        return conditions;
    }

	/**
	 * This method checks whether a given list of mappingConditions is valid.
	 * @param mapping - the owner of the mapping conditions
	 * @param conditions - the list of mappingConditions to check
	 */
	private void checkValidMappingConditions(EventEntityMapping mapping, List<MappingCondition> conditions) {

		if (mapping.getEventTypeId() == StatusUpdatedEventType.getInstance().getId()) {
			halt(HttpStatusCodes.FORBIDDEN, "you may not create mappings for this event type");
		}

		List<Long> eventTypeAttributeIds = new ArrayList<>();
		List<Long> entityTypeAttributeIds = new ArrayList<>();

		for (TypeAttribute eventTypeAttribute : PersistenceAdapterImpl.getInstance().getTypeAttributes(mapping.getEventTypeId())) {
			eventTypeAttributeIds.add(eventTypeAttribute.getId());
		}

		for (TypeAttribute entityTypeAttribute : PersistenceAdapterImpl.getInstance().getTypeAttributes(mapping.getEntityTypeId())) {
			entityTypeAttributeIds.add(entityTypeAttribute.getId());
		}

		for (MappingCondition condition : conditions) {
			if (!eventTypeAttributeIds.contains(condition.getEventTypeAttributeId())
					|| !entityTypeAttributeIds.contains(condition.getEntityTypeAttributeId())) {
				halt(HttpStatusCodes.BAD_REQUEST, "one of the conditions contained one or more non-existing attributes");
			}
		}
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
