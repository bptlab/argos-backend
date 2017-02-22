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

    private String getProductFamiliesUri() {
        return ProductFamilyEndpoint.getProductFamiliesBaseUri();
    }
}
