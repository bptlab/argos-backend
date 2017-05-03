package de.hpi.bpt.argos.api.entityType;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.hpi.bpt.argos.api.eventType.EventTypeEndpointImpl;
import de.hpi.bpt.argos.storage.PersistenceAdapterImpl;
import de.hpi.bpt.argos.storage.dataModel.attribute.type.TypeAttribute;
import de.hpi.bpt.argos.storage.dataModel.entity.type.EntityType;
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

    @Override
    public void setup(Service sparkService) {
        sparkService.get(EntityTypeEndpoint.getEntityTypeHierarchyBaseUri(), this::getEntityTypeHierarchy);
        sparkService.get(EntityTypeEndpoint.getEntityTypeAttributesBaseUri(), this::getEntityTypeAttributes);
        sparkService.get(EntityTypeEndpoint.getEntityTypeEntityMappingsBaseUri(), this::getEntityTypeEntityMappings);

    }

    @Override
    public String getEntityTypeHierarchy(Request request, Response response) {
        endpointUtil.logReceivedRequest(logger, request);

        List<EntityType> allEntityTypes = PersistenceAdapterImpl.getInstance().getEntityTypes();
        if (allEntityTypes == null) {
            halt(HttpStatusCodes.ERROR, "could not read entity types.");
        }
        EntityType root = null;
        Map<Long, List<EntityType>> tree = new HashMap<>();

        // Convert list to tree
        for (EntityType entityType : allEntityTypes) {
            if (entityType.getParentId() == -1) {
                root = entityType;
            } else {
                if (!tree.containsKey(entityType.getParentId()))
                    tree.put(entityType.getParentId(), new ArrayList<>());
                tree.get(entityType.getParentId()).add(entityType);
            }
        }

        JsonObject jsonHierarchy = createHierarchyJson(tree, root);

        response.body(serializer.toJson(jsonHierarchy));
        endpointUtil.logSendingResponse(logger, request, response.status(), response.body());
        return response.body();
    }

    @Override
    public String getEntityTypeAttributes(Request request, Response response) {
        endpointUtil.logReceivedRequest(logger, request);

        List<TypeAttribute> attributes = PersistenceAdapterImpl.getInstance().getTypeAttributes(getEntityTypeId(request));
        if (attributes == null) {
            halt(HttpStatusCodes.ERROR, "could not read attributes.");
        }
        JsonArray typeAttributes = new JsonArray();
        for (TypeAttribute att : attributes) {
            JsonObject jsonAttribute = new JsonObject();
            jsonAttribute.addProperty("Id", att.getId());
            jsonAttribute.addProperty("Name", att.getName());
            typeAttributes.add(jsonAttribute);
        }

        response.body(serializer.toJson(typeAttributes));
        endpointUtil.logSendingResponse(logger, request, response.status(), response.body());
        return response.body();
    }

    @Override
    public String getEntityTypeEntityMappings(Request request, Response response) {
        return null;
    }

    private JsonObject createHierarchyJson(Map<Long, List<EntityType>> tree, EntityType root) {
        JsonArray hierarchyJson = new JsonArray();

        JsonObject jsonRoot = createEntityTypeJson(root);
        List<JsonObject> rootList = new ArrayList<>();
        rootList.add(jsonRoot);

        Map<Integer, List<JsonObject>> layerMap = new HashMap<>();
        layerMap.put(0, rootList);

        createChildJsons(tree.get(root.getId()), layerMap, 1, tree);
        for (int layerId = 0; layerId <= layerMap.size(); layerId++) {
            JsonArray jsonLayer = createHierarchyLayerJson(layerMap.get(layerId));
            hierarchyJson.add(jsonLayer);
        }

        return hierarchyJson.getAsJsonObject();
    }

    private JsonArray createHierarchyLayerJson(List<JsonObject> entityTypesOfLayer) {
        JsonArray hierarchyLayer = new JsonArray();
        for (JsonObject jsonEntityType : entityTypesOfLayer) {
            hierarchyLayer.add(jsonEntityType);
        }
        return hierarchyLayer;
    }

    private void createChildJsons(List<EntityType> entityTypesOfLayer, Map<Integer, List<JsonObject>> map, int layerId, Map<Long, List<EntityType>> tree) {
        if (entityTypesOfLayer.isEmpty()) {
            return;
        }

        List<JsonObject> jsonEntityTypesOfLayer = new ArrayList<>();
        for (EntityType entityType : entityTypesOfLayer) {
            JsonObject jsonEntityType = createEntityTypeJson(entityType);

            jsonEntityTypesOfLayer.add(jsonEntityType);
            createChildJsons(tree.get(entityType.getId()), map, layerId + 1, tree);
        }

        if (map.containsKey(layerId)) {
            List<JsonObject> otherTypesOfLayer = map.get(layerId);
            map.remove(layerId, otherTypesOfLayer);
            jsonEntityTypesOfLayer.addAll(otherTypesOfLayer);
            map.put(layerId, jsonEntityTypesOfLayer);
        } else {
            map.put(layerId, jsonEntityTypesOfLayer);
        }
    }

    private JsonObject createEntityTypeJson(EntityType entityType) {
        JsonObject jsonEntityType = new JsonObject();
        jsonEntityType.addProperty("Id", entityType.getId());
        jsonEntityType.addProperty("ParentId", entityType.getParentId());
        jsonEntityType.addProperty("Name", entityType.getName());
        return jsonEntityType;
    }

    private long getEntityTypeId(Request request) {
        return endpointUtil.validateLong(
                request.params(EntityTypeEndpoint.getEntityTypeIdParameter(false)),
                (Long input) -> input != 0);
    }
}
