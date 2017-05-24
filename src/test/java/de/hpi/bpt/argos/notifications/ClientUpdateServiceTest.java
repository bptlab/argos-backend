package de.hpi.bpt.argos.notifications;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.hpi.bpt.argos.common.RestRequest;
import de.hpi.bpt.argos.common.RestRequestFactoryImpl;
import de.hpi.bpt.argos.core.ArgosTestParent;
import de.hpi.bpt.argos.eventProcessing.EventReceiver;
import de.hpi.bpt.argos.storage.dataModel.entity.Entity;
import de.hpi.bpt.argos.storage.dataModel.entity.type.EntityType;
import de.hpi.bpt.argos.storage.dataModel.event.type.EventType;
import de.hpi.bpt.argos.storage.dataModel.event.type.StatusUpdatedEventType;
import de.hpi.bpt.argos.testUtil.ArgosTestUtil;
import de.hpi.bpt.argos.testUtil.WebSocket;
import de.hpi.bpt.argos.util.HttpStatusCodes;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;

public class ClientUpdateServiceTest extends ArgosTestParent {

	private static EntityType testEntityType;

	@BeforeClass
	public static void initialize() {
		ArgosTestParent.setup(1000);

		testEntityType = ArgosTestUtil.createEntityType(true);
	}

	@AfterClass
	public static void tearDown() {
		ArgosTestParent.tearDown();
	}

	@Test
	public void testConnection() {
		WebSocket webSocket = new WebSocket();

		assertEquals(true, webSocket.connectToServer());
	}

	@Test
	public void testNotificationBatching() throws Exception {
		EventType eventType = StatusUpdatedEventType.getInstance();
		Entity testEntity1 = ArgosTestUtil.createEntity(testEntityType, true);
		Entity testEntity2 = ArgosTestUtil.createEntity(testEntityType, true);

		WebSocket webSocket = WebSocket.buildWebSocket();
		RestRequest request = RestRequestFactoryImpl.getInstance()
				.createPostRequest(ARGOS_REST_HOST, getReceiveEventUri(eventType.getId()));

		request.setContent(serializer.toJson(createStatusUpdatedEventJson(testEntity1)));
		assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());

		request = RestRequestFactoryImpl.getInstance()
				.createPostRequest(ARGOS_REST_HOST, getReceiveEventUri(eventType.getId()));

		request.setContent(serializer.toJson(createStatusUpdatedEventJson(testEntity2)));
		assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());

		List<String> webSocketMessages = webSocket.awaitMessages(1, 2000);
		JsonArray notifications = jsonParser.parse(webSocketMessages.get(0)).getAsJsonArray();

		assertEquals(4, notifications.size());

		for (JsonElement element : notifications) {
			JsonObject notification = element.getAsJsonObject();

			if (notification.get("ArtifactType").getAsString().equals("Event")) {
				continue;
			}

			if (notification.get("ArtifactId").getAsLong() == testEntity1.getId()
					|| notification.get("ArtifactId").getAsLong() == testEntity2.getId()) {
				continue;
			}

			fail();
		}
	}

	private JsonObject createStatusUpdatedEventJson(Entity updatedEntity) {
		JsonObject event = new JsonObject();

		event.addProperty("Timestamp", ArgosTestUtil.getCurrentTimestamp());
		event.addProperty("OldStatus", updatedEntity.getStatus());
		event.addProperty("NewStatus", updatedEntity.getStatus() + "_Updated");
		event.addProperty("CauseEventId", ArgosTestUtil.getRandomInteger(1, 10));
		event.addProperty("CauseEventTypeId", ArgosTestUtil.getRandomInteger(1, 10));
		event.addProperty("EntityId", updatedEntity.getId());

		return event;
	}

	private String getReceiveEventUri(Object eventTypeId) {
		return EventReceiver.getReceiveEventBaseUri()
				.replaceAll(EventReceiver.getEventTypeIdParameter(true), eventTypeId.toString());
	}
}
