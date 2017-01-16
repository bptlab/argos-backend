package de.hpi.bpt.argos.api;

import de.hpi.bpt.argos.common.RestRequest;
import de.hpi.bpt.argos.common.RestRequestFactory;
import de.hpi.bpt.argos.common.RestRequestFactoryImpl;
import de.hpi.bpt.argos.core.Argos;
import de.hpi.bpt.argos.core.ArgosImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProductFamilyEndpointTest {
    private static final int TEST_PORT = 9001;
    private static final int TEST_NUMBER_OF_THREADS = 8;
    private static final String TEST_HOST = "http://localhost:" + TEST_PORT;
    private static final String TEST_REQUEST_METHOD = "GET";
    private static final String TEST_CONTENT_TYPE = "application/json";
    private static final String TEST_ACCEPT_TYPE = "text/plain";
    private static final int INVALID_REQUEST_RESPONSE_CODE = 404;

    private Argos argos;
    private RestRequestFactory requestFactory;
    private RestRequest request;

    @BeforeEach
    void setUp() {
        argos = new ArgosImpl();
        argos.run(TEST_PORT, TEST_NUMBER_OF_THREADS);
        requestFactory = new RestRequestFactoryImpl();
    }

    @AfterEach
    void tearDown() {
        argos.shutdown();
    }

    @Test
    void getProductFamilies() {
        request = requestFactory.createRequest(TEST_HOST, getProductFamiliesUri(), TEST_REQUEST_METHOD, TEST_CONTENT_TYPE, TEST_ACCEPT_TYPE);

        assertEquals(request.isSuccessful(), true);
    }

    @Test
    void getProductFamilyOverview() {
        request = requestFactory.createRequest(TEST_HOST, getProductFamilyOverviewUri(42), TEST_REQUEST_METHOD, TEST_CONTENT_TYPE, TEST_ACCEPT_TYPE);
        assertEquals(true, request.isSuccessful());

        // failure: product id < 0
        request = requestFactory.createRequest(TEST_HOST, getProductFamilyOverviewUri(-42), TEST_REQUEST_METHOD, TEST_CONTENT_TYPE,
                TEST_ACCEPT_TYPE);
        assertEquals(INVALID_REQUEST_RESPONSE_CODE, request.getResponseCode());

        // failure: product id not an integer
        request = requestFactory.createRequest(TEST_HOST, getProductFamilyOverviewUri("hello_server"), TEST_REQUEST_METHOD, TEST_CONTENT_TYPE,
                TEST_ACCEPT_TYPE);
        assertEquals(INVALID_REQUEST_RESPONSE_CODE, request.getResponseCode());
    }

    @Test
    void getEventsForProductFamily() {
        request = requestFactory.createRequest(TEST_HOST, getEventsForProductFamilyUri(42, 1337, 0, 10), TEST_REQUEST_METHOD, TEST_CONTENT_TYPE,
                TEST_ACCEPT_TYPE);
        assertEquals(true, request.isSuccessful());

        // failure: product id < 0
        request = requestFactory.createRequest(TEST_HOST, getEventsForProductFamilyUri(-42, 1337, 0, 10), TEST_REQUEST_METHOD, TEST_CONTENT_TYPE,
                TEST_ACCEPT_TYPE);
        assertEquals(INVALID_REQUEST_RESPONSE_CODE, request.getResponseCode());

        // failure: product id not an integer
        request = requestFactory.createRequest(TEST_HOST, getEventsForProductFamilyUri("hello_server", 1337, 0, 10), TEST_REQUEST_METHOD,
                TEST_CONTENT_TYPE,
                TEST_ACCEPT_TYPE);
        assertEquals(INVALID_REQUEST_RESPONSE_CODE, request.getResponseCode());
    }

    private String getProductFamiliesUri() {
        return "/api/product";
    }

    private String getProductFamilyOverviewUri(Object productId) {
        return String.format("/api/product/%1$s", productId);
    }

    private String getEventsForProductFamilyUri(Object productId, Object eventTypeId, Object indexFrom, Object indexTo) {
        return String.format("/api/product/%1$s/eventtype/%2$s/%3$s/%4$s", productId, eventTypeId, indexFrom, indexTo);
    }
}
