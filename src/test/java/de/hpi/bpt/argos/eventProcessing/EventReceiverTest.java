package de.hpi.bpt.argos.eventProcessing;

import com.google.gson.JsonObject;
import de.hpi.bpt.argos.common.RestRequest;
import de.hpi.bpt.argos.common.RestRequestFactoryImpl;
import de.hpi.bpt.argos.core.ArgosTestParent;
import de.hpi.bpt.argos.eventProcessing.mapping.EventEntityMappingStatus;
import de.hpi.bpt.argos.eventProcessing.mapping.EventMappingObserver;
import de.hpi.bpt.argos.storage.PersistenceAdapterImpl;
import de.hpi.bpt.argos.storage.PersistenceArtifactUpdateType;
import de.hpi.bpt.argos.storage.dataModel.attribute.Attribute;
import de.hpi.bpt.argos.storage.dataModel.attribute.type.TypeAttribute;
import de.hpi.bpt.argos.storage.dataModel.entity.Entity;
import de.hpi.bpt.argos.storage.dataModel.entity.type.EntityType;
import de.hpi.bpt.argos.storage.dataModel.event.Event;
import de.hpi.bpt.argos.storage.dataModel.event.type.EventType;
import de.hpi.bpt.argos.storage.dataModel.event.type.StatusUpdatedEventType;
import de.hpi.bpt.argos.storage.dataModel.mapping.EventEntityMapping;
import de.hpi.bpt.argos.storage.dataModel.mapping.MappingCondition;
import de.hpi.bpt.argos.testUtil.ArgosTestUtil;
import de.hpi.bpt.argos.testUtil.WebSocket;
import de.hpi.bpt.argos.util.HttpStatusCodes;
import de.hpi.bpt.argos.util.ObjectWrapper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

public class EventReceiverTest extends ArgosTestParent {

	private static EntityType testEntityType;
	private static List<TypeAttribute> testEntityTypeAttributes;
	private static Entity testEntity;
	private static List<Attribute> testEntityAttributes;
	private static EventType testEventType;
	private static List<TypeAttribute> testEventTypeAttributes;
	private static EventEntityMapping testMapping;
	private static List<MappingCondition> testMappingConditions;

	@BeforeClass
	public static void initialize() {
		ArgosTestParent.setup();
		testEntityType = ArgosTestUtil.createEntityType(true);
		testEntityTypeAttributes = ArgosTestUtil.createEntityTypeAttributes(testEntityType, true);
		testEntity = ArgosTestUtil.createEntity(testEntityType, true);
		testEntityAttributes = ArgosTestUtil.createEntityAttributes(testEntityType, testEntity, true);
		testEventType = ArgosTestUtil.createEventType(false, true);
		testEventTypeAttributes = ArgosTestUtil.createEventTypeAttributes(testEventType, true);
		testMapping = ArgosTestUtil.createEventEntityMapping(testEventType, testEntityType, "", true);

		testMappingConditions = ArgosTestUtil.createMappingConditions(testMapping, testEventTypeAttributes, testEntityTypeAttributes, true);
	}

	@AfterClass
	public static void tearDown() {
		ArgosTestParent.tearDown();
	}

	@Test
	public void testReceiveEvent() throws Exception {
		String oldStatus = getEntityStatus();
		testMapping.setTargetStatus("");
		PersistenceAdapterImpl.getInstance().saveArtifacts(testMapping);
		int eventsCount = getEventsCount();

		WebSocket webSocket = WebSocket.buildWebSocket();

		RestRequest request = RestRequestFactoryImpl.getInstance()
				.createPostRequest(ARGOS_REST_HOST, getReceiveEventUri(testEventType.getId()));

		JsonObject event = getJsonEvent();

		setValidMapping(event);
		request.setContent(serializer.toJson(event));

		assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());

		assertEquals(oldStatus, getEntityStatus());

		List<String> webSocketMessages = webSocket.awaitMessages(1, 1000);
		ArgosTestUtil.assertWebSocketMessage(webSocketMessages.get(0), PersistenceArtifactUpdateType.CREATE, "Event");

		JsonObject message = jsonParser.parse(webSocketMessages.get(0)).getAsJsonArray().get(0).getAsJsonObject();
		assertEquals(testEntity.getId(), message.get("EntityId").getAsLong());
		assertEquals(testEventType.getId(), message.get("EventTypeId").getAsLong());
		assertEquals("Event", message.get("ArtifactType").getAsString());

		assertEquals(eventsCount + 1, getEventsCount());
	}

	@Test
	public void testReceiveEvent_InvalidEventTypeId_NotFound() {
		String targetStatus = "UnreachableStatus_" + ArgosTestUtil.getCurrentTimestamp();
		String oldStatus = getEntityStatus();
		testMapping.setTargetStatus(targetStatus);
		PersistenceAdapterImpl.getInstance().saveArtifacts(testMapping);

		RestRequest request = RestRequestFactoryImpl.getInstance()
				.createPostRequest(ARGOS_REST_HOST, getReceiveEventUri(testEventType.getId() - 1));

		JsonObject event = getJsonEvent();

		setValidMapping(event);
		request.setContent(serializer.toJson(event));
		assertEquals(HttpStatusCodes.NOT_FOUND, request.getResponseCode());

		// the entity status was not updated, although the mapping has a target status
		assertEquals(oldStatus, getEntityStatus());
	}

	@Test
	public void testReceiveEvent_InvalidMapping_Success() {
		String targetStatus = "UnreachableStatus_" + ArgosTestUtil.getCurrentTimestamp();
		String oldStatus = getEntityStatus();
		testMapping.setTargetStatus(targetStatus);
		PersistenceAdapterImpl.getInstance().saveArtifacts(testMapping);

		RestRequest request = RestRequestFactoryImpl.getInstance()
				.createPostRequest(ARGOS_REST_HOST, getReceiveEventUri(testEventType.getId()));

		JsonObject event = getJsonEvent();

		setInvalidMapping(event);
		request.setContent(serializer.toJson(event));
		assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());

		// the entity status was not updated, although the mapping has a target status
		assertEquals(oldStatus, getEntityStatus());
	}

	@Test
	public void testReceiveEvent_ChangeStatus_Success() throws Exception {
		String newStatus = "NewStatus_" + ArgosTestUtil.getCurrentTimestamp();
		testMapping.setTargetStatus(newStatus);
		PersistenceAdapterImpl.getInstance().saveArtifacts(testMapping);
		int eventsCount = getEventsCount();

		WebSocket webSocket = WebSocket.buildWebSocket();

		RestRequest request = RestRequestFactoryImpl.getInstance()
				.createPostRequest(ARGOS_REST_HOST, getReceiveEventUri(testEventType.getId()));

		JsonObject event = getJsonEvent();

		setValidMapping(event);
		request.setContent(serializer.toJson(event));
		assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());

		assertWebSocketMessages(webSocket, testEventType.getId(), testEntity.getId());

		assertEquals(eventsCount + 1, getEventsCount());
		assertEquals(newStatus, getEntityStatus());
	}

	@Test
	public void testReceiveEvent_CustomMapping_Success() throws Exception {
		Entity newEntity = ArgosTestUtil.createEntity(testEntityType, true);
		String targetStatus = "TargetStatus_" + ArgosTestUtil.getCurrentTimestamp();
		testMapping.setTargetStatus("UnreachableStatus_" + ArgosTestUtil.getCurrentTimestamp());
		String testEntityStatus = getEntityStatus();
		PersistenceAdapterImpl.getInstance().saveArtifacts(testMapping);
		int eventsCount = getEventsCount();

		ObjectWrapper<Boolean> mapperExecuted = new ObjectWrapper<>();
		mapperExecuted.set(false);

		EventCreationObserver customEventMapper = new EventCreationObserver() {
			@Override
			public void onEventCreated(EventEntityMappingStatus mappingStatus,
									   EventType eventType,
									   List<TypeAttribute> eventTypeAttributes,
									   Event event,
									   List<Attribute> eventAttributes) {

				assertFalse(mappingStatus.isMapped());
				mapperExecuted.set(true);

				mappingStatus.setEventOwner(newEntity);
				mappingStatus.getStatusUpdateStatus().setNewStatus(targetStatus);
			}
		};
		argos.addEventEntityMapper(customEventMapper);

		WebSocket webSocket = WebSocket.buildWebSocket();

		RestRequest request = RestRequestFactoryImpl.getInstance()
				.createPostRequest(ARGOS_REST_HOST, getReceiveEventUri(testEventType.getId()));

		JsonObject event = getJsonEvent();
		setValidMapping(event);

		request.setContent(serializer.toJson(event));
		assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());

		assertWebSocketMessages(webSocket, testEventType.getId(), newEntity.getId());
		argos.removeEventEntityMapper(customEventMapper);

		assertTrue(mapperExecuted.get());

		assertEquals(eventsCount + 1, getEventsCount());
		assertEquals(targetStatus, getEntityStatus(newEntity.getId()));
		assertEquals(testEntityStatus, getEntityStatus());
	}

	@Test
	public void testReceiveEvent_CustomStatusLogic_Success() throws Exception {
		String targetStatus = "TargetStatus_" + ArgosTestUtil.getCurrentTimestamp();
		testMapping.setTargetStatus("UnreachableStatus_" + ArgosTestUtil.getCurrentTimestamp());
		PersistenceAdapterImpl.getInstance().saveArtifacts(testMapping);
		int eventsCount = getEventsCount();

		ObjectWrapper<Boolean> statusLogicExecuted = new ObjectWrapper<>();
		statusLogicExecuted.set(false);

		EventMappingObserver customStatusLogic = new EventMappingObserver() {
			@Override
			public void onEventMapped(EventEntityMappingStatus processStatus) {
				statusLogicExecuted.set(true);

				assertTrue(processStatus.isMapped());
				assertFalse(processStatus.getStatusUpdateStatus().isStatusUpdated());

				processStatus.getStatusUpdateStatus().setNewStatus(targetStatus);
			}
		};
		argos.addEntityStatusCalculator(customStatusLogic);

		WebSocket webSocket = WebSocket.buildWebSocket();

		RestRequest request = RestRequestFactoryImpl.getInstance()
				.createPostRequest(ARGOS_REST_HOST, getReceiveEventUri(testEventType.getId()));

		JsonObject event = getJsonEvent();
		setValidMapping(event);

		request.setContent(serializer.toJson(event));
		assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());

		assertWebSocketMessages(webSocket, testEventType.getId(), testEntity.getId());
		argos.removeEntityStatusCalculator(customStatusLogic);

		assertTrue(statusLogicExecuted.get());
		assertEquals(eventsCount + 1, getEventsCount());
		assertEquals(targetStatus, getEntityStatus());
	}

	@Test
	public void testReceiveEvent_StatusUpdatedEvent_Success() throws Exception {
		StatusUpdatedEventType.setup(argos);

		String oldStatus = getEntityStatus();
		String newStatus = oldStatus + "_Updated";
		int statusUpdateEventsCount = getEventsCount(StatusUpdatedEventType.getInstance().getId());

		WebSocket webSocket = WebSocket.buildWebSocket();

		RestRequest request = RestRequestFactoryImpl.getInstance()
				.createPostRequest(ARGOS_REST_HOST, getReceiveEventUri(StatusUpdatedEventType.getInstance().getId()));

		JsonObject event = new JsonObject();
		event.addProperty("timestamp", ArgosTestUtil.getCurrentTimestamp());
		event.addProperty("oldStatus", oldStatus);
		event.addProperty("newStatus", newStatus);
		event.addProperty("causeEventId", 0);
		event.addProperty("causeEventTypeId", testEventType.getId());
		event.addProperty("entityId", testEntity.getId());

		request.setContent(serializer.toJson(event));

		assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());

		assertWebSocketMessages(webSocket, StatusUpdatedEventType.getInstance().getId(), testEntity.getId());

		assertEquals(statusUpdateEventsCount + 1, getEventsCount(StatusUpdatedEventType.getInstance().getId()));
		assertEquals(newStatus, getEntityStatus());
	}

	private JsonObject getJsonEvent() {
		JsonObject event = new JsonObject();

		for (TypeAttribute eventTypeAttribute : testEventTypeAttributes) {
			if (testEventType.getTimeStampAttributeId() == eventTypeAttribute.getId()) {
				event.addProperty(eventTypeAttribute.getName(), ArgosTestUtil.getCurrentTimestamp());
				continue;
			}

			event.addProperty(eventTypeAttribute.getName(), ArgosTestUtil.getRandomString());
		}

		return event;
	}

	private void setValidMapping(JsonObject event) {
		for (MappingCondition mappingCondition : testMappingConditions) {
			event.addProperty(
					getTypeAttributeName(testEventTypeAttributes, mappingCondition.getEventTypeAttributeId()),
					getAttributeValue(testEntityAttributes, mappingCondition.getEntityTypeAttributeId()));
		}
	}

	private void setInvalidMapping(JsonObject event) {
		for (MappingCondition mappingCondition : testMappingConditions) {
			event.addProperty(
					getTypeAttributeName(testEventTypeAttributes, mappingCondition.getEventTypeAttributeId()),
					getAttributeValue(testEntityAttributes, mappingCondition.getEntityTypeAttributeId()) + "_invalid");
		}
	}

	private int getEventsCount() {
		return getEventsCount(testEventType.getId());
	}

	private int getEventsCount(long eventTypeId) {
		return PersistenceAdapterImpl.getInstance().getEventCountOfEventType(eventTypeId);
	}

	private String getTypeAttributeName(List<TypeAttribute> typeAttributes, long typeAttributeId) {
		for (TypeAttribute typeAttribute : typeAttributes) {
			if (typeAttribute.getId() == typeAttributeId) {
				return typeAttribute.getName();
			}
		}

		return "";
	}

	private String getAttributeValue(List<Attribute> attributes, long typeAttributeId) {
		for (Attribute attribute : attributes) {
			if (attribute.getTypeAttributeId() == typeAttributeId) {
				return attribute.getValue();
			}
		}

		return "";
	}

	private void updateEntity() {
		testEntity = PersistenceAdapterImpl.getInstance().getEntity(testEntity.getId());
	}

	private String getEntityStatus() {
		updateEntity();
		return testEntity.getStatus();
	}

	private void assertWebSocketMessages(WebSocket webSocket, long eventTypeId, long entityId) throws Exception {
		List<String> webSocketMessages = webSocket.awaitMessages(2, 1000);

		for (String message : webSocketMessages) {
			if (message.contains("Event")) {
				ArgosTestUtil.assertWebSocketMessage(message, PersistenceArtifactUpdateType.CREATE, "Event");

				JsonObject jsonMessage = jsonParser.parse(message).getAsJsonArray().get(0).getAsJsonObject();
				assertEquals(eventTypeId, jsonMessage.get("EventTypeId").getAsLong());
				assertEquals(entityId, jsonMessage.get("EntityId").getAsLong());
				assertEquals("Event", jsonMessage.get("ArtifactType").getAsString());

				continue;
			} else if (message.contains("Entity")) {
				ArgosTestUtil.assertWebSocketMessage(message, PersistenceArtifactUpdateType.MODIFY, "Entity");
				continue;
			}

			throw new Exception("message was not expected");
		}
	}

	private String getEntityStatus(long entityId) {
		return PersistenceAdapterImpl.getInstance().getEntity(entityId).getStatus();
	}

	private String getReceiveEventUri(Object eventTypeId) {
		return EventReceiver.getReceiveEventBaseUri().replaceAll(EventReceiver.getEventTypeIdParameter(true), eventTypeId.toString());
	}
}
