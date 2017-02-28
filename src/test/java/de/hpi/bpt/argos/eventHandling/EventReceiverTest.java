package de.hpi.bpt.argos.eventHandling;

import com.google.gson.JsonObject;
import de.hpi.bpt.argos.api.eventType.EventTypeEndpoint;
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

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class EventReceiverTest extends EventPlatformEndpointParentClass {

	protected static Product testProduct;
	protected static EventType testEventType;

	@BeforeClass
	public static void initialize() {
		ProductFamily productFamily = ArgosTestUtil.createProductFamily();
		testProduct = ArgosTestUtil.createProduct(productFamily);
		testEventType = ArgosTestUtil.createEventType();
	}

	@Test
	public void testReceiveEvent() {
		request = requestFactory.createPostRequest(TEST_HOST,
				getReceiveEventUri(testEventType.getId()),
				TEST_CONTENT_TYPE,
				TEST_ACCEPT_TYPE_PLAIN);

		JsonObject jsonEvent = new JsonObject();
		jsonEvent.addProperty(testEventType.getTimestampAttribute().getName(), (new Date()).toString());
		jsonEvent.addProperty("productId", testProduct.getOrderNumber());
		jsonEvent.addProperty("productFamilyId", testProduct.getProductFamily().getName());

		request.setContent(serializer.toJson(jsonEvent));
		assertEquals(ResponseFactory.HTTP_SUCCESS_CODE, request.getResponseCode());

		List<Event> databaseEvents = ArgosTestParent.argos.getPersistenceEntityManager()
				.getEvents(testProduct.getId(), testEventType.getId(), 0,999);

		assertEquals(1, databaseEvents.size());
	}

	@Test
	public void testReceiveEvent_NewProduct_Success() {
		request = requestFactory.createPostRequest(TEST_HOST,
				getReceiveEventUri(testEventType.getId()),
				TEST_CONTENT_TYPE,
				TEST_ACCEPT_TYPE_PLAIN);

		int orderNumber = testProduct.getOrderNumber() + 1;

		JsonObject jsonEvent = new JsonObject();
		jsonEvent.addProperty(testEventType.getTimestampAttribute().getName(), (new Date()).toString());
		jsonEvent.addProperty("productId", orderNumber);
		jsonEvent.addProperty("productFamilyId", testProduct.getProductFamily().getName());

		request.setContent(serializer.toJson(jsonEvent));
		assertEquals(ResponseFactory.HTTP_SUCCESS_CODE, request.getResponseCode());

		Product product = ArgosTestParent.argos.getPersistenceEntityManager().getProduct(orderNumber);
		assertEquals(true, product != null);

		List<ProductFamily> productFamilies = ArgosTestParent.argos.getPersistenceEntityManager().getProductFamilies();
		assertEquals(1, productFamilies.size());
	}

	@Test
	public void testReceiveEvent_NewProductFamily_Success() {
		request = requestFactory.createPostRequest(TEST_HOST,
				getReceiveEventUri(testEventType.getId()),
				TEST_CONTENT_TYPE,
				TEST_ACCEPT_TYPE_PLAIN);

		int orderNumber = testProduct.getOrderNumber() + 1;
		String productFamilyId;

		do {
			productFamilyId = ArgosTestUtil.getRandomString();
		} while (productFamilyId.equals(testProduct.getProductFamily().getName()));

		JsonObject jsonEvent = new JsonObject();
		jsonEvent.addProperty(testEventType.getTimestampAttribute().getName(), (new Date()).toString());
		jsonEvent.addProperty("productId", orderNumber);
		jsonEvent.addProperty("productFamilyId", productFamilyId);

		request.setContent(serializer.toJson(jsonEvent));
		assertEquals(ResponseFactory.HTTP_SUCCESS_CODE, request.getResponseCode());

		Product product = ArgosTestParent.argos.getPersistenceEntityManager().getProduct(orderNumber);
		assertEquals(true, product != null);

		List<ProductFamily> productFamilies = ArgosTestParent.argos.getPersistenceEntityManager().getProductFamilies();
		assertEquals(2, productFamilies.size());

		ProductFamily productFamily = ArgosTestParent.argos.getPersistenceEntityManager().getProductFamily(productFamilyId);
		assertEquals(true, productFamily != null);
	}

	@Test
	public void testReceiveEvent_InvalidEventTypeId_NotFound() {
		request = requestFactory.createPostRequest(TEST_HOST,
				getReceiveEventUri(testEventType.getId() + 1),
				TEST_CONTENT_TYPE,
				TEST_ACCEPT_TYPE_PLAIN);

		assertEquals(ResponseFactory.HTTP_NOT_FOUND_CODE, request.getResponseCode());
	}

	@Test
	public void testReceiveStatusUpdateEvent() {
		request = requestFactory.createPostRequest(TEST_HOST,
				getReceiveStatusUpdateEventUri(testProduct.getId(), ProductState.RUNNING),
				TEST_CONTENT_TYPE,
				TEST_ACCEPT_TYPE_PLAIN);

		JsonObject jsonStatusUpdateEvent = new JsonObject();
		jsonStatusUpdateEvent.addProperty("timestamp", (new Date()).toString());

		request.setContent(serializer.toJson(jsonStatusUpdateEvent));
		assertEquals(ResponseFactory.HTTP_SUCCESS_CODE, request.getResponseCode());

		Product updatedProduct = ArgosTestParent.argos.getPersistenceEntityManager().getProduct(testProduct.getId());
		assertEquals(ProductState.RUNNING, updatedProduct.getState());
	}

	@Test
	public void testReceiveStatusUpdateEvent_InvalidNewState_Error() {
		request = requestFactory.createPostRequest(TEST_HOST,
				getReceiveStatusUpdateEventUri(testProduct.getId(), "invalid_state"),
				TEST_CONTENT_TYPE,
				TEST_ACCEPT_TYPE_PLAIN);

		JsonObject jsonStatusUpdateEvent = new JsonObject();
		jsonStatusUpdateEvent.addProperty("timestamp", (new Date()).toString());

		request.setContent(serializer.toJson(jsonStatusUpdateEvent));
		assertEquals(ResponseFactory.HTTP_ERROR_CODE, request.getResponseCode());
	}

	@Test
	public void testReceiveStatusUpdateEvent_InvalidProductId_NotFound() {
		request = requestFactory.createPostRequest(TEST_HOST,
				getReceiveStatusUpdateEventUri(testProduct.getId() - 1, ProductState.RUNNING),
				TEST_CONTENT_TYPE,
				TEST_ACCEPT_TYPE_PLAIN);

		JsonObject jsonStatusUpdateEvent = new JsonObject();
		jsonStatusUpdateEvent.addProperty("timestamp", (new Date()).toString());

		request.setContent(serializer.toJson(jsonStatusUpdateEvent));
		assertEquals(ResponseFactory.HTTP_NOT_FOUND_CODE, request.getResponseCode());
	}


	private String getReceiveEventUri(Object eventTypeId) {
		return EventReceiver.getReceiveEventBaseUri()
				.replaceAll(EventTypeEndpoint.getEventTypeIdParameter(true), eventTypeId.toString());
	}

	private String getReceiveStatusUpdateEventUri(Object productId, Object newState) {
		return EventReceiver.getReceiveStatusUpdateEventBaseUri()
				.replaceAll(ProductEndpoint.getProductIdParameter(true), productId.toString())
				.replaceAll(ProductEndpoint.getNewProductStatusParameter(true), newState.toString());
	}
}
