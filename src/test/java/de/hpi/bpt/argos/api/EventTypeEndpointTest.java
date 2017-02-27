package de.hpi.bpt.argos.api;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.hpi.bpt.argos.api.eventTypes.EventTypeEndpoint;
import de.hpi.bpt.argos.api.response.ResponseFactory;
import de.hpi.bpt.argos.core.ArgosTestParent;
import de.hpi.bpt.argos.core.ArgosTestUtil;
import de.hpi.bpt.argos.persistence.model.event.type.EventType;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class EventTypeEndpointTest extends CustomerEndpointParentClass {

	protected static EventType testEventType;

	@BeforeClass
	public static void createTestEventType() {
		testEventType = ArgosTestUtil.createEventType();
	}

	@Test
	public void testGetEventTypes() {
		request = requestFactory.createRequest(TEST_HOST,
				getEventTypes(),
				TEST_REQUEST_METHOD,
				TEST_CONTENT_TYPE,
				TEST_ACCEPT_TYPE_PLAIN);
		assertEquals(ResponseFactory.HTTP_SUCCESS_CODE, request.getResponseCode());

		JsonArray jsonEventTypes = jsonParser.parse(request.getResponse()).getAsJsonArray();
		boolean testEventTypeFound = false;

		for (JsonElement element : jsonEventTypes) {
			JsonObject jsonEventType = element.getAsJsonObject();

			if (jsonEventType.get("id").getAsLong() == testEventType.getId()) {
				testEventTypeFound = true;
				break;
			}
		}
		assertEquals(true, testEventTypeFound);
	}

	@Test
	public void testGetEventType() {
		request = requestFactory.createGetRequest(TEST_HOST,
				getEventType(testEventType.getId()),
				TEST_ACCEPT_TYPE_JSON);
		assertEquals(ResponseFactory.HTTP_SUCCESS_CODE, request.getResponseCode());

		JsonObject jsonEventType = jsonParser.parse(request.getResponse()).getAsJsonObject();
		assertEquals(testEventType.getId(), jsonEventType.get("id").getAsLong());
	}

	@Test
	public void testGetEventType_InvalidId_NotFound() {
		request = requestFactory.createGetRequest(TEST_HOST,
				getEventType(testEventType.getId() - 1),
				TEST_ACCEPT_TYPE_JSON);
		assertEquals(ResponseFactory.HTTP_NOT_FOUND_CODE, request.getResponseCode());
	}

	@Test
	public void testCreateEventType() {
		request = requestFactory.createPostRequest(TEST_HOST,
				createEventType(),
				TEST_CONTENT_TYPE,
				TEST_ACCEPT_TYPE_PLAIN);

		String eventTypeName = "TestType_" + ArgosTestUtil.getCurrentTimestamp();

		JsonObject jsonBody = new JsonObject();
		jsonBody.addProperty("eventQuery", "SELECT * FROM " + eventTypeName);

		JsonObject testType = new JsonObject();
		testType.addProperty("name", eventTypeName);
		testType.addProperty("timestamp", "timestamp");

		JsonObject attributes = new JsonObject();
		attributes.addProperty("productId", "INTEGER");
		attributes.addProperty("productFamilyId", "STRING");

		testType.add("attributes", attributes);

		jsonBody.add("eventType", testType);

		request.setContent(serializer.toJson(jsonBody));
		assertEquals(ResponseFactory.HTTP_SUCCESS_CODE, request.getResponseCode());

		List<EventType> eventTypes = ArgosTestParent.argos.getPersistenceEntityManager().getEventTypes();

		boolean eventTypeFound = false;

		for (EventType eventType : eventTypes) {
			if (eventType.getName().equals(eventTypeName)) {
				eventTypeFound = true;
				break;
			}
		}

		assertEquals(true, eventTypeFound);
	}

	@Test
	public void testCreateEventType_NoEventQuery_Error() {
		request = requestFactory.createPostRequest(TEST_HOST,
				createEventType(),
				TEST_CONTENT_TYPE, TEST_ACCEPT_TYPE_PLAIN);

		String eventTypeName = "EventType_" + ArgosTestUtil.getCurrentTimestamp();

		JsonObject jsonBody = new JsonObject();
		// do not add the eventQuery property -> body invalid
		//jsonBody.addProperty("eventQuery", "SELECT * FROM " + eventTypeName);

		JsonObject testType = new JsonObject();
		testType.addProperty("name", eventTypeName);
		testType.addProperty("timestamp", "timestamp");

		JsonObject attributes = new JsonObject();
		attributes.addProperty("productId", "INTEGER");
		attributes.addProperty("productFamilyId", "STRING");

		testType.add("attributes", attributes);

		jsonBody.add("eventType", testType);

		request.setContent(serializer.toJson(jsonBody));
		assertEquals(ResponseFactory.HTTP_ERROR_CODE, request.getResponseCode());
	}

	@Test
	public void testCreateEventType_NoEventType_Error() {
		request = requestFactory.createPostRequest(TEST_HOST,
				createEventType(),
				TEST_CONTENT_TYPE,
				TEST_ACCEPT_TYPE_PLAIN);

		String eventTypeName = "EventType_" + ArgosTestUtil.getCurrentTimestamp();

		JsonObject jsonBody = new JsonObject();
		jsonBody.addProperty("eventQuery", "SELECT * FROM " + eventTypeName);

		JsonObject testType = new JsonObject();
		testType.addProperty("name", eventTypeName);
		testType.addProperty("timestamp", "timestamp");

		JsonObject attributes = new JsonObject();
		attributes.addProperty("productId", "INTEGER");
		attributes.addProperty("productFamilyId", "STRING");

		testType.add("attributes", attributes);

		// do not add eventType -> body invalid
		//jsonBody.add("eventType", testType);

		request.setContent(serializer.toJson(jsonBody));
		assertEquals(ResponseFactory.HTTP_ERROR_CODE, request.getResponseCode());
	}

	@Test
	public void testCreateEventType_InvalidEventType_Error() {
		request = requestFactory.createPostRequest(TEST_HOST,
				createEventType(),
				TEST_CONTENT_TYPE,
				TEST_ACCEPT_TYPE_PLAIN);

		String eventTypeName = "EventType_" + ArgosTestUtil.getCurrentTimestamp();

		JsonObject jsonBody = new JsonObject();
		jsonBody.addProperty("eventQuery", "SELECT * FROM " + eventTypeName);

		JsonObject testType = new JsonObject();
		testType.addProperty("name", eventTypeName);
		testType.addProperty("timestamp", "timestamp");

		JsonObject attributes = new JsonObject();

		// do not add productId -> event type is invalid
		//attributes.addProperty("productId", "INTEGER");
		attributes.addProperty("productFamilyId", "STRING");

		testType.add("attributes", attributes);

		jsonBody.add("eventType", testType);

		request.setContent(serializer.toJson(jsonBody));
		assertEquals(ResponseFactory.HTTP_ERROR_CODE, request.getResponseCode());
	}

	@Test
	public void testCreateEventType_EventTypeNameIsUse_Error() {
		request = requestFactory.createPostRequest(TEST_HOST,
				createEventType(),
				TEST_CONTENT_TYPE,
				TEST_ACCEPT_TYPE_PLAIN);

		String eventTypeName = testEventType.getName();

		JsonObject jsonBody = new JsonObject();
		jsonBody.addProperty("eventQuery", "SELECT * FROM " + eventTypeName);

		JsonObject testType = new JsonObject();
		testType.addProperty("name", eventTypeName);
		testType.addProperty("timestamp", "timestamp");

		JsonObject attributes = new JsonObject();
		attributes.addProperty("productId", "INTEGER");
		attributes.addProperty("productFamilyId", "STRING");

		testType.add("attributes", attributes);

		jsonBody.add("eventType", testType);

		request.setContent(serializer.toJson(jsonBody));
		assertEquals(ResponseFactory.HTTP_ERROR_CODE, request.getResponseCode());
	}

	@Test
	public void testDeleteEventType() {
		EventType deletableEventType = ArgosTestUtil.createEventType();

		request = requestFactory.createDeleteRequest(TEST_HOST,
				deleteEventType(deletableEventType.getId()));
		assertEquals(ResponseFactory.HTTP_SUCCESS_CODE, request.getResponseCode());

		List<EventType> eventTypes = ArgosTestParent.argos.getPersistenceEntityManager().getEventTypes();
		boolean eventTypeFound = false;

		for (EventType eventType : eventTypes) {
			if (eventType.getId() == deletableEventType.getId()) {
				eventTypeFound = true;
				break;
			}
		}

		assertEquals(false, eventTypeFound);
	}

	@Test
	public void testDeleteEventType_InvalidId_NotFound() {
		request = requestFactory.createDeleteRequest(TEST_HOST,
				deleteEventType(testEventType.getId() - 1));
		assertEquals(ResponseFactory.HTTP_NOT_FOUND_CODE, request.getResponseCode());
	}

	@Test
	public void testDeleteEventType_InvalidEventType_Forbidden() {
		EventType undeletableEventType = ArgosTestUtil.createEventType();
		undeletableEventType.setDeletable(false);
		ArgosTestParent.argos.getPersistenceEntityManager().updateEntity(undeletableEventType);

		request = requestFactory.createDeleteRequest(TEST_HOST,
				deleteEventType(undeletableEventType.getId()));
		assertEquals(ResponseFactory.HTTP_FORBIDDEN_CODE, request.getResponseCode());
	}

	@Test
	public void testDeleteEventType_BlockedEventType_Error() {
		EventType blockedEventType = ArgosTestUtil.createEventType();
		blockedEventType.setName("EventType_Blocked_" + ArgosTestUtil.getCurrentTimestamp());
		ArgosTestParent.argos.getPersistenceEntityManager().updateEntity(blockedEventType);

		EventType blockingEventType = ArgosTestUtil.createEventType();
		blockingEventType.setName("EventType_Blocking_" + ArgosTestUtil.getCurrentTimestamp());
		blockingEventType.getEventQuery().setQueryString("this query blocks " + blockedEventType.getName());
		ArgosTestParent.argos.getPersistenceEntityManager().updateEntity(blockingEventType);

		request = requestFactory.createDeleteRequest(TEST_HOST,
				deleteEventType(blockedEventType.getId()));
		assertEquals(ResponseFactory.HTTP_ERROR_CODE, request.getResponseCode());

		JsonArray jsonBlockingEventType = jsonParser.parse(request.getResponse()).getAsJsonArray();
		assertEquals(1, jsonBlockingEventType.size());

		assertEquals(blockingEventType.getId(), jsonBlockingEventType.get(0).getAsLong());
	}

	private String getEventTypes() {
		return EventTypeEndpoint.getEventTypesBaseUri();
	}

	private String getEventType(Object eventTypeId) {
		return EventTypeEndpoint.getEventTypeBaseUri().replaceAll(EventTypeEndpoint.getEventTypeIdParameter(true), eventTypeId.toString());
	}

	private String createEventType() {
		return EventTypeEndpoint.getCreateEventTypeBaseUri();
	}

	private String deleteEventType(Object eventTypeId) {
		return EventTypeEndpoint.getDeleteEventTypeBaseUri().replaceAll(
				EventTypeEndpoint.getEventTypeIdParameter(true),
				eventTypeId.toString());
	}
}
