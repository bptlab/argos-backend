package de.hpi.bpt.argos.api.dataType;

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
public class DataTypeEndpointImpl extends RestEndpointImpl implements DataTypeEndpoint {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setup(ResponseFactory responseFactory, PersistenceEntityManager entityManager, Service sparkService) {
		super.setup(responseFactory, entityManager, sparkService);
		sparkService.get(DataTypeEndpoint.getDataTypesBaseUri(), this::getDataTypes);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDataTypes(Request request, Response response) {
		logInfoForReceivedRequest(request);

		String json = responseFactory.getSupportedDataTypes();

		logInfoForSendingResponse(request);
		return json;
	}
}
