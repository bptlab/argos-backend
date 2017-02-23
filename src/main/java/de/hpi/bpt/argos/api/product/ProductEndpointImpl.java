package de.hpi.bpt.argos.api.product;

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
public class ProductEndpointImpl extends RestEndpointImpl implements ProductEndpoint {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setup(ResponseFactory responseFactory, PersistenceEntityManager entityManager, Service sparkService) {
		super.setup(responseFactory, entityManager, sparkService);
		sparkService.get(ProductEndpoint.getProductBaseUri(), this::getProduct);
		sparkService.get(ProductEndpoint.getEventTypesForProductBaseUri(), this::getEventTypesForProduct);
		sparkService.get(ProductEndpoint.getEventsForProductBaseUri(), this::getEventsForProduct);
		sparkService.post(ProductEndpoint.getUpdateStatusQueryBaseUri(), this::updateStatusQuery);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getProduct(Request request, Response response) {
		logInfoForReceivedRequest(request);

		long productId = inputValidation.validateLong(
				request.params(ProductEndpoint.getProductIdParameter(false)),
				(Long input) -> input > 0);
		String json = responseFactory.getProduct(productId);

		logInfoForSendingResponse(request);
		return json;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEventTypesForProduct(Request request, Response response) {
		logInfoForReceivedRequest(request);

		long productId = inputValidation.validateLong(
				request.params(ProductEndpoint.getProductIdParameter(false)),
				(Long input) -> input > 0);
		String json = responseFactory.getAllEventTypes(productId);

		logInfoForSendingResponse(request);
		return json;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEventsForProduct(Request request, Response response) {
		logInfoForReceivedRequest(request);

		long productId = inputValidation.validateLong(
				request.params(ProductEndpoint.getProductIdParameter(false)),
				(Long input) -> input > 0);
		long eventTypeId = inputValidation.validateLong(
				request.params(ProductEndpoint.getEventTypeIdParameter(false)),
				(Long input) -> input >	0);
		int indexFrom = inputValidation.validateInteger(
				request.params(ProductEndpoint.getIndexFromParameter(false)),
				(Integer input) -> input >= 0);
		int indexTo = inputValidation.validateInteger(
				request.params(ProductEndpoint.getIndexToParameter(false)),
				(Integer input) -> input >= indexFrom);

		String json = responseFactory.getEventsForProduct(productId, eventTypeId, indexFrom, indexTo);

		logInfoForSendingResponse(request);
		return json;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String updateStatusQuery(Request request, Response response) {
		logInfoForReceivedRequest(request);

		long productId = inputValidation.validateLong(
				request.params(ProductEndpoint.getProductIdParameter(false)),
				(Long input) -> input > 0);
		ProductState newState = inputValidation.validateEnum(
				ProductState.class,
				request.params(ProductEndpoint.getNewProductStatusParameter(false)));

		responseFactory.updateStatusEventQuery(productId, newState, request.body());

		logInfoForSendingResponse(request);
		return responseFactory.finishRequest();
	}
}
