package de.hpi.bpt.argos.api;


import com.google.gson.JsonObject;
import de.hpi.bpt.argos.api.eventQuery.EventQueryEndpoint;
import de.hpi.bpt.argos.common.RestRequest;
import de.hpi.bpt.argos.common.RestRequestFactoryImpl;
import de.hpi.bpt.argos.core.ArgosTestParent;
import de.hpi.bpt.argos.storage.PersistenceAdapter;
import de.hpi.bpt.argos.storage.PersistenceAdapterImpl;
import de.hpi.bpt.argos.storage.PersistenceArtifactUpdateType;
import de.hpi.bpt.argos.storage.dataModel.event.query.EventQuery;
import de.hpi.bpt.argos.storage.dataModel.event.type.EventType;
import de.hpi.bpt.argos.testUtil.ArgosTestUtil;
import de.hpi.bpt.argos.testUtil.WebSocket;
import de.hpi.bpt.argos.util.HttpStatusCodes;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertNotNull;

public class EventQueryEndpointTest extends ArgosTestParent {

	private static EventType testEventType;

	@BeforeClass
	public static void initialize() {
		testEventType = ArgosTestUtil.createEventType(true, true);
	}

	@Test
	public void testCreateEventQuery() throws Exception {
		EventQuery newEventQuery = ArgosTestUtil.createEventQuery(testEventType, false);

		int eventQueriesCount = getEventQueriesCount();

		WebSocket webSocket = WebSocket.buildWebSocket();
		RestRequest request = RestRequestFactoryImpl.getInstance().createPostRequest(ARGOS_REST_HOST, getCreateEventQueryUri());

		JsonObject eventQueryJson = createEventQueryJson(newEventQuery, true);

		request.setContent(serializer.toJson(eventQueryJson));

		assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());
		assertEquals(eventQueriesCount + 1, getEventQueriesCount());

		List<String> webSocketMessages = webSocket.awaitMessages(1, 1000);
		ArgosTestUtil.assertWebSocketMessage(webSocketMessages.get(0), PersistenceArtifactUpdateType.CREATE, "EventQuery");

		JsonObject notification = jsonParser.parse(webSocketMessages.get(0)).getAsJsonArray().get(0).getAsJsonObject();
		EventQuery createdQuery = PersistenceAdapterImpl.getInstance().getEventQuery(notification.get("ArtifactId").getAsLong());

		assertEquals(newEventQuery.getTypeId(), createdQuery.getTypeId());
		assertEquals(newEventQuery.getDescription(), createdQuery.getDescription());
		assertEquals(newEventQuery.getQuery(), createdQuery.getQuery());
	}

	@Test
	public void testCreateEventQuery_EmptyJson_BadRequest() {
		int eventQueriesCount = getEventQueriesCount();

		RestRequest request = RestRequestFactoryImpl.getInstance().createPostRequest(ARGOS_REST_HOST, getCreateEventQueryUri());

		JsonObject newEventQuery = new JsonObject();
		request.setContent(serializer.toJson(newEventQuery));

		assertEquals(HttpStatusCodes.BAD_REQUEST, request.getResponseCode());
		assertEquals(eventQueriesCount, getEventQueriesCount());
	}

	@Test
	public void testCreateEventQuery_InvalidEventTypeId_BadRequest() {
		EventQuery newEventQuery = ArgosTestUtil.createEventQuery(testEventType, false);

		int eventQueriesCount = getEventQueriesCount();

		RestRequest request = RestRequestFactoryImpl.getInstance().createPostRequest(ARGOS_REST_HOST, getCreateEventQueryUri());

		JsonObject eventQueryJson = createEventQueryJson(newEventQuery, false);
		request.setContent(serializer.toJson(eventQueryJson));

		assertEquals(HttpStatusCodes.BAD_REQUEST, request.getResponseCode());
		assertEquals(eventQueriesCount, getEventQueriesCount());
	}

	@Test
	public void testDeleteEventQuery() throws Exception {
		EventQuery testEventQuery = ArgosTestUtil.createEventQuery(testEventType, true);
		int eventQueriesCount = getEventQueriesCount();

		WebSocket webSocket = WebSocket.buildWebSocket();

		RestRequest request = RestRequestFactoryImpl.getInstance()
				.createDeleteRequest(ARGOS_REST_HOST, getDeleteEventQueryUri(testEventQuery.getId()));

		assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());
		assertEquals(eventQueriesCount - 1, getEventQueriesCount());
		assertNull(PersistenceAdapterImpl.getInstance().getEventQuery(testEventQuery.getId()));

		List<String> webSocketMessages = webSocket.awaitMessages(1, 1000);
		ArgosTestUtil.assertWebSocketMessage(webSocketMessages.get(0), PersistenceArtifactUpdateType.DELETE, "EventQuery");

		JsonObject notification = jsonParser.parse(webSocketMessages.get(0)).getAsJsonArray().get(0).getAsJsonObject();
		assertEquals(testEventQuery.getId(), notification.get("ArtifactId").getAsLong());
	}

	@Test
	public void testDeleteEventQuery_InvalidEventQueryId_BadRequest() {
		EventQuery testEventQuery = ArgosTestUtil.createEventQuery(testEventType, true);
		int eventQueriesCount = getEventQueriesCount();

		RestRequest request = RestRequestFactoryImpl.getInstance()
				.createDeleteRequest(ARGOS_REST_HOST, getDeleteEventQueryUri(0));

		assertEquals(HttpStatusCodes.BAD_REQUEST, request.getResponseCode());
		assertEquals(eventQueriesCount, getEventQueriesCount());
		assertNotNull(PersistenceAdapterImpl.getInstance().getEventQuery(testEventQuery.getId()));
	}

	@Test
	public void testDeleteEventQuery_LastQueryMustNotBeDeleted_Forbidden() {
		EventType undeletableEventType = ArgosTestUtil.createEventType(false, true);
		EventQuery testEventQuery = ArgosTestUtil.createEventQuery(undeletableEventType, true);

		RestRequest request = RestRequestFactoryImpl.getInstance()
				.createDeleteRequest(ARGOS_REST_HOST, getDeleteEventQueryUri(testEventQuery.getId()));

		assertEquals(HttpStatusCodes.FORBIDDEN, request.getResponseCode());
		assertEquals(1, getEventQueriesCount(undeletableEventType.getId()));
	}

	@Test
	public void testDeleteEventQuery_UndeletableEventType_Success() {
		EventType undeletableEventType = ArgosTestUtil.createEventType(false, true);
		EventQuery testEventQuery1 = ArgosTestUtil.createEventQuery(undeletableEventType, true);
		EventQuery testEventQuery2 = ArgosTestUtil.createEventQuery(undeletableEventType, true);

		assertEquals(2, getEventQueriesCount(undeletableEventType.getId()));

		RestRequest request = RestRequestFactoryImpl.getInstance()
				.createDeleteRequest(ARGOS_REST_HOST, getDeleteEventQueryUri(testEventQuery1.getId()));

		assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());
		assertEquals(1, getEventQueriesCount(undeletableEventType.getId()));

		EventQuery eventQuery = PersistenceAdapterImpl.getInstance().getEventQuery(testEventQuery2.getId());
		assertEquals(testEventQuery2.getDescription(), eventQuery.getDescription());
		assertEquals(testEventQuery2.getQuery(), eventQuery.getQuery());
		assertEquals(testEventQuery2.getTypeId(), eventQuery.getTypeId());
		assertEquals(testEventQuery2.getUuid(), eventQuery.getUuid());
	}

	@Test
	public void testEditEventQuery() throws Exception {
		EventQuery testQuery = ArgosTestUtil.createEventQuery(testEventType, true);

		String newDescription = testQuery.getDescription() + "_new";
		String newQuery = testQuery.getQuery() + "_new";

		int eventQueriesCount = getEventQueriesCount();
		WebSocket webSocket = WebSocket.buildWebSocket();

		RestRequest request = RestRequestFactoryImpl.getInstance().createPutRequest(ARGOS_REST_HOST, getEditEventQueryUri(testQuery.getId()));

		JsonObject newQueryJson = new JsonObject();
		newQueryJson.addProperty("Description", newDescription);
		newQueryJson.addProperty("Query", newQuery);

		request.setContent(serializer.toJson(newQueryJson));
		assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());

		assertEquals(eventQueriesCount, getEventQueriesCount());

		EventQuery updatedQuery = PersistenceAdapterImpl.getInstance().getEventQuery(testQuery.getId());
		assertEquals(testQuery.getTypeId(), updatedQuery.getTypeId());
		assertEquals(newDescription, updatedQuery.getDescription());
		assertEquals(newQuery, updatedQuery.getQuery());

		List<String> webSocketMessages = webSocket.awaitMessages(1, 1000);
		ArgosTestUtil.assertWebSocketMessage(webSocketMessages.get(0), PersistenceArtifactUpdateType.MODIFY, "EventQuery");

		JsonObject notification = jsonParser.parse(webSocketMessages.get(0)).getAsJsonArray().get(0).getAsJsonObject();
		assertEquals(testQuery.getId(), notification.get("ArtifactId").getAsLong());
	}

	@Test
	public void testEditEventQuery_InvalidEventQueryId_BadRequest() {
		EventQuery testQuery = ArgosTestUtil.createEventQuery(testEventType, true);

		String newDescription = testQuery.getDescription() + "_new";
		String newQuery = testQuery.getQuery() + "_new";

		int eventQueriesCount = getEventQueriesCount();

		RestRequest request = RestRequestFactoryImpl.getInstance().createPutRequest(ARGOS_REST_HOST, getEditEventQueryUri(0));

		JsonObject newQueryJson = new JsonObject();
		newQueryJson.addProperty("Description", newDescription);
		newQueryJson.addProperty("Query", newQuery);

		request.setContent(serializer.toJson(newQueryJson));
		assertEquals(HttpStatusCodes.BAD_REQUEST, request.getResponseCode());

		assertEquals(eventQueriesCount, getEventQueriesCount());

		EventQuery updatedQuery = PersistenceAdapterImpl.getInstance().getEventQuery(testQuery.getId());
		assertEquals(testQuery.getTypeId(), updatedQuery.getTypeId());
		assertEquals(testQuery.getDescription(), updatedQuery.getDescription());
		assertEquals(testQuery.getQuery(), updatedQuery.getQuery());
	}

	@Test
	public void testEditEventQuery_InvalidJson_BadRequest() {
		EventQuery testQuery = ArgosTestUtil.createEventQuery(testEventType, true);

		String newQuery = testQuery.getQuery() + "_new";

		int eventQueriesCount = getEventQueriesCount();

		RestRequest request = RestRequestFactoryImpl.getInstance()
				.createPutRequest(ARGOS_REST_HOST, getEditEventQueryUri(testQuery.getId()));

		JsonObject newQueryJson = new JsonObject();
		// remove the eventTypeId attribute to make the json invalid
		newQueryJson.addProperty("Query", newQuery);

		request.setContent(serializer.toJson(newQueryJson));
		assertEquals(HttpStatusCodes.BAD_REQUEST, request.getResponseCode());

		assertEquals(eventQueriesCount, getEventQueriesCount());

		EventQuery updatedQuery = PersistenceAdapterImpl.getInstance().getEventQuery(testQuery.getId());
		assertEquals(testQuery.getTypeId(), updatedQuery.getTypeId());
		assertEquals(testQuery.getDescription(), updatedQuery.getDescription());
		assertEquals(testQuery.getQuery(), updatedQuery.getQuery());
	}

	private JsonObject createEventQueryJson(EventQuery query, boolean valid) {
		JsonObject eventQuery = new JsonObject();

		if (valid) {
			eventQuery.addProperty("EventTypeId", query.getTypeId());
		} else {
			eventQuery.addProperty("EventTypeId", 0);
		}

		eventQuery.addProperty("Description", query.getDescription());
		eventQuery.addProperty("Query", query.getQuery());

		return eventQuery;
	}

	private int getEventQueriesCount() {
		return getEventQueriesCount(testEventType.getId());
	}

	private int getEventQueriesCount(long eventTypeId) {
		return PersistenceAdapterImpl.getInstance().getEventQueries(eventTypeId).size();
	}

	private String getCreateEventQueryUri() {
		return EventQueryEndpoint.getCreateEventQueryBaseUri();
	}

	private String getDeleteEventQueryUri(Object eventQueryId) {
		return EventQueryEndpoint.getDeleteEventQueryBaseUri()
				.replaceAll(EventQueryEndpoint.getEventQueryIdParameter(true), eventQueryId.toString());
	}

	private String getEditEventQueryUri(Object eventQueryId) {
		return EventQueryEndpoint.getEditEventQueryBaseUri()
				.replaceAll(EventQueryEndpoint.getEventQueryIdParameter(true), eventQueryId.toString());
	}
}
