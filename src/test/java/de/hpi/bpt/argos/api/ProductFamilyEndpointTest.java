package de.hpi.bpt.argos.api;

import de.hpi.bpt.argos.api.productFamily.ProductFamilyEndpoint;
import de.hpi.bpt.argos.common.RestRequest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ProductFamilyEndpointTest extends EndpointParentClass {

    protected RestRequest request;

    @Test
    public void testGetProductFamilies() {
        request = requestFactory.createRequest(TEST_HOST, getProductFamiliesUri(), TEST_REQUEST_METHOD, TEST_CONTENT_TYPE, TEST_ACCEPT_TYPE);

        assertEquals(true, request.isSuccessful());
    }

    @Test
    public void testGetEventsForProduct() {
        request = requestFactory.createRequest(TEST_HOST, getEventsForProductFamilyUri(42, 1337, 0, 10), TEST_REQUEST_METHOD, TEST_CONTENT_TYPE,
                TEST_ACCEPT_TYPE);
        assertEquals(true, request.isSuccessful());

        // failure: product id < 0
        request = requestFactory.createRequest(TEST_HOST, getEventsForProductFamilyUri(-42, 1337, 0, 10), TEST_REQUEST_METHOD, TEST_CONTENT_TYPE,
                TEST_ACCEPT_TYPE);
        assertEquals(HTTP_INVALID_REQUEST_RESPONSE_CODE, request.getResponseCode());

        // failure: product id not an integer
        request = requestFactory.createRequest(TEST_HOST, getEventsForProductFamilyUri("hello_server", 1337, 0, 10), TEST_REQUEST_METHOD,
                TEST_CONTENT_TYPE,
                TEST_ACCEPT_TYPE);
        assertEquals(HTTP_INVALID_REQUEST_RESPONSE_CODE, request.getResponseCode());
    }

    private String getProductFamiliesUri() {
        return ProductFamilyEndpoint.getProductFamiliesBaseUri();
    }

    private String getEventsForProductFamilyUri(Object productId, Object eventTypeId, Object indexFrom, Object
            indexTo) {
        return String.format("/api/products/%1$s/events/%2$s/%3$s/%4$s", productId, eventTypeId, indexFrom, indexTo);
    }
}
