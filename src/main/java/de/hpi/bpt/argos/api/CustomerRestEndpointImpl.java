package de.hpi.bpt.argos.api;

import de.hpi.bpt.argos.api.event.EventEndpoint;
import de.hpi.bpt.argos.api.event.EventEndpointImpl;
import de.hpi.bpt.argos.api.product.ProductEndpoint;
import de.hpi.bpt.argos.api.product.ProductEndpointImpl;
import de.hpi.bpt.argos.api.productFamily.ProductFamilyEndpoint;
import de.hpi.bpt.argos.api.productFamily.ProductFamilyEndpointImpl;
import de.hpi.bpt.argos.api.response.ResponseFactory;
import de.hpi.bpt.argos.api.response.ResponseFactoryImpl;
import de.hpi.bpt.argos.notifications.ClientUpdateService;
import de.hpi.bpt.argos.notifications.ClientUpdateServiceImpl;
import de.hpi.bpt.argos.persistence.database.PersistenceEntityManager;
import de.hpi.bpt.argos.persistence.model.event.EventSubscriptionQuery;
import de.hpi.bpt.argos.persistence.model.event.EventSubscriptionQueryImpl;
import de.hpi.bpt.argos.persistence.model.event.attribute.EventAttribute;
import de.hpi.bpt.argos.persistence.model.event.attribute.EventAttributeImpl;
import de.hpi.bpt.argos.persistence.model.event.data.EventDataType;
import de.hpi.bpt.argos.persistence.model.event.type.EventType;
import de.hpi.bpt.argos.persistence.model.event.type.EventTypeImpl;
import spark.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class CustomerRestEndpointImpl implements CustomerRestEndpoint {

	protected PersistenceEntityManager entityManager;
	protected ResponseFactory responseFactory;

	protected ClientUpdateService clientUpdateService;
	protected EventEndpoint eventEndpoint;
	protected ProductEndpoint productEndpoint;
	protected ProductFamilyEndpoint productFamilyEndpoint;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setup(PersistenceEntityManager entityManager, Service sparkService) {
		this.entityManager = entityManager;
		responseFactory = new ResponseFactoryImpl();
		responseFactory.setup(entityManager);

		// keep this first
		clientUpdateService = new ClientUpdateServiceImpl();
		entityManager.subscribe(clientUpdateService);
		clientUpdateService.setup(sparkService);

		eventEndpoint = new EventEndpointImpl();
		eventEndpoint.setup(responseFactory, entityManager, sparkService);

		productEndpoint = new ProductEndpointImpl();
		productEndpoint.setup(responseFactory, entityManager, sparkService);

		productFamilyEndpoint = new ProductFamilyEndpointImpl();
		productFamilyEndpoint.setup(responseFactory, entityManager, sparkService);
	}
}
