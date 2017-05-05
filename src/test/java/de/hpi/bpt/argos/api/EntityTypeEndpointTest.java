package de.hpi.bpt.argos.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.hpi.bpt.argos.api.entityType.EntityTypeEndpoint;
import de.hpi.bpt.argos.common.RestRequest;
import de.hpi.bpt.argos.common.RestRequestFactoryImpl;
import de.hpi.bpt.argos.core.ArgosTestParent;
import de.hpi.bpt.argos.storage.dataModel.entity.type.EntityType;
import de.hpi.bpt.argos.testUtil.ArgosTestUtil;
import de.hpi.bpt.argos.util.HttpStatusCodes;
import org.junit.BeforeClass;
import org.junit.Test;

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
        assertEquals(4, hierarchyArray.size());

        JsonArray layerArray0 = hierarchyArray.get(0).getAsJsonArray();
        assertEquals(1, layerArray0.size());
        JsonObject jsonVirtualRoot = layerArray0.get(0).getAsJsonObject();
        assertEquals(-1, jsonVirtualRoot.get("Id").getAsInt());
        assertEquals(0, jsonVirtualRoot.get("ParentId").getAsInt());

        JsonArray layerArray1 = hierarchyArray.get(1).getAsJsonArray();
        assertEquals(2, layerArray1.size());
        assertEntityTypeIncluded(root1, layerArray1);
        assertEntityTypeIncluded(root2, layerArray1);

        JsonArray layerArray2 = hierarchyArray.get(2).getAsJsonArray();
        assertEquals(3, layerArray2.size());
        assertEntityTypeIncluded(childFirstLayer11, layerArray2);
        assertEntityTypeIncluded(childFirstLayer12, layerArray2);
        assertEntityTypeIncluded(childFirstLayer21, layerArray2);

        JsonArray layerArray3 = hierarchyArray.get(3).getAsJsonArray();
        assertEquals(1, layerArray3.size());
        assertEntityTypeIncluded(childSecondLayer111, layerArray3);
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
