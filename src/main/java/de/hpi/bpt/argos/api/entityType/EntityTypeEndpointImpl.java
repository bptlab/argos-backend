package de.hpi.bpt.argos.api.entityType;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.hpi.bpt.argos.api.RestEndpointCommon;
import de.hpi.bpt.argos.api.eventType.EventTypeEndpointImpl;
import de.hpi.bpt.argos.core.Argos;
import de.hpi.bpt.argos.storage.PersistenceAdapterImpl;
import de.hpi.bpt.argos.storage.dataModel.attribute.type.TypeAttribute;
import de.hpi.bpt.argos.storage.dataModel.entity.type.EntityType;
import de.hpi.bpt.argos.storage.dataModel.mapping.EventEntityMapping;
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

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class EntityTypeEndpointImpl implements EntityTypeEndpoint {

    private static final Logger logger = LoggerFactory.getLogger(EventTypeEndpointImpl.class);
    private static final RestEndpointUtil endpointUtil = RestEndpointUtilImpl.getInstance();
    private static final Gson serializer = new Gson();

    private String jsonHierarchy;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setup(Service sparkService) {
        sparkService.get(EntityTypeEndpoint.getEntityTypeHierarchyBaseUri(),
                (Request request, Response response) ->
                        endpointUtil.executeRequest(logger, request, response, this::getEntityTypeHierarchy));

        sparkService.get(EntityTypeEndpoint.getEntityTypeAttributesBaseUri(),
				(Request request, Response response) ->
						endpointUtil.executeRequest(logger, request, response, this::getEntityTypeAttributes));

        sparkService.get(EntityTypeEndpoint.getEntityTypeEntityMappingsBaseUri(),
				(Request request, Response response) ->
						endpointUtil.executeRequest(logger, request, response, this::getEntityTypeEntityMappings));

        jsonHierarchy = serializer.toJson(getEntityTypeHierarchy());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEntityTypeHierarchy(Request request, Response response) {
        if (Argos.isInTestMode()) {
            jsonHierarchy = serializer.toJson(getEntityTypeHierarchy());
        }
        return jsonHierarchy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEntityTypeAttributes(Request request, Response response) {
        List<TypeAttribute> typeAttributes = PersistenceAdapterImpl.getInstance().getTypeAttributes(getEntityTypeId(request));

        return serializer.toJson(RestEndpointCommon.getTypeAttributesJson(typeAttributes));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEntityTypeEntityMappings(Request request, Response response) {
        long entityTypeId = getEntityTypeId(request);
        List<EventEntityMapping> mappings = PersistenceAdapterImpl.getInstance().getEventEntityMappingsForEntityType(entityTypeId);

        return serializer.toJson(RestEndpointCommon.getEventEntityMappingsJson(mappings));
    }

    /**
     * This method returns the hierarchy of stored entity types as json.
     * @return json containing all entity types as hierarchy
     */
    private JsonArray getEntityTypeHierarchy() {
        List<EntityType> allEntityTypes = PersistenceAdapterImpl.getInstance().getEntityTypes();

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
    private JsonArray getHierarchyJson(Map<Long, List<EntityType>> tree, List<EntityType> roots) {
        JsonArray hierarchyJson = new JsonArray();

        Map<Integer, List<JsonObject>> layerMap = new HashMap<>();

        getChildJsons(roots, layerMap, 0, tree);
        for (List<JsonObject> layer : layerMap.values()) {
            JsonArray jsonHierarchyLayer = new JsonArray();
            for (JsonObject jsonEntityType : layer) {
                jsonHierarchyLayer.add(jsonEntityType);
            }
            hierarchyJson.add(jsonHierarchyLayer);
        }

        return hierarchyJson;
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
        if (entityTypesOfLayer == null || entityTypesOfLayer.isEmpty()) {
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
