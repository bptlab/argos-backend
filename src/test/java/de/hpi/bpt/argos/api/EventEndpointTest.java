package de.hpi.bpt.argos.api;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class EventEndpointTest extends EndpointParentClass {
    @Test
    public void testGetSingleEvent() {
        request = requestFactory.createRequest(TEST_HOST, getEvent(42), TEST_REQUEST_METHOD, TEST_CONTENT_TYPE,
                TEST_ACCEPT_TYPE);
        assertEquals(HTTP_NOT_FOUND_CODE, request.getResponseCode());

        // failure: event id < 0
        request = requestFactory.createRequest(TEST_HOST, getEvent("-42"), TEST_REQUEST_METHOD, TEST_CONTENT_TYPE,
                TEST_ACCEPT_TYPE);
        assertEquals(INVALID_REQUEST_RESPONSE_CODE, request.getResponseCode());

        // failure: event id not an integer
        request = requestFactory.createRequest(TEST_HOST, getEvent("hello_server"), TEST_REQUEST_METHOD, TEST_CONTENT_TYPE,
                TEST_ACCEPT_TYPE);
        assertEquals(INVALID_REQUEST_RESPONSE_CODE, request.getResponseCode());
}

    private String getEvent(Object eventId) {
        return String.format("/api/events/%1$s", eventId);
    }
}