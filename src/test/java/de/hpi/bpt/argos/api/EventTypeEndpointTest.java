package de.hpi.bpt.argos.api;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.hpi.bpt.argos.api.eventTypes.EventTypeEndpoint;
import de.hpi.bpt.argos.common.RestRequest;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class EventTypeEndpointTest extends EndpointParentClass {
	protected static final Gson serializer = new Gson();
	protected static final JsonParser jsonParser = new JsonParser();

	protected RestRequest request;

	@Test
	public void testGetEventTypes() {
		request = requestFactory.createRequest(TEST_HOST, getEventTypes(), TEST_REQUEST_METHOD, TEST_CONTENT_TYPE,
				TEST_ACCEPT_TYPE);

		assertEquals(HTTP_SUCCESSFUL_RESPONSE_CODE, request.getResponseCode());
	}

	@Test
	public void testGetEventType() {
		request = requestFactory.createRequest(TEST_HOST, getEventType(-42), TEST_REQUEST_METHOD, TEST_CONTENT_TYPE,
				TEST_ACCEPT_TYPE);

		assertEquals(HTTP_INVALID_REQUEST_RESPONSE_CODE, request.getResponseCode());

		request = requestFactory.createRequest(TEST_HOST, getEventType("invalid_parameter"), TEST_REQUEST_METHOD, TEST_CONTENT_TYPE,
				TEST_ACCEPT_TYPE);

		assertEquals(HTTP_INVALID_REQUEST_RESPONSE_CODE, request.getResponseCode());

		request = requestFactory.createRequest(TEST_HOST, getEventType(42), TEST_REQUEST_METHOD, TEST_CONTENT_TYPE,
				TEST_ACCEPT_TYPE);

		assertEquals(HTTP_NOT_FOUND_CODE, request.getResponseCode());
	}

	@Test
	public void testCreateEventType() {
		request = requestFactory.createPostRequest(TEST_HOST, createEventType(), TEST_CONTENT_TYPE, TEST_ACCEPT_TYPE);

		String eventTypeName = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss").format(new Date());

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

		assertEquals(HTTP_SUCCESSFUL_RESPONSE_CODE, request.getResponseCode());


		request = requestFactory.createRequest(TEST_HOST, getEventTypes(), TEST_REQUEST_METHOD, TEST_CONTENT_TYPE,
				TEST_CONTENT_TYPE);

		assertEquals(HTTP_SUCCESSFUL_RESPONSE_CODE, request.getResponseCode());

		JsonArray jsonEventTypes = jsonParser.parse(request.getResponse()).getAsJsonArray();
		long eventTypeId = 0;

		for (JsonElement jsonElement : jsonEventTypes) {
			JsonObject jsonEventType = jsonElement.getAsJsonObject();

			JsonElement nameAttribute = jsonEventType.get("name");
			JsonElement idAttribute = jsonEventType.get("id");

			if (nameAttribute.getAsString().equals(eventTypeName)) {
				eventTypeId = idAttribute.getAsLong();
				break;
			}
		}

		request = requestFactory.createDeleteRequest(TEST_HOST, deleteEventType(eventTypeId), TEST_CONTENT_TYPE, TEST_ACCEPT_TYPE);

		assertEquals(HTTP_SUCCESSFUL_RESPONSE_CODE, request.getResponseCode());
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
