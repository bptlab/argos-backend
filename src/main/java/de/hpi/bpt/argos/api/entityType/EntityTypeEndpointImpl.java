package de.hpi.bpt.argos.api.entityType;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.hpi.bpt.argos.api.eventType.EventTypeEndpointImpl;
import de.hpi.bpt.argos.storage.PersistenceAdapterImpl;
import de.hpi.bpt.argos.storage.dataModel.attribute.type.TypeAttribute;
import de.hpi.bpt.argos.storage.dataModel.entity.type.EntityType;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.halt;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class EntityTypeEndpointImpl implements EntityTypeEndpoint {

    private static final Logger logger = LoggerFactory.getLogger(EventTypeEndpointImpl.class);
    private static final RestEndpointUtil endpointUtil = RestEndpointUtilImpl.getInstance();
    private static final Gson serializer = new Gson();

    private JsonObject jsonHierarchy;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setup(Service sparkService) {
        sparkService.get(EntityTypeEndpoint.getEntityTypeHierarchyBaseUri(), this::getEntityTypeHierarchy);
        sparkService.get(EntityTypeEndpoint.getEntityTypeAttributesBaseUri(), this::getEntityTypeAttributes);
        sparkService.get(EntityTypeEndpoint.getEntityTypeEntityMappingsBaseUri(), this::getEntityTypeEntityMappings);

        jsonHierarchy = getEntityTypeHierarchy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEntityTypeHierarchy(Request request, Response response) {
        endpointUtil.logReceivedRequest(logger, request);

        response.body(serializer.toJson(jsonHierarchy));

        endpointUtil.logSendingResponse(logger, request, response.status(), response.body());
        return response.body();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEntityTypeAttributes(Request request, Response response) {
        endpointUtil.logReceivedRequest(logger, request);

        List<TypeAttribute> typeAttributes = PersistenceAdapterImpl.getInstance().getTypeAttributes(getEntityTypeId(request));
        if (typeAttributes == null) {
            halt(HttpStatusCodes.ERROR, "could not read attributes.");
        }
        JsonArray jsonTypeAttributes = new JsonArray();
        for (TypeAttribute att : typeAttributes) {
            JsonObject jsonAttribute = new JsonObject();
            jsonAttribute.addProperty("Id", att.getId());
            jsonAttribute.addProperty("Name", att.getName());
            jsonTypeAttributes.add(jsonAttribute);
        }

        response.body(serializer.toJson(jsonTypeAttributes));
        endpointUtil.logSendingResponse(logger, request, response.status(), response.body());
        return response.body();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEntityTypeEntityMappings(Request request, Response response) {
        endpointUtil.logReceivedRequest(logger, request);

        long entityTypeId = getEntityTypeId(request);
        List<EventEntityMapping> mappings = PersistenceAdapterImpl.getInstance().getEventEntityMappingsForEntityType(entityTypeId);
        JsonArray jsonMappings = getMappingsJson(mappings);

        response.body(serializer.toJson(jsonMappings));
        endpointUtil.logSendingResponse(logger, request, response.status(), response.body());
        return response.body();
    }

    /**
     * This method returns the hierarchy of stored entity types as json.
     * @return json containing all entity types as hierarchy
     */
    private JsonObject getEntityTypeHierarchy() {
        List<EntityType> allEntityTypes = PersistenceAdapterImpl.getInstance().getEntityTypes();
        if (allEntityTypes == null) {
            halt(HttpStatusCodes.ERROR, "could not read entity types.");
        }
        Map<Long, List<EntityType>> tree = new HashMap<>();

        // Convert list to tree
        List<EntityType> roots = new ArrayList<>();
        for (EntityType entityType : allEntityTypes) {
            if (entityType.getParentId() < 0) {
                entityType.setParentId(-1);
                roots.add(entityType);
            } else {
                if (!tree.containsKey(entityType.getParentId())) {
                    tree.put(entityType.getParentId(), new ArrayList<>());
                }
                tree.get(entityType.getParentId()).add(entityType);
            }
        }

        return getHierarchyJson(tree, roots);
    }

    /**
     * This method returns a json containing all hierarchy objects of the given tree.
     * @param tree map containing all hierarchy layers
     * @param roots first level of tree
     * @return json containing all hierarchy objects
     */
    private JsonObject getHierarchyJson(Map<Long, List<EntityType>> tree, List<EntityType> roots) {
        JsonArray hierarchyJson = new JsonArray();

        JsonArray jsonVirtualRootLayer = new JsonArray();
        JsonObject virtualRoot = getVirtualRootJson();
        jsonVirtualRootLayer.add(virtualRoot);
        hierarchyJson.add(jsonVirtualRootLayer);

        Map<Integer, List<JsonObject>> layerMap = new HashMap<>();

        getChildJsons(roots, layerMap, 1, tree);
        for (int layerId = 0; layerId <= layerMap.size(); layerId++) {
            JsonArray jsonHierarchyLayer = new JsonArray();
            for (JsonObject jsonEntityType : layerMap.get(layerId)) {
                jsonHierarchyLayer.add(jsonEntityType);
            }
            hierarchyJson.add(jsonHierarchyLayer);
        }

        return hierarchyJson.getAsJsonObject();
    }

    /**
     * This method computes the jsons for all entity types given by entityTypesOfLayer and saves them into the given map.
     * @param entityTypesOfLayer entity types of the processed layer
     * @param layerMap the map the entity type jsons are saved in
     * @param layerId the hierarchy level of the entities to be processed
     * @param tree the tree containing all entity types of this hierarchy
     */
    private void getChildJsons(List<EntityType> entityTypesOfLayer,
                               Map<Integer, List<JsonObject>> layerMap, int layerId, Map<Long, List<EntityType>> tree) {
        if (entityTypesOfLayer.isEmpty()) {
            return;
        }

        List<JsonObject> jsonEntityTypesOfLayer = new ArrayList<>();
        for (EntityType entityType : entityTypesOfLayer) {
            JsonObject jsonEntityType = getEntityTypeJson(entityType);

            jsonEntityTypesOfLayer.add(jsonEntityType);
            getChildJsons(tree.get(entityType.getId()), layerMap, layerId + 1, tree);
        }

        if (layerMap.containsKey(layerId)) {
            List<JsonObject> otherTypesOfLayer = layerMap.get(layerId);
            layerMap.remove(layerId, otherTypesOfLayer);
            jsonEntityTypesOfLayer.addAll(otherTypesOfLayer);
            layerMap.put(layerId, jsonEntityTypesOfLayer);
        } else {
            layerMap.put(layerId, jsonEntityTypesOfLayer);
        }
    }

    /**
     * This method returns the given entity type as json object.
     * @param entityType the entity type to be processed
     * @return json representation of the given entity type
     */
    private JsonObject getEntityTypeJson(EntityType entityType) {
        JsonObject jsonEntityType = new JsonObject();
        jsonEntityType.addProperty("Id", entityType.getId());
        jsonEntityType.addProperty("ParentId", entityType.getParentId());
        jsonEntityType.addProperty("Name", entityType.getName());
        return jsonEntityType;
    }

    /**
     * This method returns a json containing a virtual root entity type
     * that is the parent of all entity types of highest level.
     * @return json representing a virtual root entity type
     */
    private JsonObject getVirtualRootJson() {
        JsonObject virtualRoot = new JsonObject();
        virtualRoot.addProperty("Id", -1);
        virtualRoot.addProperty("ParentId", 0);
        virtualRoot.addProperty("Name", "virtual root");
        return virtualRoot;
    }

    /**
     * This method returns the given mappings as json array.
     * @param mappings all mappings to be processed
     * @return json representation of the given mappings
     */
    private JsonArray getMappingsJson(List<EventEntityMapping> mappings) {
        JsonArray mappingsJson = new JsonArray();

        for (EventEntityMapping mapping : mappings) {
            JsonObject jsonMapping = new JsonObject();
            jsonMapping.addProperty("Id", mapping.getId());
            jsonMapping.addProperty("EventTypeId", mapping.getEventTypeId());
            jsonMapping.addProperty("EntityTypeId", mapping.getEntityTypeId());
            List<MappingCondition> conditions = PersistenceAdapterImpl.getInstance().getMappingConditions(mapping.getId());
            jsonMapping.add("EventEntityMappingConditions", getMappingConditionsJson(conditions));
        }

        return mappingsJson;
    }

    /**
     * This method returns the given conditions as json array.
     * @param conditions the conditions to be processed
     * @return json representation of the given mapping conditions
     */
    private JsonArray getMappingConditionsJson(List<MappingCondition> conditions) {
        JsonArray jsonConditions = new JsonArray();

        for (MappingCondition condition : conditions) {
            JsonObject jsonCondition = new JsonObject();
            jsonCondition.addProperty("EventTypeAttributeId", condition.getEventTypeAttributeId());
            jsonCondition.addProperty("EntityTypeAttributeId", condition.getEntityTypeAttributeId());
            jsonConditions.add(jsonCondition);
        }

        return jsonConditions;
    }

    /**
     * This method returns the id of the event type given in request.
     * @param request the request
     * @return entity id from request
     */
    private long getEntityTypeId(Request request) {
        return endpointUtil.validateLong(
                request.params(EntityTypeEndpoint.getEntityTypeIdParameter(false)),
                (Long input) -> input != 0);
    }
}
