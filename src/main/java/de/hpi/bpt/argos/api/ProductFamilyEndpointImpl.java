package de.hpi.bpt.argos.api;

import com.google.gson.Gson;
import de.hpi.bpt.argos.api.response.ResponseFactory;
import de.hpi.bpt.argos.api.response.ResponseFactoryImpl;
import de.hpi.bpt.argos.common.RestEndpointImpl;
import de.hpi.bpt.argos.common.validation.RestInputValidationService;
import de.hpi.bpt.argos.common.validation.RestInputValidationServiceImpl;
import de.hpi.bpt.argos.persistence.database.DatabaseConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Service;

import static spark.Spark.halt;

/**
 *  {@inheritDoc}
 *  This is the implementation.
 */
public class ProductFamilyEndpointImpl extends RestEndpointImpl implements ProductFamilyEndpoint {
	protected static final Gson serializer = new Gson();
	protected static final Logger logger = LoggerFactory.getLogger(ProductFamilyEndpointImpl.class);

	protected static final String GET_PRODUCT_FAMILIES = "/api/productfamilies";
	protected static final String GET_PRODUCT_OVERVIEW = "/api/products/:productId/eventtypes";
	protected static final String GET_EVENTS_FOR_PRODUCT =
			"/api/products/:productId/events/:eventTypeId/:indexFrom/:indexTo";

	protected ResponseFactory responseFactory;
	protected DatabaseConnection databaseConnection;
	protected RestInputValidationService inputValidation;

	public ProductFamilyEndpointImpl(DatabaseConnection databaseConnection) {
		this.databaseConnection = databaseConnection;
		responseFactory = new ResponseFactoryImpl(databaseConnection);
		inputValidation = new RestInputValidationServiceImpl();
	}

    /**
     * {@inheritDoc}
     */
    @Override
	public void setup(Service sparkService) {
		sparkService.get(GET_PRODUCT_FAMILIES, this::getProductFamilies);
		sparkService.get(GET_PRODUCT_OVERVIEW,this::getProductOverview);
		sparkService.get(GET_EVENTS_FOR_PRODUCT, this::getEventsForProduct);
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public String getProductFamilies(Request request, Response response) {
		logInfoForReceivedRequest(request);

		String json = responseFactory.getAllProductFamilies();
		logInfoForSendingProductFamilies(json);
		return json;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public String getProductOverview(Request request, Response response) {
		logInfoForReceivedRequest(request);
		int productId = inputValidation.validateInteger(request.params("productId"), (Integer input) -> input > 0);
		String json = responseFactory.getAllEventTypes(productId);
		logInfoForSendingProduct(json);
		return json;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public String getEventsForProduct(Request request, Response response) {
		logInfoForReceivedRequest(request);

		int productId = inputValidation.validateInteger(request.params("productId"), (Integer input) -> input >= 0);
		int eventTypeId = inputValidation.validateInteger(request.params("eventTypeId"), (Integer input) -> input >= 0);
		int indexFrom = inputValidation.validateInteger(request.params("indexFrom"), (Integer input) ->  input >= 0);
		int indexTo = inputValidation.validateInteger(request.params("indexTo"), (Integer input) -> input >= indexFrom);

		String json = responseFactory.getEventsForProduct(productId, eventTypeId, indexFrom, indexTo);
		logInfoForSendingEvents(json);
		return json;
	}

    /**
     * This method logs a string on info level.
     * @param head - string to be logged
     */
	protected void logInfo(String head) {
		logger.info(head);
	}

    /**
     * This method logs a string on error level.
     * @param head - string to be logged
     */
    protected void logError(String head) {
        logger.error(head);
    }

    /**
     * This method logs an info, if a request is received via url and the associated method was called.
     * @param request - Spark request object to be logged
     */
    protected void logInfoForReceivedRequest(Request request) {
		logInfo("received request : (uri) " + request.uri() + "    (body) " + request.body());
	}

    /**
     * This method logs an info, if the product families (json) are sent as a response.
     * @param json - product families encoded as json string
     */
	protected void logInfoForSendingProductFamilies(String json) {
		logInfo("sending product families: " + json);
	}

	/**
	 * This method logs an info, if a product (json) is sent as a response.
	 * @param json - product encoded as json string
	 */
	protected void logInfoForSendingProduct(String json) {
		logInfo("sending product: " + json);
	}

	/**
	 * This method logs an info, if events (json) are sent as a response.
	 * @param json - events encoded as json string
	 */
	protected void logInfoForSendingEvents(String json) {
		logInfo("sending events: " + json);
	}
}
