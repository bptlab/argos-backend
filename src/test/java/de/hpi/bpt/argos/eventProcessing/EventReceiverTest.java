package de.hpi.bpt.argos.eventProcessing;

import com.google.gson.JsonObject;
import de.hpi.bpt.argos.common.RestRequest;
import de.hpi.bpt.argos.common.RestRequestFactoryImpl;
import de.hpi.bpt.argos.core.ArgosTestParent;
import de.hpi.bpt.argos.storage.PersistenceAdapterImpl;
import de.hpi.bpt.argos.storage.PersistenceArtifactUpdateType;
import de.hpi.bpt.argos.storage.dataModel.attribute.Attribute;
import de.hpi.bpt.argos.storage.dataModel.attribute.type.TypeAttribute;
import de.hpi.bpt.argos.storage.dataModel.entity.Entity;
import de.hpi.bpt.argos.storage.dataModel.entity.type.EntityType;
import de.hpi.bpt.argos.storage.dataModel.event.Event;
import de.hpi.bpt.argos.storage.dataModel.event.type.EventType;
import de.hpi.bpt.argos.storage.dataModel.mapping.EventEntityMapping;
import de.hpi.bpt.argos.storage.dataModel.mapping.MappingCondition;
import de.hpi.bpt.argos.util.ArgosTestUtil;
import de.hpi.bpt.argos.util.HttpStatusCodes;
import de.hpi.bpt.argos.util.WebSocket;
import javafx.util.Pair;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertEquals;

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
		testEntityType = ArgosTestUtil.createEntityType(true);
		testEntityTypeAttributes = ArgosTestUtil.createEntityTypeAttributes(testEntityType, true);
		testEntity = ArgosTestUtil.createEntity(testEntityType, true);
		testEntityAttributes = ArgosTestUtil.createEntityAttributes(testEntityType, testEntity, true);
		testEventType = ArgosTestUtil.createEventType(false, true);
		testEventTypeAttributes = ArgosTestUtil.createEventTypeAttributes(testEventType, true);
		testMapping = ArgosTestUtil.createEventEntityMapping(testEventType, testEntityType, "", true);

		testMappingConditions = ArgosTestUtil.createMappingConditions(testMapping, true,
				new Pair<>(testEventTypeAttributes.get(0).getId(), testEntityTypeAttributes.get(1).getId()));
	}

	@Test
	public void testReceiveEvent() throws Exception {
		String oldStatus = getEntityStatus();
		testMapping.setTargetStatus("");
		PersistenceAdapterImpl.getInstance().saveArtifacts(testMapping);

		WebSocket webSocket = WebSocket.buildWebSocket();

		RestRequest request = RestRequestFactoryImpl.getInstance()
				.createPostRequest(ARGOS_REST_HOST, getReceiveEventUri(testEventType.getId()));

		JsonObject event = new JsonObject();

		for (TypeAttribute eventTypeAttribute : testEventTypeAttributes) {
			if (testEventType.getTimeStampAttributeId() == eventTypeAttribute.getId()) {
				event.addProperty(eventTypeAttribute.getName(), ArgosTestUtil.getCurrentTimestamp());
				continue;
			}

			event.addProperty(eventTypeAttribute.getName(), ArgosTestUtil.getRandomString());
		}

		setValidMapping(event);
		request.setContent(serializer.toJson(event));

		assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());

		List<Event> events = PersistenceAdapterImpl.getInstance().getEventsOfEventType(testEventType.getId());
		assertEquals(true, events.size() >= 1);

		assertEquals(oldStatus, getEntityStatus());

		List<String> webSocketMessages = webSocket.awaitMessages(1, 1000);
		assertEquals(1, webSocketMessages.size());
		ArgosTestUtil.assertWebSocketMessage(webSocketMessages.get(0), PersistenceArtifactUpdateType.CREATE, "Event");
	}

	@Test
	public void testReceiveEvent_InvalidEventTypeId_NotFound() {
		String oldStatus = getEntityStatus();
		testMapping.setTargetStatus("");
		PersistenceAdapterImpl.getInstance().saveArtifacts(testMapping);

		RestRequest request = RestRequestFactoryImpl.getInstance()
				.createPostRequest(ARGOS_REST_HOST, getReceiveEventUri(testEventType.getId() - 1));

		JsonObject event = new JsonObject();

		for (TypeAttribute eventTypeAttribute : testEventTypeAttributes) {
			if (testEventType.getTimeStampAttributeId() == eventTypeAttribute.getId()) {
				event.addProperty(eventTypeAttribute.getName(), ArgosTestUtil.getCurrentTimestamp());
				continue;
			}

			event.addProperty(eventTypeAttribute.getName(), ArgosTestUtil.getRandomString());
		}

		setValidMapping(event);
		request.setContent(serializer.toJson(event));
		assertEquals(HttpStatusCodes.NOT_FOUND, request.getResponseCode());

		assertEquals(oldStatus, getEntityStatus());
	}

	@Test
	public void testReceiveEvent_InvalidMapping_BadRequest() {
		String oldStatus = getEntityStatus();
		testMapping.setTargetStatus("");
		PersistenceAdapterImpl.getInstance().saveArtifacts(testMapping);

		RestRequest request = RestRequestFactoryImpl.getInstance()
				.createPostRequest(ARGOS_REST_HOST, getReceiveEventUri(testEventType.getId()));

		JsonObject event = new JsonObject();

		for (TypeAttribute eventTypeAttribute : testEventTypeAttributes) {
			if (testEventType.getTimeStampAttributeId() == eventTypeAttribute.getId()) {
				event.addProperty(eventTypeAttribute.getName(), ArgosTestUtil.getCurrentTimestamp());
				continue;
			}

			event.addProperty(eventTypeAttribute.getName(), ArgosTestUtil.getRandomString());
		}

		setInvalidMapping(event);
		request.setContent(serializer.toJson(event));
		assertEquals(HttpStatusCodes.BAD_REQUEST, request.getResponseCode());

		assertEquals(oldStatus, getEntityStatus());
	}

	@Test
	public void testReceiveEvent_ChangeStatus_Success() throws Exception {
		String newStatus = "NewStatus_" + ArgosTestUtil.getCurrentTimestamp();
		testMapping.setTargetStatus(newStatus);
		PersistenceAdapterImpl.getInstance().saveArtifacts(testMapping);

		WebSocket webSocket = WebSocket.buildWebSocket();

		RestRequest request = RestRequestFactoryImpl.getInstance()
				.createPostRequest(ARGOS_REST_HOST, getReceiveEventUri(testEventType.getId()));

		JsonObject event = new JsonObject();

		for (TypeAttribute eventTypeAttribute : testEventTypeAttributes) {
			if (testEventType.getTimeStampAttributeId() == eventTypeAttribute.getId()) {
				event.addProperty(eventTypeAttribute.getName(), ArgosTestUtil.getCurrentTimestamp());
				continue;
			}

			event.addProperty(eventTypeAttribute.getName(), ArgosTestUtil.getRandomString());
		}

		setValidMapping(event);
		request.setContent(serializer.toJson(event));
		assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());

		List<Event> events = PersistenceAdapterImpl.getInstance().getEventsOfEventType(testEventType.getId());
		assertEquals(1, events.size());
		assertEquals(newStatus, getEntityStatus());

		List<String> webSocketMessages = webSocket.awaitMessages(2, 1000);

		for (String message : webSocketMessages) {
			if (message.contains("Event")) {
				ArgosTestUtil.assertWebSocketMessage(message, PersistenceArtifactUpdateType.CREATE, "Event");
				continue;
			} else if (message.contains("Entity")) {
				ArgosTestUtil.assertWebSocketMessage(message, PersistenceArtifactUpdateType.MODIFY, "Entity");
				continue;
			}

			throw new Exception("message was not expected");
		}
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

	private String getReceiveEventUri(Object eventTypeId) {
		return EventReceiver.getReceiveEventBaseUri().replaceAll(EventReceiver.getEventTypeIdParameter(true), eventTypeId.toString());
	}
}
