package de.hpi.bpt.argos.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.hpi.bpt.argos.api.entityType.EntityTypeEndpoint;
import de.hpi.bpt.argos.common.RestRequest;
import de.hpi.bpt.argos.common.RestRequestFactoryImpl;
import de.hpi.bpt.argos.core.ArgosTestParent;
import de.hpi.bpt.argos.storage.dataModel.attribute.type.TypeAttribute;
import de.hpi.bpt.argos.storage.dataModel.entity.type.EntityType;
import de.hpi.bpt.argos.storage.dataModel.mapping.EventEntityMapping;
import de.hpi.bpt.argos.storage.dataModel.mapping.MappingCondition;
import de.hpi.bpt.argos.testUtil.ArgosTestUtil;
import de.hpi.bpt.argos.util.HttpStatusCodes;
import de.hpi.bpt.argos.util.PairImpl;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

public class EntityTypeEndpointTest extends ArgosTestParent {
    static EntityType root1;
    static EntityType root2;
    static EntityType childFirstLayer11;
    static EntityType childFirstLayer12;
    static EntityType childFirstLayer21;
    static EntityType childSecondLayer111;

    @BeforeClass
    public static void initialize() {
        root1 = ArgosTestUtil.createEntityType(true);
        root2 = ArgosTestUtil.createEntityType(true);
        childFirstLayer11 = ArgosTestUtil.createEntityType(root1, true);
        childFirstLayer12 = ArgosTestUtil.createEntityType(root1, true);
        childFirstLayer21 = ArgosTestUtil.createEntityType(root2, true);
        childSecondLayer111 = ArgosTestUtil.createEntityType(childFirstLayer11, true);
    }

    @Test
    public void testGetEntityTypeHierarchy() {
        RestRequest request = RestRequestFactoryImpl.getInstance()
                .createGetRequest(ARGOS_REST_HOST, EntityTypeEndpoint.getEntityTypeHierarchyBaseUri());

        assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());

        JsonArray hierarchyArray = jsonParser.parse(request.getResponse()).getAsJsonArray();
        assertEquals(3, hierarchyArray.size());

        JsonArray layerArray0 = hierarchyArray.get(0).getAsJsonArray();
        assertEquals(2, layerArray0.size());
        assertEntityTypeIncluded(root1, layerArray0);
        assertEntityTypeIncluded(root2, layerArray0);

        JsonArray layerArray1 = hierarchyArray.get(1).getAsJsonArray();
        assertEquals(3, layerArray1.size());
        assertEntityTypeIncluded(childFirstLayer11, layerArray1);
        assertEntityTypeIncluded(childFirstLayer12, layerArray1);
        assertEntityTypeIncluded(childFirstLayer21, layerArray1);

        JsonArray layerArray2 = hierarchyArray.get(2).getAsJsonArray();
        assertEquals(1, layerArray2.size());
        assertEntityTypeIncluded(childSecondLayer111, layerArray2);
    }

    @Test
    public void testGetAttributes() {
        List<TypeAttribute> attributes = ArgosTestUtil.createEntityTypeAttributes(root1, true);
        RestRequest request = RestRequestFactoryImpl.getInstance()
                .createGetRequest(ARGOS_REST_HOST, EntityTypeEndpoint.getEntityTypeAttributesUri(root1.getId()));

        assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());

        JsonArray attributesArray = jsonParser.parse(request.getResponse()).getAsJsonArray();
        assertEquals(attributes.size(), attributesArray.size());

        assertAllAttributesIncluded(attributes, attributesArray);
    }

    @Test
    public void testGetAttributes_InvalidId_BadRequest() {
        RestRequest request = RestRequestFactoryImpl.getInstance()
                .createGetRequest(ARGOS_REST_HOST, EntityTypeEndpoint.getEntityTypeAttributesUri(root1.getId() - 1));

        assertEquals(HttpStatusCodes.BAD_REQUEST, request.getResponseCode());
    }

    @Test
    public void testGetMappings() {
        EventEntityMapping mapping = ArgosTestUtil.createEventEntityMapping(ArgosTestUtil.createEventType(true, true), root1, "", true);
        List<MappingCondition> conditions = ArgosTestUtil.createMappingConditions(mapping, true, new PairImpl<>(1L, 2L));

        RestRequest request = RestRequestFactoryImpl.getInstance()
                .createGetRequest(ARGOS_REST_HOST, EntityTypeEndpoint.getEntityTypeEntityMappingsUri(root1.getId()));

        assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());

        JsonArray mappingsArray =  jsonParser.parse(request.getResponse()).getAsJsonArray();
        assertEquals(1, mappingsArray.size());

        List<JsonElement> foundMappingJsons = new ArrayList<>();
        JsonObject jsonMapping = mappingsArray.get(0).getAsJsonObject();
        assertEquals(jsonMapping.get("Id").getAsLong(), mapping.getId());
        assertEquals(jsonMapping.get("EntityTypeId").getAsLong(), mapping.getEntityTypeId());
        assertEquals(jsonMapping.get("EventTypeId").getAsLong(), mapping.getEventTypeId());
        assertEquals(jsonMapping.get("TargetStatus").getAsString(), mapping.getTargetStatus());
        JsonArray conditionsArray = jsonMapping.get("EventEntityMappingConditions").getAsJsonArray();
        for (JsonElement jsonCondition : conditionsArray) {
            boolean conditionFound = false;
            for (MappingCondition condition : conditions) {
                JsonObject jsonConditionObject = jsonCondition.getAsJsonObject();
                if (jsonConditionObject.get("EventTypeAttributeId").getAsLong() == condition.getEventTypeAttributeId() &&
                        jsonConditionObject.get("EntityTypeAttributeId").getAsLong() == condition.getEntityTypeAttributeId()) {
                    conditionFound = true;
                    break;
                }
            }
            assertTrue(conditionFound);
            foundMappingJsons.add(jsonMapping);
        }
        assertTrue(foundMappingJsons.size() == mappingsArray.size());
    }

    @Test
    public void testGetMappings_InvalidId_BadRequest() {
        RestRequest request = RestRequestFactoryImpl.getInstance()
                .createGetRequest(ARGOS_REST_HOST, EntityTypeEndpoint.getEntityTypeEntityMappingsUri(root1.getId() - 1));

        assertEquals(HttpStatusCodes.BAD_REQUEST, request.getResponseCode());
    }

    private void assertAllAttributesIncluded(List<TypeAttribute> attributes, JsonArray attributesArray) {
        List<JsonElement> foundAttributeJsons = new ArrayList<>();
        for (JsonElement jsonAttribute : attributesArray) {
            boolean attributeFound = false;
            for (TypeAttribute attribute : attributes) {
                JsonObject jsonAttributeObject = jsonAttribute.getAsJsonObject();
                if (jsonAttributeObject.get("Id").getAsLong() == attribute.getId() &&
                        jsonAttributeObject.get("Name").getAsString().equals(attribute.getName())) {
                    attributeFound = true;
                    break;
                }
            }
            assertTrue(attributeFound);
            foundAttributeJsons.add(jsonAttribute);
        }
        assertTrue(foundAttributeJsons.size() == attributesArray.size());
    }

    private void assertEntityTypeIncluded(EntityType entityType, JsonArray jsonEntityTypeArray) {
        boolean entityTypeIncluded = false;
        JsonObject jsonEntityType = new JsonObject();
        for (JsonElement jsonElement : jsonEntityTypeArray) {
            jsonEntityType = jsonElement.getAsJsonObject();
            if (entityType.getId() == jsonEntityType.get("Id").getAsLong()) {
                entityTypeIncluded = true;
                break;
            }
        }
        assertTrue(entityTypeIncluded);
        assertEquals(entityType.getParentId(), jsonEntityType.get("ParentId").getAsLong());
        assertEquals(entityType.getName(), jsonEntityType.get("Name").getAsString());
    }
}
