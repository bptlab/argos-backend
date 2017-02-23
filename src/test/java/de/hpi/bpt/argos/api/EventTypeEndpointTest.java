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

public class EventTypeEndpointTest extends EndpointParentClass {

	protected static EventType testEventType;

	@BeforeClass
	public static void createTestEventType() {
		testEventType = ArgosTestUtil.createEventType();
	}

	@Test
	public void testGetEventTypes() {
		request = requestFactory.createRequest(TEST_HOST, getEventTypes(), TEST_REQUEST_METHOD, TEST_CONTENT_TYPE, TEST_ACCEPT_TYPE_PLAIN);
		assertEquals(ResponseFactory.getHttpSuccessCode(), request.getResponseCode());

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
		request = requestFactory.createGetRequest(TEST_HOST, getEventType(testEventType.getId()), TEST_ACCEPT_TYPE_JSON);
		assertEquals(ResponseFactory.getHttpSuccessCode(), request.getResponseCode());

		JsonObject jsonEventType = jsonParser.parse(request.getResponse()).getAsJsonObject();
		assertEquals(testEventType.getId(), jsonEventType.get("id").getAsLong());
	}

	@Test
	public void testGetEventType_InvalidId_NotFound() {
		request = requestFactory.createGetRequest(TEST_HOST, getEventType(testEventType.getId() - 1), TEST_ACCEPT_TYPE_JSON);
		assertEquals(ResponseFactory.getHttpNotFoundCode(), request.getResponseCode());
	}

	@Test
	public void testCreateEventType() {
		request = requestFactory.createPostRequest(TEST_HOST, createEventType(), TEST_CONTENT_TYPE, TEST_ACCEPT_TYPE_PLAIN);

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
		assertEquals(ResponseFactory.getHttpSuccessCode(), request.getResponseCode());

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
		request = requestFactory.createPostRequest(TEST_HOST, createEventType(), TEST_CONTENT_TYPE, TEST_ACCEPT_TYPE_PLAIN);

		String eventTypeName = "EventType_" + ArgosTestUtil.getCurrentTimestamp();

		JsonObject jsonBody = new JsonObject();
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
		assertEquals(ResponseFactory.getHttpErrorCode(), request.getResponseCode());
	}

	@Test
	public void testCreateEventType_NoEventType_Error() {
		request = requestFactory.createPostRequest(TEST_HOST, createEventType(), TEST_CONTENT_TYPE, TEST_ACCEPT_TYPE_PLAIN);

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

		//jsonBody.add("eventType", testType);

		request.setContent(serializer.toJson(jsonBody));
		assertEquals(ResponseFactory.getHttpErrorCode(), request.getResponseCode());
	}

	@Test
	public void testCreateEventType_InvalidEventType_Error() {
		request = requestFactory.createPostRequest(TEST_HOST, createEventType(), TEST_CONTENT_TYPE, TEST_ACCEPT_TYPE_PLAIN);

		String eventTypeName = "EventType_" + ArgosTestUtil.getCurrentTimestamp();

		JsonObject jsonBody = new JsonObject();
		jsonBody.addProperty("eventQuery", "SELECT * FROM " + eventTypeName);

		JsonObject testType = new JsonObject();
		testType.addProperty("name", eventTypeName);
		testType.addProperty("timestamp", "timestamp");

		JsonObject attributes = new JsonObject();
		//attributes.addProperty("productId", "INTEGER");
		attributes.addProperty("productFamilyId", "STRING");

		testType.add("attributes", attributes);

		jsonBody.add("eventType", testType);

		request.setContent(serializer.toJson(jsonBody));
		assertEquals(ResponseFactory.getHttpErrorCode(), request.getResponseCode());
	}

	@Test
	public void testCreateEventType_EventTypeNameIsUse_Error() {
		request = requestFactory.createPostRequest(TEST_HOST, createEventType(), TEST_CONTENT_TYPE, TEST_ACCEPT_TYPE_PLAIN);

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
		assertEquals(ResponseFactory.getHttpErrorCode(), request.getResponseCode());
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
