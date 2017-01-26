package de.hpi.bpt.argos.api;

import com.google.gson.Gson;
import de.hpi.bpt.argos.api.response.ResponseFactory;
import de.hpi.bpt.argos.api.response.ResponseFactoryImpl;
import de.hpi.bpt.argos.common.RestEndpointImpl;
import de.hpi.bpt.argos.persistence.database.DatabaseConnection;
import de.hpi.bpt.argos.persistence.model.event.Event;
import de.hpi.bpt.argos.persistence.model.event.EventType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static spark.Spark.halt;

/**
 *  {@inheritDoc}
 *  This is the implementation.
 */
public class ProductFamilyEndpointImpl extends RestEndpointImpl implements ProductFamilyEndpoint {
	protected static final Gson serializer = new Gson();
	protected static final Logger logger = LoggerFactory.getLogger(ProductFamilyEndpointImpl.class);

	protected static final int HTTP_ERROR_NOT_FOUND = 404;
	protected static final String GET_PRODUCT_FAMILIES = "/api/productfamilies";
	protected static final String GET_PRODUCT_OVERVIEW = "/api/products/:productId/eventtypes";
	protected static final String GET_EVENTS_FOR_PRODUCT =
			"/api/products/:productId/events/:eventTypeId/:indexFrom/:indexTo";
	protected ResponseFactory responseFactory;
	protected DatabaseConnection databaseConnection;

	public ProductFamilyEndpointImpl(DatabaseConnection databaseConnection) {
		this.databaseConnection = databaseConnection;
		responseFactory = new ResponseFactoryImpl(databaseConnection);
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
		int productId = validateInputInteger(request.params("productId"), (Integer input) -> input > 0);
		String json = responseFactory.getAllEventTypes(productId);
		logInfoForSendingProductFamilies(json);
		return json;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public String getEventsForProduct(Request request, Response response) {
		logInfoForReceivedRequest(request);

		int productId = validateInputInteger(request.params("productId"), (Integer input) -> input >= 0);
		int eventTypeId = validateInputInteger(request.params("eventTypeId"), (Integer input) -> input >= 0);
		int indexFrom = validateInputInteger(request.params("indexFrom"), (Integer input) ->  input >= 0);
		int indexTo = validateInputInteger(request.params("indexTo"), (Integer input) -> input >= indexFrom);

		List<Event> events = databaseConnection.listEventsForProductOfTypeInRange(productId, eventTypeId, indexFrom,
				indexTo);
		System.out.println(events);
		// TODO: implement logic

		return "";
	}

    /**
     * This method validates the input as an integer that is given as a string with a generic validation function.
     * @param inputValue - string to be tested
     * @param validateInputResult - function to be tested on the parsed integer as validation
	 * @return - returns a
     */
	protected int validateInputInteger(String inputValue, Function<Integer, Boolean> validateInputResult) {
	    //TODO: api fails with a less generic exception (InputMismatchException)
		try {
			if (!validateInputResult.apply(Integer.parseInt(inputValue))) {
				throw new Exception("input did not pass validation");
			}
		} catch (Exception e) {
			logErrorWhileInputValidation(inputValue, "Integer");
			halt(HTTP_ERROR_NOT_FOUND, e.getMessage());
		}
		return Integer.parseInt(inputValue);
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
     * This methods logs an info, if the product families (json) are sent as a response.
     * @param json - product families encoded as json string
     */
	protected void logInfoForSendingProductFamilies(String json) {
		logInfo("sending product families: " + json);
	}

    /**
     * This methods logs an error, if the input validation can't cast the inputValue eventType.
     * @param inputValue - inputValue from url
     * @param expectedInputType - expected inputType (must be a Java Class
     */
    protected void logErrorWhileInputValidation(String inputValue, String expectedInputType) {
		logError(String.format("tried to cast (input) \"%1$s\" to %2$s", inputValue, expectedInputType));
	}
}
