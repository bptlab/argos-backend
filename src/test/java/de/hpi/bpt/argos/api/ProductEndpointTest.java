package de.hpi.bpt.argos.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.hpi.bpt.argos.api.product.ProductEndpoint;
import de.hpi.bpt.argos.api.response.ResponseFactory;
import de.hpi.bpt.argos.core.ArgosTestParent;
import de.hpi.bpt.argos.core.ArgosTestUtil;
import de.hpi.bpt.argos.persistence.model.event.Event;
import de.hpi.bpt.argos.persistence.model.event.type.EventType;
import de.hpi.bpt.argos.persistence.model.product.Product;
import de.hpi.bpt.argos.persistence.model.product.ProductFamily;
import de.hpi.bpt.argos.persistence.model.product.ProductState;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ProductEndpointTest extends EndpointParentClass {

	protected static Product testProduct;
	protected static EventType testEventType;
	protected static Event testEvent;

	@BeforeClass
	public static void createTestProduct() {
		ProductFamily productFamily = ArgosTestUtil.createProductFamily();
		testProduct = ArgosTestUtil.createProduct(productFamily);

		testEventType = ArgosTestUtil.createEventType();
		testEvent = ArgosTestUtil.createEvent(testEventType, testProduct);
	}

	@Test
	public void testGetProduct() {
		request = requestFactory.createGetRequest(TEST_HOST,
				getProduct(testProduct.getId()),
				TEST_ACCEPT_TYPE_JSON);
		assertEquals(ResponseFactory.HTTP_SUCCESS_CODE, request.getResponseCode());

		JsonObject jsonProduct = jsonParser.parse(request.getResponse()).getAsJsonObject();
		assertEquals(testProduct.getId(), jsonProduct.get("id").getAsLong());
	}

	@Test
	public void testGetProduct_InvalidId_NotFound() {
		request = requestFactory.createGetRequest(TEST_HOST,
				getProduct(testProduct.getId() - 1),
				TEST_ACCEPT_TYPE_JSON);
		assertEquals(ResponseFactory.HTTP_NOT_FOUND_CODE, request.getResponseCode());
	}

	@Test
	public void testGetEventTypesForProduct() {
		request = requestFactory.createGetRequest(TEST_HOST,
				getEventTypesForProduct(testProduct.getId()),
				TEST_ACCEPT_TYPE_PLAIN);
		assertEquals(ResponseFactory.HTTP_SUCCESS_CODE, request.getResponseCode());

		JsonArray jsonEventTypes = jsonParser.parse(request.getResponse()).getAsJsonArray();
		boolean testEventTypeFound = false;

		for (JsonElement element : jsonEventTypes) {
			JsonObject jsonEventType = element.getAsJsonObject();

			if (jsonEventType.get("id").getAsLong() == testEventType.getId()) {
				testEventTypeFound = true;

				assertEquals(1, jsonEventType.get("numberOfEvents").getAsInt());

				break;
			}
		}

		assertEquals(true, testEventTypeFound);
	}

	@Test
	public void testGetEventTypesForProduct_InvalidId_NotFound() {
		request = requestFactory.createGetRequest(TEST_HOST,
				getEventTypesForProduct(testProduct.getId() - 1),
				TEST_ACCEPT_TYPE_PLAIN);
		assertEquals(ResponseFactory.HTTP_NOT_FOUND_CODE, request.getResponseCode());
	}

	@Test
	public void testGetEventsForProduct() {
		request = requestFactory.createGetRequest(TEST_HOST,
				getEventsForProductUri(testProduct.getId(), testEventType.getId(), 0, 1),
				TEST_ACCEPT_TYPE_JSON);
		assertEquals(ResponseFactory.HTTP_SUCCESS_CODE, request.getResponseCode());

		JsonArray jsonEvents = jsonParser.parse(request.getResponse()).getAsJsonArray();
		assertEquals(1, jsonEvents.size());

		JsonObject jsonEvent = jsonEvents.get(0).getAsJsonObject();
		assertEquals(testEvent.getId(), jsonEvent.get("id").getAsLong());
	}

	@Test
	public void testGetEventsForProduct_InvalidEventTypeId_Success() {
		request = requestFactory.createGetRequest(TEST_HOST,
				getEventsForProductUri(testProduct.getId(), testEventType.getId() - 1, 0, 1),
				TEST_ACCEPT_TYPE_JSON);
		assertEquals(ResponseFactory.HTTP_SUCCESS_CODE, request.getResponseCode());

		JsonArray jsonEvents = jsonParser.parse(request.getResponse()).getAsJsonArray();
		assertEquals(0, jsonEvents.size());
	}

	@Test
	public void testGetEventsForProduct_InvalidProductId_Success() {
		request = requestFactory.createGetRequest(TEST_HOST,
				getEventsForProductUri(testProduct.getId() - 1, testEventType.getId(), 0, 1),
				TEST_ACCEPT_TYPE_JSON);
		assertEquals(ResponseFactory.HTTP_SUCCESS_CODE, request.getResponseCode());

		JsonArray jsonEvents = jsonParser.parse(request.getResponse()).getAsJsonArray();
		assertEquals(0, jsonEvents.size());
	}

	@Test
	public void testUpdateStatusQuery() {
		ProductState newState = ProductState.RUNNING;

		request = requestFactory.createPostRequest(TEST_HOST,
				getUpdateStatusQueryUri(testProduct.getId(), newState),
				TEST_CONTENT_TYPE,
				TEST_ACCEPT_TYPE_PLAIN);

		String newEventQuery = "new event query";

		JsonObject jsonQuery = new JsonObject();
		jsonQuery.addProperty("eventQuery", newEventQuery);

		request.setContent(serializer.toJson(jsonQuery));
		assertEquals(ResponseFactory.HTTP_SUCCESS_CODE, request.getResponseCode());

		Product updatedProduct = ArgosTestParent.argos.getPersistenceEntityManager().getProduct(testProduct.getId());
		assertEquals(newEventQuery, updatedProduct.getStatusUpdateQuery(newState).getQueryString());
	}

	@Test
	public void testUpdateStatusQuery_InvalidQuery_Error() {
		ProductState newState = ProductState.RUNNING;

		request = requestFactory.createPostRequest(TEST_HOST,
				getUpdateStatusQueryUri(testProduct.getId(), newState),
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
				getUpdateStatusQueryUri(testProduct.getId() - 1, newState),
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
				getUpdateStatusQueryUri(testProduct.getId(), newState),
				TEST_CONTENT_TYPE,
				TEST_ACCEPT_TYPE_PLAIN);

		String newEventQuery = "new event query";

		JsonObject jsonQuery = new JsonObject();
		jsonQuery.addProperty("eventQuery", newEventQuery);

		request.setContent(serializer.toJson(jsonQuery));
		assertEquals(ResponseFactory.HTTP_ERROR_CODE, request.getResponseCode());
	}

	private String getProduct(Object productId) {
		return ProductEndpoint.getProductBaseUri()
				.replaceAll(ProductEndpoint.getProductIdParameter(true), productId.toString());
	}

	private String getEventTypesForProduct(Object productId) {
		return ProductEndpoint.getEventTypesForProductBaseUri()
				.replaceAll(ProductEndpoint.getProductIdParameter(true), productId.toString());
	}

	private String getEventsForProductUri(Object productId, Object eventTypeId, Object indexFrom, Object indexTo) {
		return ProductEndpoint.getEventsForProductBaseUri()
				.replaceAll(ProductEndpoint.getProductIdParameter(true), productId.toString())
				.replaceAll(ProductEndpoint.getEventTypeIdParameter(true), eventTypeId.toString())
				.replaceAll(ProductEndpoint.getIndexFromParameter(true), indexFrom.toString())
				.replaceAll(ProductEndpoint.getIndexToParameter(true), indexTo.toString());
	}

	private String getUpdateStatusQueryUri(Object productId, Object newState) {
		return ProductEndpoint.getUpdateStatusQueryBaseUri()
				.replaceAll(ProductEndpoint.getProductIdParameter(true), productId.toString())
				.replaceAll(ProductEndpoint.getNewProductStatusParameter(true), newState.toString());
	}

}
