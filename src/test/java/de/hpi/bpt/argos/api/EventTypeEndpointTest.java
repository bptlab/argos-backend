package de.hpi.bpt.argos.api;


import de.hpi.bpt.argos.api.eventTypes.EventTypeEndpoint;
import de.hpi.bpt.argos.common.RestRequest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EventTypeEndpointTest extends EndpointParentClass {

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


	private String getEventTypes() {
		return EventTypeEndpoint.getEventTypesBaseUri();
	}

	private String getEventType(Object eventTypeId) {
		return EventTypeEndpoint.getEventTypeBaseUri().replaceAll(EventTypeEndpoint.getEventTypeIdParameter(true), eventTypeId.toString());
	}
}
