package de.hpi.bpt.argos.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.hpi.bpt.argos.api.product.ProductEndpoint;
import de.hpi.bpt.argos.common.RestRequest;
import de.hpi.bpt.argos.persistence.model.product.ProductState;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ProductEndpointTest extends EndpointParentClass {
	protected static final Gson serializer = new Gson();

	protected RestRequest request;

	@Test
	public void testGetProduct() {
		request = requestFactory.createGetRequest(TEST_HOST, getProduct(-42), TEST_ACCEPT_TYPE);

		assertEquals(HTTP_INVALID_REQUEST_RESPONSE_CODE, request.getResponseCode());
	}

	@Test
	public void testGetEventTypes() {
		request = requestFactory.createGetRequest(TEST_HOST, getEventTypesForProduct(1), TEST_ACCEPT_TYPE);

		assertEquals(HTTP_SUCCESSFUL_RESPONSE_CODE, request.getResponseCode());

		request = requestFactory.createGetRequest(TEST_HOST, getEventTypesForProduct(-42), TEST_ACCEPT_TYPE);

		assertEquals(HTTP_INVALID_REQUEST_RESPONSE_CODE, request.getResponseCode());
	}

	@Test
	public void testGetEvents() {
		request = requestFactory.createGetRequest(TEST_HOST, getEventsForProductUri(1, 1, 0, 1),
				TEST_ACCEPT_TYPE);

		assertEquals(HTTP_SUCCESSFUL_RESPONSE_CODE, request.getResponseCode());

		request = requestFactory.createGetRequest(TEST_HOST, getEventsForProductUri(-1, 1, 0, 1),
				TEST_ACCEPT_TYPE);

		assertEquals(HTTP_INVALID_REQUEST_RESPONSE_CODE, request.getResponseCode());
	}

	@Test
	public void testUpdateStatusQuery() {
		request = requestFactory.createPostRequest(TEST_HOST, getUpdateStatusQueryUri(1, ProductState.RUNNING), TEST_CONTENT_TYPE,
				TEST_ACCEPT_TYPE);

		JsonObject jsonQuery = new JsonObject();
		jsonQuery.addProperty("eventQuery", "invalid query");

		request.setContent(serializer.toJson(jsonQuery));

		assertEquals(HTTP_NOT_FOUND_CODE, request.getResponseCode());

		request = requestFactory.createPostRequest(TEST_HOST, getUpdateStatusQueryUri(1, "invalid_state"), TEST_CONTENT_TYPE,
				TEST_ACCEPT_TYPE);

		request.setContent(serializer.toJson(jsonQuery));

		assertEquals(HTTP_INVALID_REQUEST_RESPONSE_CODE, request.getResponseCode());
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
