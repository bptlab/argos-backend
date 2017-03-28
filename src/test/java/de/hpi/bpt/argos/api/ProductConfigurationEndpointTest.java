package de.hpi.bpt.argos.api;


import com.google.gson.JsonObject;
import de.hpi.bpt.argos.api.productConfiguration.ProductConfigurationEndPoint;
import de.hpi.bpt.argos.api.response.ResponseFactory;
import de.hpi.bpt.argos.core.ArgosTestParent;
import de.hpi.bpt.argos.core.ArgosTestUtil;
import de.hpi.bpt.argos.persistence.model.product.Product;
import de.hpi.bpt.argos.persistence.model.product.ProductConfiguration;
import de.hpi.bpt.argos.persistence.model.product.ProductFamily;
import de.hpi.bpt.argos.persistence.model.product.ProductState;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ProductConfigurationEndpointTest extends CustomerEndpointParentClass {

	protected static ProductConfiguration testProductConfiguration;

	@BeforeClass
	public static void createTestProduct() {
		ProductFamily productFamily = ArgosTestUtil.createProductFamily();
		Product product = ArgosTestUtil.createProduct(productFamily);
		testProductConfiguration = ArgosTestUtil.createProductConfiguration(product);
	}

	@Test
	public void testGetProductConfiguration() {
		request = requestFactory.createGetRequest(TEST_HOST,
				getProductConfiguration(testProductConfiguration.getId()),
				TEST_ACCEPT_TYPE_JSON);

		assertEquals(ResponseFactory.HTTP_SUCCESS_CODE, request.getResponseCode());

		JsonObject jsonProductConfiguration = jsonParser.parse(request.getResponse()).getAsJsonObject();
		assertEquals(testProductConfiguration.getId(), jsonProductConfiguration.get("id").getAsLong());
	}

	@Test
	public void testGetProductConfiguration_InvalidId_NotFound() {
		request = requestFactory.createGetRequest(TEST_HOST,
				getProductConfiguration(testProductConfiguration.getId() - 1));

		assertEquals(ResponseFactory.HTTP_NOT_FOUND_CODE, request.getResponseCode());
	}

	@Test
	public void testUpdateStatusQuery() {
		ProductState newState = ProductState.RUNNING;

		request = requestFactory.createPostRequest(TEST_HOST,
				getUpdateStatusQueryUri(testProductConfiguration.getId(), newState),
				TEST_CONTENT_TYPE,
				TEST_ACCEPT_TYPE_PLAIN);

		String newEventQuery = "new event query";

		JsonObject jsonQuery = new JsonObject();
		jsonQuery.addProperty("eventQuery", newEventQuery);

		request.setContent(serializer.toJson(jsonQuery));
		assertEquals(ResponseFactory.HTTP_SUCCESS_CODE, request.getResponseCode());

		ProductConfiguration updatedConfiguration = ArgosTestParent.argos.getPersistenceEntityManager()
				.getProductConfiguration(testProductConfiguration.getId());

		assertEquals(newEventQuery, updatedConfiguration.getStatusUpdateQuery(newState).getQueryString());
	}

	@Test
	public void testUpdateStatusQuery_InvalidQuery_Error() {
		ProductState newState = ProductState.RUNNING;

		request = requestFactory.createPostRequest(TEST_HOST,
				getUpdateStatusQueryUri(testProductConfiguration.getId(), newState),
				TEST_CONTENT_TYPE,
				TEST_ACCEPT_TYPE_PLAIN);

		String newEventQuery = "";

		JsonObject jsonQuery = new JsonObject();
		jsonQuery.addProperty("eventQuery", newEventQuery);

		request.setContent(serializer.toJson(jsonQuery));
		assertEquals(ResponseFactory.HTTP_ERROR_CODE, request.getResponseCode());
	}

	@Test
	public void testUpdateStatusQuery_InvalidId_NotFound() {
		ProductState newState = ProductState.RUNNING;

		request = requestFactory.createPostRequest(TEST_HOST,
				getUpdateStatusQueryUri(testProductConfiguration.getId() - 1, newState),
				TEST_CONTENT_TYPE,
				TEST_ACCEPT_TYPE_PLAIN);

		String newEventQuery = "new event query";

		JsonObject jsonQuery = new JsonObject();
		jsonQuery.addProperty("eventQuery", newEventQuery);

		request.setContent(serializer.toJson(jsonQuery));
		assertEquals(ResponseFactory.HTTP_NOT_FOUND_CODE, request.getResponseCode());
	}

	@Test
	public void testUpdateStatusQuery_InvalidNewState_Error() {
		ProductState newState = ProductState.UNDEFINED;

		request = requestFactory.createPostRequest(TEST_HOST,
				getUpdateStatusQueryUri(testProductConfiguration.getId(), newState),
				TEST_CONTENT_TYPE,
				TEST_ACCEPT_TYPE_PLAIN);

		String newEventQuery = "new event query";

		JsonObject jsonQuery = new JsonObject();
		jsonQuery.addProperty("eventQuery", newEventQuery);

		request.setContent(serializer.toJson(jsonQuery));
		assertEquals(ResponseFactory.HTTP_ERROR_CODE, request.getResponseCode());
	}

	private String getProductConfiguration(Object productConfigurationId) {
		return ProductConfigurationEndPoint.getProductConfigurationBaseUri()
				.replaceAll(ProductConfigurationEndPoint.getProductConfigurationIdParameter(true), productConfigurationId.toString());
	}

	private String getUpdateStatusQueryUri(Object productConfigurationId, Object newState) {
		return ProductConfigurationEndPoint.getUpdateStatusQueryBaseUri()
				.replaceAll(ProductConfigurationEndPoint.getProductConfigurationIdParameter(true), productConfigurationId.toString())
				.replaceAll(ProductConfigurationEndPoint.getNewProductStatusParameter(true), newState.toString());
	}
}
