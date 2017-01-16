package de.hpi.bpt.argos.api;

import com.google.gson.Gson;
import de.hpi.bpt.argos.common.RestEndpointImpl;
import de.hpi.bpt.argos.model.ProductFamily;
import de.hpi.bpt.argos.model.ProductFamilyImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Service;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Function;

import static spark.Spark.halt;

public class ProductFamilyEndpointImpl extends RestEndpointImpl implements ProductFamilyEndpoint {
	protected static final Gson serializer = new Gson();
	protected static final Logger logger = LoggerFactory.getLogger(ProductFamilyEndpointImpl.class);

	protected static final String GET_PRODUCT_FAMILIES = "/api/product";
	protected static final String GET_PRODUCT_FAMILY_OVERVIEW = "/api/product/:productId";
	protected static final String GET_EVENTS_FOR_PRODUCT_FAMILY = "/api/product/:productId/eventtype/:eventTypeId/:indexFrom/:indexTo";

	protected Set<ProductFamily> productFamilies;

	private ProductFamily exampleFamily;

	public ProductFamilyEndpointImpl() {
		productFamilies = new HashSet<>();
	}

	@Override
	public void setup(Service sparkService) {
		sparkService.get(GET_PRODUCT_FAMILIES, this::getProductFamilies);
		sparkService.get(GET_PRODUCT_FAMILY_OVERVIEW, this::getProductFamilyOverview);
		sparkService.get(GET_EVENTS_FOR_PRODUCT_FAMILY, this::getEventsForProductFamily);

		// EXAMPLE DATA
		exampleFamily = new ProductFamilyImpl();
		exampleFamily.setExampleData();
		productFamilies.add(exampleFamily);
	}

	@Override
	public String getProductFamilies(Request request, Response response) {
		logInfoForReceivedRequest(request);
		String json = serializer.toJson(productFamilies);
		logInfoForSendingProductFamilies(json);
		return json;
	}

	@Override
	public String getProductFamilyOverview(Request request, Response response) {
		logInfoForReceivedRequest(request);

		String productFamilyId = request.params("productId");
		validateInputInteger(productFamilyId, (Integer input) -> { return input > 0; });

		// TODO: implement logic

		// TODO: remove this example later
		String json = exampleFamily.toJson();

		return json;
	}

	@Override
	public String getEventsForProductFamily(Request request, Response response) {
		logInfoForReceivedRequest(request);

		String productFamilyId = request.params("productId");
		validateInputInteger(productFamilyId, (Integer input) -> { return input > 0; });

		// TODO: implement logic

		return "";
	}

	protected void validateInputInteger(String inputValue, Function<Integer, Boolean> validateInputResult) {
		try {
			int integer = Integer.parseInt(inputValue);
			if (!validateInputResult.apply(integer)) {
				throw new Exception("input did not pass validation");
			}
		} catch (Exception e) {
			logErrorWhileInputValidation(inputValue, "Integer");
			halt(404, e.getMessage());
		}
	}

	protected void logInfo(String head) {
		logger.info(head);
	}

	protected void logInfoForReceivedRequest(Request request) {
		logInfo("received request : (uri) " + request.uri() + "    (body) " + request.body());
	}

	protected void logInfoForSendingProductFamilies(String json) {
		logInfo("sending product families: " + json);
	}

	protected void logError(String head) {
		logger.error(head);
	}

	protected void logErrorWhileInputValidation(String inputValue, String expectedInputType) {
		logError(String.format("tried to cast (input) \"%1$s\" to %2$s", inputValue, expectedInputType));
	}
}
