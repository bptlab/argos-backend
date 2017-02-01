package de.hpi.bpt.argos.api.productFamily;

import de.hpi.bpt.argos.api.response.ResponseFactory;
import de.hpi.bpt.argos.common.RestEndpointImpl;
import de.hpi.bpt.argos.persistence.database.PersistenceEntityManager;

import spark.Request;
import spark.Response;
import spark.Service;

/**
 *  {@inheritDoc}
 *  This is the implementation.
 */
public class ProductFamilyEndpointImpl extends RestEndpointImpl implements ProductFamilyEndpoint {

    /**
     * {@inheritDoc}
     */
    @Override
	public void setup(ResponseFactory responseFactory, PersistenceEntityManager entityManager, Service sparkService) {
		super.setup(responseFactory, entityManager, sparkService);
		sparkService.get(ProductFamilyEndpoint.getProductFamiliesBaseUri(), this::getProductFamilies);
		sparkService.get(ProductFamilyEndpoint.getProductFamilyBaseUri(), this::getProductFamily);
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public String getProductFamilies(Request request, Response response) {
		logInfoForReceivedRequest(request);

		String json = responseFactory.getAllProductFamilies();
		logInfoForSendingResponse(request);
		return json;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getProductFamily(Request request, Response response) {
		logInfoForReceivedRequest(request);

		long productFamilyId = inputValidation.validateLong(request.params("productFamilyId"), (Long input) -> input > 0);
		String json = responseFactory.getProductFamily(productFamilyId);

		logInfoForSendingResponse(request);
		return json;
	}
}
