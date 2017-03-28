package de.hpi.bpt.argos.api.productConfiguration;

import de.hpi.bpt.argos.api.response.ResponseFactory;
import de.hpi.bpt.argos.common.RestEndpointImpl;
import de.hpi.bpt.argos.persistence.database.PersistenceEntityManager;
import de.hpi.bpt.argos.persistence.model.product.ProductState;
import spark.Request;
import spark.Response;
import spark.Service;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class ProductConfigurationEndpointImpl extends RestEndpointImpl implements ProductConfigurationEndPoint {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setup(ResponseFactory responseFactory, PersistenceEntityManager entityManager, Service sparkService) {
		super.setup(responseFactory, entityManager, sparkService);
		sparkService.get(ProductConfigurationEndPoint.getProductConfigurationBaseUri(), this::getProductConfiguration);
		sparkService.post(ProductConfigurationEndPoint.getUpdateStatusQueryBaseUri(), this::updateStatusQuery);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getProductConfiguration(Request request, Response response) {
		logInfoForReceivedRequest(request);

		long productConfigurationId = inputValidation.validateLong(
				request.params(ProductConfigurationEndPoint.getProductConfigurationIdParameter(false)),
				(Long input) -> input > 0);

		String json = responseFactory.getProductConfiguration(productConfigurationId);

		logInfoForSendingResponse(request);
		return json;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String updateStatusQuery(Request request, Response response) {
		logInfoForReceivedRequest(request);

		long productConfigurationId = inputValidation.validateLong(
				request.params(ProductConfigurationEndPoint.getProductConfigurationIdParameter(false)),
				(Long input) -> input > 0);
		ProductState newState = inputValidation.validateEnum(
				ProductState.class,
				request.params(ProductConfigurationEndPoint.getNewProductStatusParameter(false)));

		responseFactory.updateStatusEventQuery(productConfigurationId, newState, request.body());

		logInfoForSendingResponse(request);
		return responseFactory.finishRequest();
	}
}
