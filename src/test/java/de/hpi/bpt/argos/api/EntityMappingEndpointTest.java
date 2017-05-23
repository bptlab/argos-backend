package de.hpi.bpt.argos.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.hpi.bpt.argos.api.entityMapping.EntityMappingEndpoint;
import de.hpi.bpt.argos.common.RestRequest;
import de.hpi.bpt.argos.common.RestRequestFactoryImpl;
import de.hpi.bpt.argos.core.ArgosTestParent;
import de.hpi.bpt.argos.storage.PersistenceAdapterImpl;
import de.hpi.bpt.argos.storage.PersistenceArtifactUpdateType;
import de.hpi.bpt.argos.storage.dataModel.attribute.type.TypeAttribute;
import de.hpi.bpt.argos.storage.dataModel.entity.type.EntityType;
import de.hpi.bpt.argos.storage.dataModel.event.type.EventType;
import de.hpi.bpt.argos.storage.dataModel.event.type.StatusUpdatedEventType;
import de.hpi.bpt.argos.storage.dataModel.mapping.EventEntityMapping;
import de.hpi.bpt.argos.storage.dataModel.mapping.MappingCondition;
import de.hpi.bpt.argos.testUtil.ArgosTestUtil;
import de.hpi.bpt.argos.testUtil.WebSocket;
import de.hpi.bpt.argos.util.HttpStatusCodes;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class EntityMappingEndpointTest extends ArgosTestParent {

	private static EntityType testEntityType;
	private static List<TypeAttribute> testEntityTypeAttributes;
	private static EventType testEventType;
	private static List<TypeAttribute> testEventTypeAttributes;

	@BeforeClass
	public static void initialize() {
		ArgosTestParent.setup();
		testEntityType = ArgosTestUtil.createEntityType(true);
		testEntityTypeAttributes = ArgosTestUtil.createEntityTypeAttributes(testEntityType, true);
		testEventType = ArgosTestUtil.createEventType(false, true);
		testEventTypeAttributes = ArgosTestUtil.createEventTypeAttributes(testEventType, true);
	}

	@AfterClass
	public static void tearDown() {
		ArgosTestParent.tearDown();
	}

	@Test
	public void testCreateEntityMapping() throws Exception {
		String targetStatus = "TargetStatus_" + ArgosTestUtil.getCurrentTimestamp();
		int entityMappingsCount = getMappingsCount();

		WebSocket webSocket = WebSocket.buildWebSocket();

		RestRequest request = RestRequestFactoryImpl.getInstance().createPostRequest(ARGOS_REST_HOST, getCreateEntityMappingUri());

		JsonObject newMapping = createNewMappingJson();
		newMapping.addProperty("TargetStatus", targetStatus);

		request.setContent(serializer.toJson(newMapping));

		assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());

		assertEquals(entityMappingsCount + 1, getMappingsCount());

		List<String> webSocketMessages = webSocket.awaitMessages(1, 1000);
		ArgosTestUtil.assertWebSocketMessage(webSocketMessages.get(0), PersistenceArtifactUpdateType.CREATE, "EventEntityMapping");

		JsonObject notification = jsonParser.parse(webSocketMessages.get(0)).getAsJsonArray().get(0).getAsJsonObject();

		EventEntityMapping createdMapping = PersistenceAdapterImpl.getInstance().getEventEntityMapping(notification.get("ArtifactId").getAsLong());
		assertMapping(createdMapping, targetStatus);
	}

	@Test
	public void testCreateEntityMapping_StatusUpdateEventType_Forbidden() {
		EventType statusUpdatedEventType = StatusUpdatedEventType.getInstance();
		List<TypeAttribute> statusUpdatedTypeAttributes = PersistenceAdapterImpl.getInstance()
				.getTypeAttributes(statusUpdatedEventType.getId());

		RestRequest request = RestRequestFactoryImpl.getInstance().createPostRequest(ARGOS_REST_HOST, getCreateEntityMappingUri());

		JsonObject newMapping = createNewMappingJson(statusUpdatedEventType, statusUpdatedTypeAttributes, testEntityType, testEntityTypeAttributes);

		request.setContent(serializer.toJson(newMapping));

		assertEquals(HttpStatusCodes.FORBIDDEN, request.getResponseCode());
	}

	@Test
	public void testCreateEntityMapping_InvalidEntityTypeId_BadRequest() {
		RestRequest request = RestRequestFactoryImpl.getInstance().createPostRequest(ARGOS_REST_HOST, getCreateEntityMappingUri());

		JsonObject newMapping = createNewMappingJson();
		newMapping.addProperty("EntityTypeId", 0);

		request.setContent(serializer.toJson(newMapping));

		assertEquals(HttpStatusCodes.BAD_REQUEST, request.getResponseCode());
	}

	@Test
	public void testCreateEntityMapping_InvalidEventTypeId_BadRequest() {
		RestRequest request = RestRequestFactoryImpl.getInstance().createPostRequest(ARGOS_REST_HOST, getCreateEntityMappingUri());

		JsonObject newMapping = createNewMappingJson();
		newMapping.addProperty("EventTypeId", 0);

		request.setContent(serializer.toJson(newMapping));

		assertEquals(HttpStatusCodes.BAD_REQUEST, request.getResponseCode());
	}

	@Test
	public void testCreateEntityMapping_InvalidEntityAttributeTypeId_BadRequest() {
		RestRequest request = RestRequestFactoryImpl.getInstance().createPostRequest(ARGOS_REST_HOST, getCreateEntityMappingUri());

		JsonObject newMapping = createNewMappingJson();
		newMapping.get("EventEntityMappingConditions")
				.getAsJsonArray().get(0).getAsJsonObject()
				.addProperty("EntityTypeAttributeId", 0);

		request.setContent(serializer.toJson(newMapping));

		assertEquals(HttpStatusCodes.BAD_REQUEST, request.getResponseCode());
	}

	@Test
	public void testCreateEntityMapping_InvalidEventAttributeTypeId_BadRequest() {
		RestRequest request = RestRequestFactoryImpl.getInstance().createPostRequest(ARGOS_REST_HOST, getCreateEntityMappingUri());

		JsonObject newMapping = createNewMappingJson();
		newMapping.get("EventEntityMappingConditions")
				.getAsJsonArray().get(0).getAsJsonObject()
				.addProperty("EventTypeAttributeId", 0);

		request.setContent(serializer.toJson(newMapping));

		assertEquals(HttpStatusCodes.BAD_REQUEST, request.getResponseCode());
	}

	@Test
	public void testDeleteEntityMapping() throws Exception {
		EventEntityMapping testMapping = createMappingWithConditions();
		int entityMappingsCount = getMappingsCount();

		WebSocket webSocket = WebSocket.buildWebSocket();

		RestRequest request = RestRequestFactoryImpl.getInstance()
				.createDeleteRequest(ARGOS_REST_HOST, getDeleteEntityMappingUri(testMapping.getId()));

		assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());

		assertEquals(entityMappingsCount - 1, getMappingsCount());
		assertEquals(null, PersistenceAdapterImpl.getInstance().getEventEntityMapping(testMapping.getId()));
		assertEquals(0, PersistenceAdapterImpl.getInstance().getMappingConditions(testMapping.getId()).size());

		List<String> webSocketMessages = webSocket.awaitMessages(1, 1000);
		ArgosTestUtil.assertWebSocketMessage(webSocketMessages.get(0), PersistenceArtifactUpdateType.DELETE, "EventEntityMapping");
	}

	@Test
	public void testDeleteEntityMapping_InvalidId_BadRequest() {
		EventEntityMapping testMapping = createMappingWithConditions();
		int entityMappingsCount = getMappingsCount();

		RestRequest request = RestRequestFactoryImpl.getInstance()
				.createDeleteRequest(ARGOS_REST_HOST, getDeleteEntityMappingUri(0));

		assertEquals(HttpStatusCodes.BAD_REQUEST, request.getResponseCode());
		assertEquals(entityMappingsCount, getMappingsCount());
	}

	@Test
	public void testEditEntityMapping() throws Exception {
		EventEntityMapping testMapping = createMappingWithConditions();
		EntityType newTargetEntityType = ArgosTestUtil.createEntityType(true);
		List<TypeAttribute> newTargetEntityTypeAttributes = ArgosTestUtil.createEntityTypeAttributes(newTargetEntityType, true);
		EventType newTargetEventType = ArgosTestUtil.createEventType(true, true);
		List<TypeAttribute> newTargetEventTypeAttributes = ArgosTestUtil.createEventTypeAttributes(newTargetEventType, true);

		String targetStatus = "TargetStatus_" + ArgosTestUtil.getCurrentTimestamp();
		int entityMappingsCount = getMappingsCount(newTargetEventType);

		WebSocket webSocket = WebSocket.buildWebSocket();

		RestRequest request = RestRequestFactoryImpl.getInstance()
				.createPutRequest(ARGOS_REST_HOST, getEditEntityMappingUri(testMapping.getId()));

		JsonObject newMapping = new JsonObject();

		newMapping.addProperty("EventTypeId", newTargetEventType.getId());
		newMapping.addProperty("EntityTypeId", newTargetEntityType.getId());
		newMapping.addProperty("TargetStatus", targetStatus);

		JsonArray mappingConditions = new JsonArray();

		JsonObject newMappingCondition = new JsonObject();
		newMappingCondition.addProperty("EventTypeAttributeId", newTargetEventTypeAttributes.get(0).getId());
		newMappingCondition.addProperty("EntityTypeAttributeId", newTargetEntityTypeAttributes.get(0).getId());

		mappingConditions.add(newMappingCondition);

		newMapping.add("EventEntityMappingConditions", mappingConditions);

		request.setContent(serializer.toJson(newMapping));

		assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());

		assertEquals(entityMappingsCount + 1, getMappingsCount(newTargetEventType));

		EventEntityMapping updatedMapping = PersistenceAdapterImpl.getInstance().getEventEntityMapping(testMapping.getId());
		assertEquals(newTargetEventType.getId(), updatedMapping.getEventTypeId());
		assertEquals(newTargetEntityType.getId(), updatedMapping.getEntityTypeId());
		assertEquals(targetStatus, updatedMapping.getTargetStatus());

		List<MappingCondition> updatedMappingConditions = PersistenceAdapterImpl.getInstance().getMappingConditions(updatedMapping.getId());
		assertEquals(1, updatedMappingConditions.size());

		MappingCondition updatedMappingCondition = updatedMappingConditions.get(0);
		assertEquals(newTargetEventTypeAttributes.get(0).getId(), updatedMappingCondition.getEventTypeAttributeId());
		assertEquals(newTargetEntityTypeAttributes.get(0).getId(), updatedMappingCondition.getEntityTypeAttributeId());

		List<String> webSocketMessages = webSocket.awaitMessages(1, 1000);
		ArgosTestUtil.assertWebSocketMessage(webSocketMessages.get(0), PersistenceArtifactUpdateType.MODIFY, "EventEntityMapping");
	}

	@Test
	public void testEditEntityMapping_StatusUpdateEventType_Forbidden() {
		EventType statusUpdatedEventType = StatusUpdatedEventType.getInstance();
		List<TypeAttribute> statusUpdatedTypeAttributes = PersistenceAdapterImpl.getInstance().getTypeAttributes(statusUpdatedEventType.getId());
		EventEntityMapping testMapping = ArgosTestUtil.createEventEntityMapping(testEventType, testEntityType, "", true);

		RestRequest request = RestRequestFactoryImpl.getInstance()
				.createPutRequest(ARGOS_REST_HOST, getEditEntityMappingUri(testMapping.getId()));

		JsonObject updatedMapping = createNewMappingJson(statusUpdatedEventType,
				statusUpdatedTypeAttributes,
				testEntityType,
				testEntityTypeAttributes);

		request.setContent(serializer.toJson(updatedMapping));

		assertEquals(HttpStatusCodes.FORBIDDEN, request.getResponseCode());
	}

	@Test
	public void testEditEntityMapping_InvalidEntityTypeId_BadRequest() {
		EventEntityMapping testMapping = createMappingWithConditions();
		String oldStatus = testMapping.getTargetStatus();
		int entityMappingsCount = getMappingsCount();

		RestRequest request = RestRequestFactoryImpl.getInstance()
				.createPutRequest(ARGOS_REST_HOST, getEditEntityMappingUri(testMapping.getId()));

		JsonObject newMapping = createNewMappingJson();
		newMapping.addProperty("EntityTypeId", 0);

		request.setContent(serializer.toJson(newMapping));

		assertEquals(HttpStatusCodes.BAD_REQUEST, request.getResponseCode());

		assertEquals(entityMappingsCount, getMappingsCount());

		assertMapping(PersistenceAdapterImpl.getInstance().getEventEntityMapping(testMapping.getId()), oldStatus);
	}

	@Test
	public void testEditEntityMapping_InvalidEventTypeId_BadRequest() {
		EventEntityMapping testMapping = createMappingWithConditions();
		String oldStatus = testMapping.getTargetStatus();
		int entityMappingsCount = getMappingsCount();

		RestRequest request = RestRequestFactoryImpl.getInstance()
				.createPutRequest(ARGOS_REST_HOST, getEditEntityMappingUri(testMapping.getId()));

		JsonObject newMapping = createNewMappingJson();
		newMapping.addProperty("EventTypeId", 0);

		request.setContent(serializer.toJson(newMapping));

		assertEquals(HttpStatusCodes.BAD_REQUEST, request.getResponseCode());

		assertEquals(entityMappingsCount, getMappingsCount());

		assertMapping(PersistenceAdapterImpl.getInstance().getEventEntityMapping(testMapping.getId()), oldStatus);
	}

	@Test
	public void testEditEntityMapping_InvalidEntityAttributeTypeId_BadRequest() {
		EventEntityMapping testMapping = createMappingWithConditions();
		String oldStatus = testMapping.getTargetStatus();
		int entityMappingsCount = getMappingsCount();

		RestRequest request = RestRequestFactoryImpl.getInstance()
				.createPutRequest(ARGOS_REST_HOST, getEditEntityMappingUri(testMapping.getId()));

		JsonObject newMapping = createNewMappingJson();
		newMapping.get("EventEntityMappingConditions")
				.getAsJsonArray().get(0).getAsJsonObject()
				.addProperty("EntityTypeAttributeId", 0);

		request.setContent(serializer.toJson(newMapping));

		assertEquals(HttpStatusCodes.BAD_REQUEST, request.getResponseCode());

		assertEquals(entityMappingsCount, getMappingsCount());

		assertMapping(PersistenceAdapterImpl.getInstance().getEventEntityMapping(testMapping.getId()), oldStatus);
	}

	@Test
	public void testEditEntityMapping_InvalidEventAttributeTypeId_BadRequest() {
		EventEntityMapping testMapping = createMappingWithConditions();
		String oldStatus = testMapping.getTargetStatus();
		int entityMappingsCount = getMappingsCount();

		RestRequest request = RestRequestFactoryImpl.getInstance()
				.createPutRequest(ARGOS_REST_HOST, getEditEntityMappingUri(testMapping.getId()));

		JsonObject newMapping = createNewMappingJson();
		newMapping.get("EventEntityMappingConditions")
				.getAsJsonArray().get(0).getAsJsonObject()
				.addProperty("EventTypeAttributeId", 0);

		request.setContent(serializer.toJson(newMapping));

		assertEquals(HttpStatusCodes.BAD_REQUEST, request.getResponseCode());

		assertEquals(entityMappingsCount, getMappingsCount());

		assertMapping(PersistenceAdapterImpl.getInstance().getEventEntityMapping(testMapping.getId()), oldStatus);
	}

	private EventEntityMapping createMappingWithConditions() {
		EventEntityMapping testMapping = ArgosTestUtil.createEventEntityMapping(testEventType, testEntityType, "", true);
		List<MappingCondition> testMappingConditions = ArgosTestUtil
				.createMappingConditions(testMapping, testEventTypeAttributes, testEntityTypeAttributes, true);

		return testMapping;
	}

	private int getMappingsCount(EventType owner) {
		return PersistenceAdapterImpl.getInstance().getEventEntityMappingsForEventType(owner.getId()).size();
	}

	private int getMappingsCount() {
		return getMappingsCount(testEventType);
	}

	private void assertMapping(EventEntityMapping mapping, String expectedStatus) {
		assertEquals(testEventType.getId(), mapping.getEventTypeId());
		assertEquals(testEntityType.getId(), mapping.getEntityTypeId());
		assertEquals(expectedStatus, mapping.getTargetStatus());

		List<MappingCondition> updatedMappingConditions = PersistenceAdapterImpl.getInstance().getMappingConditions(mapping.getId());
		assertEquals(1, updatedMappingConditions.size());

		MappingCondition updatedMappingCondition = updatedMappingConditions.get(0);
		assertEquals(testEventTypeAttributes.get(0).getId(), updatedMappingCondition.getEventTypeAttributeId());
		assertEquals(testEntityTypeAttributes.get(0).getId(), updatedMappingCondition.getEntityTypeAttributeId());
	}

	private JsonObject createNewMappingJson() {
		return createNewMappingJson(testEventType, testEventTypeAttributes, testEntityType, testEntityTypeAttributes);
	}

	private JsonObject createNewMappingJson(EventType eventType,
											List<TypeAttribute> eventTypeAttributes,
											EntityType entityType,
											List<TypeAttribute> entityTypeAttributes) {
		JsonObject newMapping = new JsonObject();

		newMapping.addProperty("EventTypeId", eventType.getId());
		newMapping.addProperty("EntityTypeId", entityType.getId());
		newMapping.addProperty("TargetStatus", "NewStatus_" + ArgosTestUtil.getCurrentTimestamp());

		JsonArray mappingConditions = new JsonArray();

		JsonObject newMappingCondition = new JsonObject();
		newMappingCondition.addProperty("EventTypeAttributeId", eventTypeAttributes.get(0).getId());
		newMappingCondition.addProperty("EntityTypeAttributeId", entityTypeAttributes.get(0).getId());

		mappingConditions.add(newMappingCondition);

		newMapping.add("EventEntityMappingConditions", mappingConditions);

		return newMapping;
	}

	private String getCreateEntityMappingUri() {
		return EntityMappingEndpoint.getCreateEntityMappingBaseUri();
	}

	private String getDeleteEntityMappingUri(Object entityMappingId) {
		return EntityMappingEndpoint.getDeleteEntityMappingBaseUri()
				.replaceAll(EntityMappingEndpoint.getEntityMappingIdParameter(true), entityMappingId.toString());
	}

	private String getEditEntityMappingUri(Object entityMappingId) {
		return EntityMappingEndpoint.getEditEntityMappingBaseUri()
				.replaceAll(EntityMappingEndpoint.getEntityMappingIdParameter(true), entityMappingId.toString());
	}
}
