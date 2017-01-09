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

import java.util.HashSet;
import java.util.Set;

public class ProductFamilyEndpointImpl extends RestEndpointImpl implements ProductFamilyEndpoint {
	protected static final Gson serializer = new Gson();
	protected static final Logger logger = LoggerFactory.getLogger(ProductFamilyEndpointImpl.class);

	protected static final String GET_PRODUCT_FAMILIES = "/api/product";
	protected static final String GET_PRODUCT_FAMILY_OVERVIEW = "/api/product/:productId";

	protected Set<ProductFamily> productFamilies;

	private ProductFamily exampleFamily;

	public ProductFamilyEndpointImpl() {
		productFamilies = new HashSet<>();
	}

	@Override
	public void setup(Service sparkService) {
		sparkService.get(GET_PRODUCT_FAMILIES, this::getProductFamilies);
		sparkService.get(GET_PRODUCT_FAMILY_OVERVIEW, this::getProductFamilyOverview);

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

		String json = exampleFamily.toJson();

		return json;
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
}
