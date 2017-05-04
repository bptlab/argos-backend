package de.hpi.bpt.argos.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.hpi.bpt.argos.api.eventType.EventTypeEndpoint;
import de.hpi.bpt.argos.common.RestRequest;
import de.hpi.bpt.argos.common.RestRequestFactoryImpl;
import de.hpi.bpt.argos.core.ArgosTestParent;
import de.hpi.bpt.argos.storage.PersistenceAdapterImpl;
import de.hpi.bpt.argos.storage.dataModel.attribute.type.TypeAttribute;
import de.hpi.bpt.argos.storage.dataModel.entity.type.EntityTypeImpl;
import de.hpi.bpt.argos.storage.dataModel.event.query.EventQuery;
import de.hpi.bpt.argos.storage.dataModel.event.type.EventType;
import de.hpi.bpt.argos.storage.dataModel.mapping.EventEntityMapping;
import de.hpi.bpt.argos.storage.dataModel.mapping.MappingCondition;
import de.hpi.bpt.argos.util.ArgosTestUtil;
import de.hpi.bpt.argos.util.HttpStatusCodes;
import javafx.util.Pair;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EventTypeEndpointTest extends ArgosTestParent {
    private static EventType testEventType;

    @BeforeClass
    public static void initialize() {
        testEventType = ArgosTestUtil.createEventType(false, true);
    }

    @Test
    public void testGetEventTypes() {
        RestRequest request = RestRequestFactoryImpl.getInstance()
                .createGetRequest(ARGOS_REST_HOST, EventTypeEndpoint.getEventTypesBaseUri());

        assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());

        JsonParser parser = new JsonParser();
        try {
            JsonObject responseJson =  parser.parse(request.getResponse()).getAsJsonObject();
            JsonArray eventTypesArray = responseJson.get("EventTypes").getAsJsonArray();
            assertEquals(1, eventTypesArray.size());

            JsonObject jsonEventType = eventTypesArray.get(0).getAsJsonObject();
            assertEquals(testEventType.getId(), jsonEventType.get("Id").getAsLong());
            assertEquals(testEventType.getName(), jsonEventType.get("Name").getAsString());
            assertEquals(testEventType.getTimeStampAttributeId(), jsonEventType.get("TimestampAttributeId").getAsLong());
            assertEquals(0, jsonEventType.get("NumberOfEvents").getAsInt());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetEventType() {
        RestRequest request = RestRequestFactoryImpl.getInstance()
                .createGetRequest(ARGOS_REST_HOST, EventTypeEndpoint.getEventTypeUri(testEventType.getId()));

        assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());

        JsonParser parser = new JsonParser();
        try {
            JsonObject responseJson =  parser.parse(request.getResponse()).getAsJsonObject();

            assertEquals(testEventType.getId(), responseJson.get("Id").getAsLong());
            assertEquals(testEventType.getName(), responseJson.get("Name").getAsString());
            assertEquals(testEventType.getTimeStampAttributeId(), responseJson.get("TimestampAttributeId").getAsLong());
            assertEquals(0, responseJson.get("NumberOfEvents").getAsInt());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetEventType_InvalidEventTypeId() {
        RestRequest request = RestRequestFactoryImpl.getInstance()
                .createGetRequest(ARGOS_REST_HOST, EventTypeEndpoint.getEventTypeUri(testEventType.getId() - 1));

        assertEquals(HttpStatusCodes.BAD_REQUEST, request.getResponseCode());
    }

    @Test
    public void testCreateEventType() {
        RestRequest request = RestRequestFactoryImpl.getInstance()
                .createPostRequest(ARGOS_REST_HOST, EventTypeEndpoint.getCreateEventTypeBaseUri());

        EventType newEventType = ArgosTestUtil.createEventType(true, false);
        JsonObject newJsonEventType = createEventTypeJson(newEventType, true);

        request.setContent(serializer.toJson(newJsonEventType));

        assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());

        boolean newEventTypeStored = false;
        for (EventType existingType : PersistenceAdapterImpl.getInstance().getEventTypes()) {
            if (existingType.getName().equals(newEventType.getName())) {
                newEventTypeStored = true;
                break;
            }
        }
        assertTrue(newEventTypeStored);
    }

    @Test
    public void testCreateEventType_EmptyJson() {
        RestRequest request = RestRequestFactoryImpl.getInstance()
                .createPostRequest(ARGOS_REST_HOST, EventTypeEndpoint.getCreateEventTypeBaseUri());

        JsonObject emptyJson = new JsonObject();
        request.setContent(serializer.toJson(emptyJson));

        assertEquals(HttpStatusCodes.BAD_REQUEST, request.getResponseCode());
    }

    @Test
    public void testCreateEventType_InvalidJson() {
        RestRequest request = RestRequestFactoryImpl.getInstance()
                .createPostRequest(ARGOS_REST_HOST, EventTypeEndpoint.getCreateEventTypeBaseUri());

        EventType newEventType = ArgosTestUtil.createEventType(true, false);
        JsonObject newJsonEventType = createEventTypeJson(newEventType, false);

        request.setContent(serializer.toJson(newJsonEventType));

        assertEquals(HttpStatusCodes.BAD_REQUEST, request.getResponseCode());
        // TODO not stored to unicorn
        assertNotStored(newEventType);
    }

    @Test
    public void testDeleteEventType() {
        EventType eventTypeToDelete = ArgosTestUtil.createEventType(true, true);
        EventQuery query = ArgosTestUtil.createEventQuery(eventTypeToDelete, true);
        List<TypeAttribute> attributes = ArgosTestUtil.createEventTypeAttributes(eventTypeToDelete, true);
        EventEntityMapping mapping = ArgosTestUtil.createEventEntityMapping(eventTypeToDelete, new EntityTypeImpl(), "", true);
        List<MappingCondition> conditions = ArgosTestUtil.createMappingConditions(mapping, true, new Pair<>((long) 1, (long) 2));



        RestRequest request = RestRequestFactoryImpl.getInstance()
                .createDeleteRequest(ARGOS_REST_HOST, EventTypeEndpoint.getDeleteEventTypeUri(eventTypeToDelete.getId()));

        assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());

        boolean eventTypeDeleted = true;
        for (EventType existingType : PersistenceAdapterImpl.getInstance().getEventTypes()) {
            if (existingType.getId() == (eventTypeToDelete.getId())) {
                eventTypeDeleted = false;
                break;
            }
        }
        assertTrue(eventTypeDeleted);

        boolean eventQueryDeleted = true;
        for (EventQuery existingQuery : PersistenceAdapterImpl.getInstance().getEventQueries(eventTypeToDelete.getId())) {
            if (existingQuery.getId() == (query.getId())) {
                eventQueryDeleted = false;
                break;
            }
        }
        assertTrue(eventQueryDeleted);

        boolean attributesDeleted = true;
        for (TypeAttribute existingAttribute : PersistenceAdapterImpl.getInstance().getTypeAttributes(eventTypeToDelete.getId())) {
            for (TypeAttribute attribute : attributes) {
                if (existingAttribute.getId() == (attribute.getId())) {
                    attributesDeleted = false;
                    break;
                }
            }
        }
        assertTrue(attributesDeleted);

        boolean mappingsDeleted = true;
        for (EventEntityMapping existingMapping : PersistenceAdapterImpl.getInstance().getEventEntityMappingsForEventType(eventTypeToDelete.getId())) {
            if (existingMapping.getId() == (mapping.getId())) {
                mappingsDeleted = false;
                break;
            }
        }
        assertTrue(mappingsDeleted);

        boolean conditionsDeleted = true;
        for (MappingCondition existingCondition : PersistenceAdapterImpl.getInstance().getMappingConditions(mapping.getId())) {
            for (MappingCondition condition : conditions) {
                if (existingCondition.getId() == (condition.getId())) {
                    conditionsDeleted = false;
                    break;
                }
            }
        }
        assertTrue(conditionsDeleted);
    }

    @Test
    public void testDeleteEventType_InvalidId() {
        RestRequest request = RestRequestFactoryImpl.getInstance()
                .createDeleteRequest(ARGOS_REST_HOST, EventTypeEndpoint.getDeleteEventTypeUri(testEventType.getId() - 1));

        assertEquals(HttpStatusCodes.BAD_REQUEST, request.getResponseCode());

        boolean eventTypeDeleted = true;
        for (EventType existingType : PersistenceAdapterImpl.getInstance().getEventTypes()) {
            if (existingType.getName().equals(testEventType.getName())) {
                eventTypeDeleted = false;
                break;
            }
        }
        assertFalse(eventTypeDeleted);
    }

    @Test
    public void testDeleteEventType_NotDeletable() {
        RestRequest request = RestRequestFactoryImpl.getInstance()
                .createDeleteRequest(ARGOS_REST_HOST, EventTypeEndpoint.getDeleteEventTypeUri(testEventType.getId()));

        assertEquals(HttpStatusCodes.FORBIDDEN, request.getResponseCode());

        boolean eventTypeDeleted = true;
        for (EventType existingType : PersistenceAdapterImpl.getInstance().getEventTypes()) {
            if (existingType.getName().equals(testEventType.getName())) {
                eventTypeDeleted = false;
                break;
            }
        }
        assertFalse(eventTypeDeleted);
    }

    @Test
    public void testDeleteEventType_BlockedEventType() {
        EventType blockedEventType = ArgosTestUtil.createEventType(true, true);
        EventType blockingEventType = ArgosTestUtil.createEventType(true, true);

        ArgosTestUtil.createBlockingEventQuery(blockingEventType, true, blockedEventType);

        RestRequest request = RestRequestFactoryImpl.getInstance()
                .createDeleteRequest(ARGOS_REST_HOST, EventTypeEndpoint.getDeleteEventTypeUri(blockedEventType.getId()));

        assertEquals(HttpStatusCodes.ERROR, request.getResponseCode());

        JsonParser parser = new JsonParser();
        JsonArray jsonBlockingEventType = parser.parse(request.getResponse()).getAsJsonArray();
        assertEquals(1, jsonBlockingEventType.size());

        assertEquals(blockingEventType.getId(), jsonBlockingEventType.get(0).getAsLong());
    }


    @Test
    public void testGetEventTypesAttributes() {
        List<TypeAttribute> attributes = ArgosTestUtil.createEventTypeAttributes(testEventType, true);

        RestRequest request = RestRequestFactoryImpl.getInstance()
                .createGetRequest(ARGOS_REST_HOST, EventTypeEndpoint.getEventTypeAttributesUri(testEventType.getId()));

        assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());

        JsonParser parser = new JsonParser();
        try {
            JsonObject responseJson =  parser.parse(request.getResponse()).getAsJsonObject();

            JsonArray attributeArray = responseJson.get("TypeAttributes").getAsJsonArray();
            List<JsonElement> foundAttributeJsons = new ArrayList<>();
            for (JsonElement attribute : attributeArray) {
                boolean attributeFound = false;
                for (TypeAttribute att : attributes) {
                    JsonObject attributeObject = attribute.getAsJsonObject();
                    if (attributeObject.get("Id").getAsLong() == att.getId() &&
                            attributeObject.get("Name").getAsString().equals(att.getName())) {
                        attributeFound = true;
                        break;
                    }
                }
                if (!attributeFound) {
                    fail("wrong attribute in json included");
                }
                foundAttributeJsons.add(attribute);
            }
            if (foundAttributeJsons.size() != attributeArray.size()) {
                fail("not all json attributes exist");
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetEventTypesAttributes_InvalidId() {
        RestRequest request = RestRequestFactoryImpl.getInstance()
                .createGetRequest(ARGOS_REST_HOST, EventTypeEndpoint.getEventTypeAttributesUri(testEventType.getId() - 1));

        assertEquals(HttpStatusCodes.BAD_REQUEST, request.getResponseCode());
    }

    @Test
    public void testGetEventTypeQueries() {
        List<EventQuery> queries = ArgosTestUtil.createEventQueries(testEventType, true);

        RestRequest request = RestRequestFactoryImpl.getInstance()
                .createGetRequest(ARGOS_REST_HOST, EventTypeEndpoint.getEventTypeQueriesUri(testEventType.getId()));

        assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());

        JsonParser parser = new JsonParser();
        try {
            JsonObject responseJson =  parser.parse(request.getResponse()).getAsJsonObject();

            JsonArray queriesArray = responseJson.get("EventQueries").getAsJsonArray();
            List<JsonElement> foundQueryJsons = new ArrayList<>();
            for (JsonElement jsonQuery : queriesArray) {
                boolean queryFound = false;
                for (EventQuery query : queries) {
                    JsonObject queryObject = jsonQuery.getAsJsonObject();
                    if (queryObject.get("Id").getAsLong() == query.getId() &&
                            queryObject.get("Description").getAsString().equals(query.getDescription()) &&
                            queryObject.get("Query").getAsString().equals(query.getQuery())) {
                        queryFound = true;
                        break;
                    }
                }
                if (!queryFound) {
                    fail("wrong attribute in json included");
                }
                foundQueryJsons.add(jsonQuery);
            }
            if (foundQueryJsons.size() != queriesArray.size()) {
                fail("not all json attributes exist");
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetEventTypeQueries_InvalidId() {
        RestRequest request = RestRequestFactoryImpl.getInstance()
                .createGetRequest(ARGOS_REST_HOST, EventTypeEndpoint.getEventTypeQueriesUri(testEventType.getId() - 1));

        assertEquals(HttpStatusCodes.BAD_REQUEST, request.getResponseCode());
    }

    @Test
    public void testGetEventTypeEntityMappings() {
        EventEntityMapping mapping = ArgosTestUtil.createEventEntityMapping(testEventType, ArgosTestUtil.createEntityType(true), "", true);
        List<MappingCondition> conditions = ArgosTestUtil.createMappingConditions(mapping, true, new Pair<>((long) 1, (long) 2));

        RestRequest request = RestRequestFactoryImpl.getInstance()
                .createGetRequest(ARGOS_REST_HOST, EventTypeEndpoint.getEventTypeEntityMappingsUri(testEventType.getId()));

        assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());

        JsonParser parser = new JsonParser();
        try {
            JsonObject responseJson =  parser.parse(request.getResponse()).getAsJsonObject();

            JsonArray mappingsArray = responseJson.get("EventEntityMappings").getAsJsonArray();
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
                if (!conditionFound) {
                    fail("wrong attribute in json included");
                }
                foundMappingJsons.add(jsonMapping);
            }
            if (foundMappingJsons.size() != mappingsArray.size()) {
                fail("not all json attributes exist");
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetEventTypeEntityMappings_InvalidId() {
        RestRequest request = RestRequestFactoryImpl.getInstance()
                .createGetRequest(ARGOS_REST_HOST, EventTypeEndpoint.getEventTypeEntityMappingsUri(testEventType.getId() - 1));

        assertEquals(HttpStatusCodes.BAD_REQUEST, request.getResponseCode());
    }

    private JsonObject createEventTypeJson(EventType newEventType, boolean valid) {
        JsonObject newJsonEventType = new JsonObject();

        JsonObject newProperties = new JsonObject();

        if(valid) {
            newProperties.addProperty("Name", newEventType.getName());
        }
        newProperties.addProperty("TimestampAttributeName", newEventType.getTimeStampAttributeId());

        JsonArray newJsonAttributes = new JsonArray();
        List<TypeAttribute> attributes = ArgosTestUtil.createEventTypeAttributes(newEventType, false);
        for (TypeAttribute att : attributes) {
            JsonObject newJsonAttribute = new JsonObject();
            newJsonAttribute.addProperty("Name", att.getName());
            newJsonAttributes.add(newJsonAttribute);
        }

        newProperties.add("TypeAttributes", newJsonAttributes);
        newJsonEventType.add("EventType", newProperties);

        return newJsonEventType;
    }

    private void assertNotStored(EventType newEventType) {
        boolean newEventTypeStored = false;
        for (EventType existingType : PersistenceAdapterImpl.getInstance().getEventTypes()) {
            if (existingType.getName().equals(newEventType.getName())) {
                newEventTypeStored = true;
                break;
            }
        }
        assertFalse(newEventTypeStored);
    }
}
