package de.hpi.bpt.argos.api;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.hpi.bpt.argos.api.product.ProductEndpoint;
import de.hpi.bpt.argos.api.productConfiguration.ProductConfigurationEndPoint;
import de.hpi.bpt.argos.api.response.ResponseFactory;
import de.hpi.bpt.argos.core.ArgosTestParent;
import de.hpi.bpt.argos.core.ArgosTestUtil;
import de.hpi.bpt.argos.persistence.model.event.Event;
import de.hpi.bpt.argos.persistence.model.event.type.EventType;
import de.hpi.bpt.argos.persistence.model.product.Product;
import de.hpi.bpt.argos.persistence.model.product.ProductConfiguration;
import de.hpi.bpt.argos.persistence.model.product.ProductFamily;
import de.hpi.bpt.argos.persistence.model.product.ProductState;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ProductConfigurationEndpointTest extends CustomerEndpointParentClass {

	protected static ProductConfiguration testProductConfiguration;
	protected static EventType testEventType;
	protected static Event testEvent;

	@BeforeClass
	public static void createTestProduct() {
		ProductFamily productFamily = ArgosTestUtil.createProductFamily();
		Product product = ArgosTestUtil.createProduct(productFamily);
		testProductConfiguration = ArgosTestUtil.createProductConfiguration(product);

		testEventType = ArgosTestUtil.createEventType();
		testEvent = ArgosTestUtil.createEvent(testEventType, testProductConfiguration);
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
	public void testGetEventTypesForProductConfiguration() {
		request = requestFactory.createGetRequest(TEST_HOST,
				getEventTypesForProductConfiguration(testProductConfiguration.getId()),
				TEST_ACCEPT_TYPE_PLAIN);
		assertEquals(ResponseFactory.HTTP_SUCCESS_CODE, request.getResponseCode());

		JsonArray jsonEventTypes = jsonParser.parse(request.getResponse()).getAsJsonArray();

		assertEquals(true, assertEventTypeExists(jsonEventTypes, testEventType.getId(), 1));
	}

	@Test
	public void testGetEventTypesForProductConfiguration_InvalidId_NotFound() {
		request = requestFactory.createGetRequest(TEST_HOST,
				getEventTypesForProductConfiguration(testProductConfiguration.getId() - 1),
				TEST_ACCEPT_TYPE_PLAIN);
		assertEquals(ResponseFactory.HTTP_NOT_FOUND_CODE, request.getResponseCode());
	}

	@Test
	public void testGetEventsForProductConfiguration() {
		request = requestFactory.createGetRequest(TEST_HOST,
				getEventsForProductConfiguration(testProductConfiguration.getId(), testEventType.getId(), 0, 1),
				TEST_ACCEPT_TYPE_JSON);
		assertEquals(ResponseFactory.HTTP_SUCCESS_CODE, request.getResponseCode());

		JsonArray jsonEvents = jsonParser.parse(request.getResponse()).getAsJsonArray();
		assertEquals(1, jsonEvents.size());

		JsonObject jsonEvent = jsonEvents.get(0).getAsJsonObject();
		assertEquals(testEvent.getId(), jsonEvent.get("id").getAsLong());
	}

	@Test
	public void testGetEventsForProductConfiguration_InvalidEventTypeId_Success() {
		request = requestFactory.createGetRequest(TEST_HOST,
				getEventsForProductConfiguration(testProductConfiguration.getId(), testEventType.getId() - 1, 0, 1),
				TEST_ACCEPT_TYPE_JSON);
		assertEquals(ResponseFactory.HTTP_SUCCESS_CODE, request.getResponseCode());

		JsonArray jsonEvents = jsonParser.parse(request.getResponse()).getAsJsonArray();
		assertEquals(0, jsonEvents.size());
	}

	@Test
	public void testGetEventsForProductConfiguration_InvalidProductId_Success() {
		request = requestFactory.createGetRequest(TEST_HOST,
				getEventsForProductConfiguration(testProductConfiguration.getId() - 1, testEventType.getId(), 0, 1),
				TEST_ACCEPT_TYPE_JSON);
		assertEquals(ResponseFactory.HTTP_SUCCESS_CODE, request.getResponseCode());

		JsonArray jsonEvents = jsonParser.parse(request.getResponse()).getAsJsonArray();
		assertEquals(0, jsonEvents.size());
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

	private String getEventTypesForProductConfiguration(Object productConfigurationId) {
		return ProductConfigurationEndPoint.getEventTypesForProductConfigurationBaseUri()
				.replaceAll(ProductConfigurationEndPoint.getProductConfigurationIdParameter(true), productConfigurationId.toString());
	}

	private String getEventsForProductConfiguration(Object productConfigurationId, Object eventTypeId, Object indexFrom, Object indexTo) {
		return ProductConfigurationEndPoint.getEventsForProductConfigurationBaseUri()
				.replaceAll(ProductConfigurationEndPoint.getProductConfigurationIdParameter(true), productConfigurationId.toString())
				.replaceAll(ProductEndpoint.getEventTypeIdParameter(true), eventTypeId.toString())
				.replaceAll(ProductEndpoint.getIndexFromParameter(true), indexFrom.toString())
				.replaceAll(ProductEndpoint.getIndexToParameter(true), indexTo.toString());
	}

	private String getUpdateStatusQueryUri(Object productConfigurationId, Object newState) {
		return ProductConfigurationEndPoint.getUpdateStatusQueryBaseUri()
				.replaceAll(ProductConfigurationEndPoint.getProductConfigurationIdParameter(true), productConfigurationId.toString())
				.replaceAll(ProductConfigurationEndPoint.getNewProductStatusParameter(true), newState.toString());
	}
}
