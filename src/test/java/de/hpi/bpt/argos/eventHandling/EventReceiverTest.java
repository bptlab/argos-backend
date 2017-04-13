package de.hpi.bpt.argos.eventHandling;

import com.google.gson.JsonObject;
import de.hpi.bpt.argos.api.eventType.EventTypeEndpoint;
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
import de.hpi.bpt.argos.persistence.model.product.error.ErrorCause;
import de.hpi.bpt.argos.persistence.model.product.error.ErrorType;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EventReceiverTest extends EventPlatformEndpointParentClass {

	protected static ProductConfiguration testProductConfiguration;
	protected static Product testProduct;
	protected static EventType testEventType;
	protected static ErrorType testErrorType;
	protected static ErrorCause testErrorCause;

	@BeforeClass
	public static void initialize() {
		ProductFamily productFamily = ArgosTestUtil.createProductFamily();
		testProduct = ArgosTestUtil.createProduct(productFamily);
		testProductConfiguration = ArgosTestUtil.createProductConfiguration(testProduct);
		testEventType = ArgosTestUtil.createEventType();
		testErrorType = ArgosTestUtil.createErrorType(testProductConfiguration);
		testErrorCause = ArgosTestUtil.createErrorCause(testErrorType);
	}

	@Test
	public void testReceiveEvent() {
		List<Event> databaseEvents = ArgosTestParent.argos.getPersistenceEntityManager()
				.getEventsForProduct(testProduct.getId(), testEventType.getId(), 0,999);
		long currentNumberOfEvents = databaseEvents.size();

		float codingPlugSoftwareVersion = 1.0f;
		testProductConfiguration.addCodingPlugSoftwareVersion(codingPlugSoftwareVersion);
		ArgosTestParent.argos.getPersistenceEntityManager().updateEntity(testProductConfiguration);

		request = requestFactory.createPostRequest(TEST_HOST,
				getReceiveEventUri(testEventType.getId()),
				TEST_CONTENT_TYPE,
				TEST_ACCEPT_TYPE_PLAIN);

		JsonObject jsonEvent = new JsonObject();
		jsonEvent.addProperty(testEventType.getTimestampAttribute().getName(), (new Date()).toString());
		jsonEvent.addProperty("productId", testProduct.getOrderNumber());
		jsonEvent.addProperty("productFamilyId", testProduct.getProductFamily().getName());
		jsonEvent.addProperty("codingPlugId", testProductConfiguration.getCodingPlugId());
		jsonEvent.addProperty("codingPlugSoftwareVersion", codingPlugSoftwareVersion);
		jsonEvent.addProperty("causeId", testErrorType.getCauseCode());
		jsonEvent.addProperty("causeDescription", testErrorCause.getDescription());

		request.setContent(serializer.toJson(jsonEvent));
		assertEquals(ResponseFactory.HTTP_SUCCESS_CODE, request.getResponseCode());

		Product updatedProduct = ArgosTestParent.argos.getPersistenceEntityManager().getProduct(testProduct.getId());

		assertEquals(1, updatedProduct.getProductConfigurations().size());



        long productConfigurationId = updatedProduct.getProductConfiguration(testProductConfiguration.getCodingPlugId(), codingPlugSoftwareVersion).getId();

        ProductConfiguration configuration = ArgosTestParent.argos.getPersistenceEntityManager().getProductConfiguration(productConfigurationId);

        assertEquals(1, configuration.getErrorType(testErrorType.getCauseCode())
                .getErrorCause(testErrorCause.getDescription()).getErrorOccurrences());

		databaseEvents = ArgosTestParent.argos.getPersistenceEntityManager()
				.getEventsForProduct(testProduct.getId(), testEventType.getId(), 0,999);
		assertEquals(currentNumberOfEvents + 1, databaseEvents.size());
	}

	@Test
	public void testReceiveEvent_NoCause_Success() {
		List<Event> databaseEvents = ArgosTestParent.argos.getPersistenceEntityManager()
				.getEventsForProduct(testProduct.getId(), testEventType.getId(), 0,999);
		long currentNumberOfEvents = databaseEvents.size();

		float codingPlugSoftwareVersion = 1.0f;
		testProductConfiguration.addCodingPlugSoftwareVersion(codingPlugSoftwareVersion);
		ArgosTestParent.argos.getPersistenceEntityManager().updateEntity(testProductConfiguration);

		request = requestFactory.createPostRequest(TEST_HOST,
				getReceiveEventUri(testEventType.getId()),
				TEST_CONTENT_TYPE,
				TEST_ACCEPT_TYPE_PLAIN);

		JsonObject jsonEvent = new JsonObject();
		jsonEvent.addProperty(testEventType.getTimestampAttribute().getName(), (new Date()).toString());
		jsonEvent.addProperty("productId", testProduct.getOrderNumber());
		jsonEvent.addProperty("productFamilyId", testProduct.getProductFamily().getName());
		jsonEvent.addProperty("codingPlugId", testProductConfiguration.getCodingPlugId());
		jsonEvent.addProperty("codingPlugSoftwareVersion", codingPlugSoftwareVersion);
		// do not add causeId and errorDescription, so we can not put this event to any specific error cause
//		jsonEvent.addProperty("causeId", testErrorType.getCauseCode());
//		jsonEvent.addProperty("errorDescription", testErrorCause.getDescription());

		request.setContent(serializer.toJson(jsonEvent));
		assertEquals(ResponseFactory.HTTP_SUCCESS_CODE, request.getResponseCode());

		Product updatedProduct = ArgosTestParent.argos.getPersistenceEntityManager().getProduct(testProduct.getId());

		assertEquals(1, updatedProduct.getProductConfigurations().size());

		long productConfigurationId = updatedProduct.getProductConfiguration(testProductConfiguration.getCodingPlugId(), codingPlugSoftwareVersion).getId();

		ProductConfiguration configuration = ArgosTestParent.argos.getPersistenceEntityManager().getProductConfiguration(productConfigurationId);

		assertEquals(0, configuration.getErrorType(testErrorType.getCauseCode())
                .getErrorCause(testErrorCause.getDescription()).getErrorOccurrences());

		databaseEvents = ArgosTestParent.argos.getPersistenceEntityManager()
				.getEventsForProduct(testProduct.getId(), testEventType.getId(), 0,999);

		assertEquals(currentNumberOfEvents + 1, databaseEvents.size());
	}

	@Test
	public void testReceiveEvent_NewConfiguration_Success() {
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

		Product updatedProduct = ArgosTestParent.argos.getPersistenceEntityManager().getProduct(testProduct.getId());

		assertEquals(2, updatedProduct.getProductConfigurations().size());

		for (ProductConfiguration configuration : updatedProduct.getProductConfigurations()) {
			if (configuration.getId() == testProductConfiguration.getId()) {
				continue;
			}

			assertEquals(1, configuration.getNumberOfEvents());

            ProductConfiguration configurationWithErrorTypes = ArgosTestParent.argos.getPersistenceEntityManager().getProductConfiguration(configuration.getId());

			assertEquals(0, configurationWithErrorTypes.getErrorTypes().size());
		}
	}

	@Test
	public void testReceiveEvent_NewProduct_Success() {
		request = requestFactory.createPostRequest(TEST_HOST,
				getReceiveEventUri(testEventType.getId()),
				TEST_CONTENT_TYPE,
				TEST_ACCEPT_TYPE_PLAIN);

		long orderNumber = testProduct.getOrderNumber() + 1;

		JsonObject jsonEvent = new JsonObject();
		jsonEvent.addProperty(testEventType.getTimestampAttribute().getName(), (new Date()).toString());
		jsonEvent.addProperty("productId", orderNumber);
		jsonEvent.addProperty("productFamilyId", testProduct.getProductFamily().getName());

		request.setContent(serializer.toJson(jsonEvent));
		assertEquals(ResponseFactory.HTTP_SUCCESS_CODE, request.getResponseCode());

		Product product = ArgosTestParent.argos.getPersistenceEntityManager().getProductByExternalId(orderNumber);
		assertNotNull(product);

		assertEquals(1, product.getProductConfigurations().size());

		List<ProductFamily> productFamilies = ArgosTestParent.argos.getPersistenceEntityManager().getProductFamilies();
		assertEquals(1, productFamilies.size());
	}

	@Test
	public void testReceiveEvent_NewProductFamily_Success() {
		request = requestFactory.createPostRequest(TEST_HOST,
				getReceiveEventUri(testEventType.getId()),
				TEST_CONTENT_TYPE,
				TEST_ACCEPT_TYPE_PLAIN);

		long orderNumber = testProduct.getOrderNumber() + 1;
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

		Product product = ArgosTestParent.argos.getPersistenceEntityManager().getProductByExternalId(orderNumber);
		assertNotNull(product);

		List<ProductFamily> productFamilies = ArgosTestParent.argos.getPersistenceEntityManager().getProductFamilies();
		assertEquals(2, productFamilies.size());

		ProductFamily productFamily = ArgosTestParent.argos.getPersistenceEntityManager().getProductFamily(productFamilyId);
		assertNotNull(productFamily);
	}

	@Test
	public void testReceiveEvent_InvalidEventTypeId_NotFound() {
		request = requestFactory.createPostRequest(TEST_HOST,
				getReceiveEventUri(testEventType.getId() + 1),
				TEST_CONTENT_TYPE,
				TEST_ACCEPT_TYPE_PLAIN);

		assertEquals(ResponseFactory.HTTP_ERROR_CODE, request.getResponseCode());
	}

	@Test
	public void testReceiveStatusUpdateEvent() {
		request = requestFactory.createPostRequest(TEST_HOST,
				getReceiveStatusUpdateEventUri(testProductConfiguration.getId(), ProductState.ERROR),
				TEST_CONTENT_TYPE,
				TEST_ACCEPT_TYPE_PLAIN);

		JsonObject jsonStatusUpdateEvent = new JsonObject();
		jsonStatusUpdateEvent.addProperty("timestamp", (new Date()).toString());

		request.setContent(serializer.toJson(jsonStatusUpdateEvent));
		assertEquals(ResponseFactory.HTTP_SUCCESS_CODE, request.getResponseCode());

		ProductConfiguration updatedConfiguration = ArgosTestParent.argos.getPersistenceEntityManager().getProductConfiguration
				(testProductConfiguration.getId());

		assertEquals(ProductState.ERROR, updatedConfiguration.getState());

		Product updatedProduct = ArgosTestParent.argos.getPersistenceEntityManager().getProduct(testProduct.getId());
		assertEquals(ProductState.ERROR, updatedProduct.getState());
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
		assertEquals(ResponseFactory.HTTP_ERROR_CODE, request.getResponseCode());
	}


	private String getReceiveEventUri(Object eventTypeId) {
		return EventReceiver.getReceiveEventBaseUri()
				.replaceAll(EventTypeEndpoint.getEventTypeIdParameter(true), eventTypeId.toString());
	}

	private String getReceiveStatusUpdateEventUri(Object productId, Object newState) {
		return EventReceiver.getReceiveStatusUpdateEventBaseUri()
				.replaceAll(ProductConfigurationEndPoint.getProductConfigurationIdParameter(true), productId.toString())
				.replaceAll(ProductConfigurationEndPoint.getNewProductStatusParameter(true), newState.toString());
	}
}
