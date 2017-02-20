package de.hpi.bpt.argos.api;

import de.hpi.bpt.argos.api.event.EventEndpoint;
import de.hpi.bpt.argos.api.event.EventEndpointImpl;
import de.hpi.bpt.argos.api.eventQueries.EventQueryEndpoint;
import de.hpi.bpt.argos.api.eventQueries.EventQueryEndpointImpl;
import de.hpi.bpt.argos.api.eventTypes.EventTypeEndpoint;
import de.hpi.bpt.argos.api.eventTypes.EventTypeEndpointImpl;
import de.hpi.bpt.argos.api.product.ProductEndpoint;
import de.hpi.bpt.argos.api.product.ProductEndpointImpl;
import de.hpi.bpt.argos.api.productFamily.ProductFamilyEndpoint;
import de.hpi.bpt.argos.api.productFamily.ProductFamilyEndpointImpl;
import de.hpi.bpt.argos.api.response.ResponseFactory;
import de.hpi.bpt.argos.notifications.ClientUpdateService;
import de.hpi.bpt.argos.notifications.ClientUpdateServiceImpl;
import de.hpi.bpt.argos.persistence.database.PersistenceEntityManager;
import spark.Service;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class CustomerRestEndpointImpl implements CustomerRestEndpoint {

	protected PersistenceEntityManager entityManager;
	protected ResponseFactory responseFactory;

	protected ClientUpdateService clientUpdateService;
	protected EventEndpoint eventEndpoint;
	protected EventTypeEndpoint eventTypeEndpoint;
	protected ProductEndpoint productEndpoint;
	protected ProductFamilyEndpoint productFamilyEndpoint;
	protected EventQueryEndpoint eventQueryEndpoint;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setup(PersistenceEntityManager entityManager, Service sparkService, ResponseFactory responseFactory) {
		this.entityManager = entityManager;
		this.responseFactory = responseFactory;

		// keep this first
		clientUpdateService = new ClientUpdateServiceImpl();
		entityManager.subscribe(clientUpdateService);
		clientUpdateService.setup(sparkService);

		eventEndpoint = new EventEndpointImpl();
		eventEndpoint.setup(responseFactory, entityManager, sparkService);

		eventTypeEndpoint = new EventTypeEndpointImpl();
		eventTypeEndpoint.setup(responseFactory, entityManager, sparkService);

		productEndpoint = new ProductEndpointImpl();
		productEndpoint.setup(responseFactory, entityManager, sparkService);

		productFamilyEndpoint = new ProductFamilyEndpointImpl();
		productFamilyEndpoint.setup(responseFactory, entityManager, sparkService);

		eventQueryEndpoint = new EventQueryEndpointImpl();
		eventQueryEndpoint.setup(responseFactory, entityManager, sparkService);
	}
}
