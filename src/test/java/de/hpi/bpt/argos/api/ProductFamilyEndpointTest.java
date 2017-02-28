package de.hpi.bpt.argos.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.hpi.bpt.argos.api.productFamily.ProductFamilyEndpoint;
import de.hpi.bpt.argos.api.response.ResponseFactory;
import de.hpi.bpt.argos.core.ArgosTestUtil;
import de.hpi.bpt.argos.persistence.model.product.ProductFamily;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ProductFamilyEndpointTest extends CustomerEndpointParentClass {

    protected static ProductFamily testProductFamily;

    @BeforeClass
	public static void createTestProductFamily() {
    	testProductFamily = ArgosTestUtil.createProductFamily();
	}

    @Test
    public void testGetProductFamilies() {
    	request = requestFactory.createGetRequest(TEST_HOST,
				getProductFamiliesUri(),
				TEST_ACCEPT_TYPE_JSON);
    	assertEquals(ResponseFactory.HTTP_SUCCESS_CODE, request.getResponseCode());

		JsonArray jsonProductFamilies = jsonParser.parse(request.getResponse()).getAsJsonArray();
		assertEquals(1, jsonProductFamilies.size());

		assertEquals(testProductFamily.getId(), jsonProductFamilies.get(0).getAsJsonObject().get("id").getAsLong());
    }

    @Test
	public void testGetProductFamily() {
		request = requestFactory.createGetRequest(TEST_HOST,
				getProductFamilyUri(testProductFamily.getId()),
				TEST_ACCEPT_TYPE_JSON);
		assertEquals(ResponseFactory.HTTP_SUCCESS_CODE, request.getResponseCode());

		JsonObject jsonProductFamily = jsonParser.parse(request.getResponse()).getAsJsonObject();
		assertEquals(testProductFamily.getId(), jsonProductFamily.get("id").getAsLong());
	}

	@Test
	public void testGetProductFamily_InvalidId_NotFound() {
		request = requestFactory.createGetRequest(TEST_HOST,
				getProductFamilyUri(testProductFamily.getId() - 1),
				TEST_ACCEPT_TYPE_JSON);
		assertEquals(ResponseFactory.HTTP_NOT_FOUND_CODE, request.getResponseCode());
	}

    private String getProductFamiliesUri() {
        return ProductFamilyEndpoint.getProductFamiliesBaseUri();
    }

    private String getProductFamilyUri(Object productFamilyId) {
    	return ProductFamilyEndpoint.getProductFamilyBaseUri()
				.replaceAll(ProductFamilyEndpoint.getProductFamilyIdParameter(true), productFamilyId.toString());
	}
}
