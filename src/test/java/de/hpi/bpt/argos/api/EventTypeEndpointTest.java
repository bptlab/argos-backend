package de.hpi.bpt.argos.api;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.hpi.bpt.argos.api.eventTypes.EventTypeEndpoint;
import de.hpi.bpt.argos.common.RestRequest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EventTypeEndpointTest extends EndpointParentClass {
	protected static final Gson serializer = new Gson();

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
		// TODO: delete test_type event type

		request = requestFactory.createPostRequest(TEST_HOST, createEventType(), TEST_CONTENT_TYPE, TEST_ACCEPT_TYPE);

		JsonObject jsonBody = new JsonObject();
		jsonBody.addProperty("subscriptionQuery", "SELECT * FROM TEST_TYPE");

		JsonObject testType = new JsonObject();
		testType.addProperty("name", "TEST_TYPE");
		testType.addProperty("timestamp", "timestamp");

		JsonObject attributes = new JsonObject();
		attributes.addProperty("productId", "INTEGER");
		attributes.addProperty("productFamilyId", "STRING");

		testType.add("attributes", attributes);

		jsonBody.add("eventType", testType);

		request.setContent(serializer.toJson(jsonBody));

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
}
