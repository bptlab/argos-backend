package de.hpi.bpt.argos.api.productConfiguration;

import de.hpi.bpt.argos.api.response.ResponseFactory;
import de.hpi.bpt.argos.common.RestEndpointImpl;
import de.hpi.bpt.argos.persistence.database.PersistenceEntityManager;
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
}
