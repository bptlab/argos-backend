package de.hpi.bpt.argos.api;

import com.google.gson.JsonArray;
import de.hpi.bpt.argos.api.productFamily.ProductFamilyEndpoint;
import de.hpi.bpt.argos.api.response.ResponseFactory;
import de.hpi.bpt.argos.core.ArgosTestUtil;
import de.hpi.bpt.argos.persistence.model.product.ProductFamily;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ProductFamilyEndpointTest extends EndpointParentClass {

    protected static ProductFamily testProductFamily;

    @BeforeClass
	public static void createTestProductFamily() {
    	testProductFamily = ArgosTestUtil.createProductFamily();
	}

    @Test
    public void testGetProductFamilies() {
    	request = requestFactory.createGetRequest(TEST_HOST, getProductFamiliesUri(), TEST_ACCEPT_TYPE_JSON);
    	assertEquals(ResponseFactory.getHttpSuccessCode(), request.getResponseCode());

		JsonArray jsonProductFamilies = jsonParser.parse(request.getResponse()).getAsJsonArray();
		assertEquals(1, jsonProductFamilies.size());

		assertEquals(testProductFamily.getId(), jsonProductFamilies.get(0).getAsJsonObject().get("id").getAsLong());
    }

    private String getProductFamiliesUri() {
        return ProductFamilyEndpoint.getProductFamiliesBaseUri();
    }
}
