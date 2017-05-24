package de.hpi.bpt.argos.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.hpi.bpt.argos.api.eventType.EventTypeEndpoint;
import de.hpi.bpt.argos.common.RestRequest;
import de.hpi.bpt.argos.common.RestRequestFactoryImpl;
import de.hpi.bpt.argos.core.ArgosTestParent;
import de.hpi.bpt.argos.storage.PersistenceAdapterImpl;
import de.hpi.bpt.argos.storage.PersistenceArtifactUpdateType;
import de.hpi.bpt.argos.storage.dataModel.PersistenceArtifact;
import de.hpi.bpt.argos.storage.dataModel.attribute.type.TypeAttribute;
import de.hpi.bpt.argos.storage.dataModel.entity.type.EntityTypeImpl;
import de.hpi.bpt.argos.storage.dataModel.event.query.EventQuery;
import de.hpi.bpt.argos.storage.dataModel.event.type.EventType;
import de.hpi.bpt.argos.storage.dataModel.event.type.StatusUpdatedEventType;
import de.hpi.bpt.argos.storage.dataModel.mapping.EventEntityMapping;
import de.hpi.bpt.argos.storage.dataModel.mapping.MappingCondition;
import de.hpi.bpt.argos.testUtil.ArgosTestUtil;
import de.hpi.bpt.argos.testUtil.WebSocket;
import de.hpi.bpt.argos.util.HttpStatusCodes;
import de.hpi.bpt.argos.util.PairImpl;
import org.junit.AfterClass;
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
        ArgosTestParent.setup();
        testEventType = ArgosTestUtil.createEventType(false, true);
    }

	@AfterClass
	public static void tearDown() {
		ArgosTestParent.tearDown();
	}

    @Test
    public void testGetEventTypes() {
    	int eventTypesCount = PersistenceAdapterImpl.getInstance().getEventTypes().size();

        RestRequest request = RestRequestFactoryImpl.getInstance()
                .createGetRequest(ARGOS_REST_HOST, EventTypeEndpoint.getEventTypesBaseUri());

        assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());

        JsonArray eventTypesArray = jsonParser.parse(request.getResponse()).getAsJsonArray();
        assertEquals(eventTypesCount, eventTypesArray.size());

        for (JsonElement element : eventTypesArray) {
        	JsonObject jsonEventType = element.getAsJsonObject();

        	if (jsonEventType.get("Name").getAsString().equals(testEventType.getName())) {
        		assertEventType(testEventType, jsonEventType);
			} else if (jsonEventType.get("Name").getAsString().equals(StatusUpdatedEventType.NAME)) {
        		assertEventType(StatusUpdatedEventType.getInstance(), jsonEventType);
			} else {
        		fail();
			}
		}
    }

    @Test
    public void testGetEventType() {
        RestRequest request = RestRequestFactoryImpl.getInstance()
                .createGetRequest(ARGOS_REST_HOST, EventTypeEndpoint.getEventTypeUri(testEventType.getId()));

        assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());

        JsonObject responseJson =  jsonParser.parse(request.getResponse()).getAsJsonObject();

        assertEventType(testEventType, responseJson);
    }

    @Test
    public void testGetEventType_InvalidEventTypeId_NotFound() {
        RestRequest request = RestRequestFactoryImpl.getInstance()
                .createGetRequest(ARGOS_REST_HOST, EventTypeEndpoint.getEventTypeUri(testEventType.getId() - 1));

        assertEquals(HttpStatusCodes.NOT_FOUND, request.getResponseCode());
    }

    @Test
    public void testCreateEventType() throws Exception {
        WebSocket webSocket = WebSocket.buildWebSocket();

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

        List<String> webSocketMessages = webSocket.awaitMessages(1, 1000);
        ArgosTestUtil.assertWebSocketMessage(webSocketMessages.get(0), PersistenceArtifactUpdateType.CREATE, "EventType");
    }

    @Test
    public void testCreateEventType_EmptyJson_Error() {
        RestRequest request = RestRequestFactoryImpl.getInstance()
                .createPostRequest(ARGOS_REST_HOST, EventTypeEndpoint.getCreateEventTypeBaseUri());

        JsonObject emptyJson = new JsonObject();
        request.setContent(serializer.toJson(emptyJson));

        assertEquals(HttpStatusCodes.ERROR, request.getResponseCode());
    }

    @Test
    public void testCreateEventType_InvalidJson_Error() {
        RestRequest request = RestRequestFactoryImpl.getInstance()
                .createPostRequest(ARGOS_REST_HOST, EventTypeEndpoint.getCreateEventTypeBaseUri());

        EventType newEventType = ArgosTestUtil.createEventType(true, false);
        JsonObject newJsonEventType = createEventTypeJson(newEventType, false);

        request.setContent(serializer.toJson(newJsonEventType));

        assertEquals(HttpStatusCodes.ERROR, request.getResponseCode());
        // TODO not stored to unicorn
        assertNotStored(newEventType);
    }

    @Test
    public void testDeleteEventType() throws Exception {
        EventType eventTypeToDelete = ArgosTestUtil.createEventType(true, true);
        EventQuery query = ArgosTestUtil.createEventQuery(eventTypeToDelete, true);
        List<TypeAttribute> attributes = ArgosTestUtil.createEventTypeAttributes(eventTypeToDelete, true);
        EventEntityMapping mapping = ArgosTestUtil.createEventEntityMapping(eventTypeToDelete, new EntityTypeImpl(), "", true);
        List<MappingCondition> conditions = ArgosTestUtil.createMappingConditions(mapping, true, new PairImpl<>( 1L, 2L));

        WebSocket webSocket = WebSocket.buildWebSocket();

        RestRequest request = RestRequestFactoryImpl.getInstance()
                .createDeleteRequest(ARGOS_REST_HOST, EventTypeEndpoint.getDeleteEventTypeUri(eventTypeToDelete.getId()));

        assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());

        assertTrue(checkIfDeleted(eventTypeToDelete, new ArrayList<>(PersistenceAdapterImpl.getInstance().getEventTypes())));
        assertTrue(checkIfDeleted(query, new ArrayList<>(PersistenceAdapterImpl.getInstance().getEventQueries(eventTypeToDelete.getId()))));
        for (TypeAttribute existingAttribute : PersistenceAdapterImpl.getInstance().getTypeAttributes(eventTypeToDelete.getId())) {
            assertTrue(checkIfDeleted(existingAttribute, new ArrayList<>(attributes)));
        }
        assertTrue(checkIfDeleted(mapping, new ArrayList<>(PersistenceAdapterImpl.getInstance().getEventEntityMappingsForEventType(eventTypeToDelete.getId()))));
        for (MappingCondition existingCondition : PersistenceAdapterImpl.getInstance().getMappingConditions(mapping.getId())) {
            assertTrue(checkIfDeleted(existingCondition, new ArrayList<>(conditions)));
        }

        List<String> webSocketMessages = webSocket.awaitMessages(1, 1000);
        ArgosTestUtil.assertWebSocketMessage(webSocketMessages.get(0), PersistenceArtifactUpdateType.DELETE, "EventType");
    }

    @Test
    public void testDeleteEventType_InvalidId_BadRequest() {
        RestRequest request = RestRequestFactoryImpl.getInstance()
                .createDeleteRequest(ARGOS_REST_HOST, EventTypeEndpoint.getDeleteEventTypeUri(testEventType.getId() - 1));

        assertEquals(HttpStatusCodes.BAD_REQUEST, request.getResponseCode());

        assertFalse(checkIfDeleted(testEventType, new ArrayList<>(PersistenceAdapterImpl.getInstance().getEventTypes())));
    }

    @Test
    public void testDeleteEventType_NotDeletable_Forbidden() {
        RestRequest request = RestRequestFactoryImpl.getInstance()
                .createDeleteRequest(ARGOS_REST_HOST, EventTypeEndpoint.getDeleteEventTypeUri(testEventType.getId()));

        assertEquals(HttpStatusCodes.FORBIDDEN, request.getResponseCode());

        assertFalse(checkIfDeleted(testEventType, new ArrayList<>(PersistenceAdapterImpl.getInstance().getEventTypes())));
    }

    @Test
    public void testDeleteEventType_BlockedEventType_Error() {
        EventType blockedEventType = ArgosTestUtil.createEventType(true, true);
        EventType blockingEventType = ArgosTestUtil.createEventType(true, true);

        ArgosTestUtil.createBlockingEventQuery(blockingEventType, true, blockedEventType);

        RestRequest request = RestRequestFactoryImpl.getInstance()
                .createDeleteRequest(ARGOS_REST_HOST, EventTypeEndpoint.getDeleteEventTypeUri(blockedEventType.getId()));

        assertEquals(HttpStatusCodes.ERROR, request.getResponseCode());

        JsonArray jsonBlockingEventType = jsonParser.parse(request.getResponse()).getAsJsonArray();
        assertEquals(1, jsonBlockingEventType.size());

        assertEquals(blockingEventType.getId(), jsonBlockingEventType.get(0).getAsLong());

        assertFalse(checkIfDeleted(blockedEventType, new ArrayList<>(PersistenceAdapterImpl.getInstance().getEventTypes())));
    }

    @Test
    public void testGetEventTypesAttributes() {
        List<TypeAttribute> attributes = ArgosTestUtil.createEventTypeAttributes(testEventType, true);

        RestRequest request = RestRequestFactoryImpl.getInstance()
                .createGetRequest(ARGOS_REST_HOST, EventTypeEndpoint.getEventTypeAttributesUri(testEventType.getId()));

        assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());

        JsonArray attributeArray =  jsonParser.parse(request.getResponse()).getAsJsonArray();

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
            assertTrue(attributeFound);
            foundAttributeJsons.add(attribute);
        }
        assertTrue(foundAttributeJsons.size() == attributeArray.size());
    }

    @Test
    public void testGetEventTypesAttributes_InvalidId_Success() {
        RestRequest request = RestRequestFactoryImpl.getInstance()
                .createGetRequest(ARGOS_REST_HOST, EventTypeEndpoint.getEventTypeAttributesUri(testEventType.getId() - 1));

        assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());
        assertEquals("[]", request.getResponse());
    }

    @Test
    public void testGetEventTypeQueries() {
        List<EventQuery> queries = ArgosTestUtil.createEventQueries(testEventType, true);

        RestRequest request = RestRequestFactoryImpl.getInstance()
                .createGetRequest(ARGOS_REST_HOST, EventTypeEndpoint.getEventTypeQueriesUri(testEventType.getId()));

        assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());

        JsonArray queriesArray =  jsonParser.parse(request.getResponse()).getAsJsonArray();

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
            assertTrue(queryFound);
            foundQueryJsons.add(jsonQuery);
        }
        assertTrue(foundQueryJsons.size() == queriesArray.size());
    }

    @Test
    public void testGetEventTypeQueries_InvalidId_Success() {
        RestRequest request = RestRequestFactoryImpl.getInstance()
                .createGetRequest(ARGOS_REST_HOST, EventTypeEndpoint.getEventTypeQueriesUri(testEventType.getId() - 1));

        assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());
        assertEquals("[]", request.getResponse());
    }

    @Test
    public void testGetEventTypeEntityMappings() {
        EventEntityMapping mapping = ArgosTestUtil.createEventEntityMapping(testEventType, ArgosTestUtil.createEntityType(true), "", true);
        List<MappingCondition> conditions = ArgosTestUtil.createMappingConditions(mapping, true, new PairImpl<>(1L, 2L));

        RestRequest request = RestRequestFactoryImpl.getInstance()
                .createGetRequest(ARGOS_REST_HOST, EventTypeEndpoint.getEventTypeEntityMappingsUri(testEventType.getId()));

        assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());

        JsonArray mappingsArray =  jsonParser.parse(request.getResponse()).getAsJsonArray();

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
    public void testGetEventTypeEntityMappings_InvalidId_Success() {
        RestRequest request = RestRequestFactoryImpl.getInstance()
                .createGetRequest(ARGOS_REST_HOST, EventTypeEndpoint.getEventTypeEntityMappingsUri(testEventType.getId() - 1));

        assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());
        assertEquals("[]", request.getResponse());
    }

    private JsonObject createEventTypeJson(EventType newEventType, boolean valid) {
        JsonObject newJsonEventType = new JsonObject();

        if(valid) {
            newJsonEventType.addProperty("Name", newEventType.getName());
        }
        newJsonEventType.addProperty("TimestampAttributeName", newEventType.getTimeStampAttributeId());

        JsonArray newJsonAttributes = new JsonArray();
        List<TypeAttribute> attributes = ArgosTestUtil.createEventTypeAttributes(newEventType, false);
        for (TypeAttribute att : attributes) {
            JsonObject newJsonAttribute = new JsonObject();
            newJsonAttribute.addProperty("Name", att.getName());
            newJsonAttributes.add(newJsonAttribute);
        }

        newJsonEventType.add("TypeAttributes", newJsonAttributes);

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

    private boolean checkIfDeleted(PersistenceArtifact existingArtifact, List<PersistenceArtifact> objectsToCheck) {
        boolean artifactDeleted = true;
        for (PersistenceArtifact artifact : objectsToCheck) {
            if (existingArtifact.getId() == artifact.getId()) {
                artifactDeleted = false;
                break;
            }
        }
        return artifactDeleted;
    }

    private void assertEventType(EventType expected, JsonObject actual) {
    	int eventsCount = PersistenceAdapterImpl.getInstance().getEventCountOfEventType(expected.getId());

		assertEquals(expected.getId(), actual.get("Id").getAsLong());
		assertEquals(expected.getName(), actual.get("Name").getAsString());
		assertEquals(expected.getTimeStampAttributeId(), actual.get("TimestampAttributeId").getAsLong());
		assertEquals(eventsCount, actual.get("NumberOfEvents").getAsInt());
	}
}
