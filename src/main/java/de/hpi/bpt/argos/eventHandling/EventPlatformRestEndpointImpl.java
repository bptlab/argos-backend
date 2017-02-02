package de.hpi.bpt.argos.eventHandling;

import de.hpi.bpt.argos.api.response.ResponseFactory;
import de.hpi.bpt.argos.api.response.ResponseFactoryImpl;
import de.hpi.bpt.argos.persistence.database.PersistenceEntityManager;
import spark.Service;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class EventPlatformRestEndpointImpl implements EventPlatformRestEndpoint {

	protected PersistenceEntityManager entityManager;

	protected EventSubscriber eventSubscriber;
	protected ResponseFactory responseFactory;
	protected EventReceiver eventReceiver;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setup(PersistenceEntityManager entityManager, Service sparkService) {
		this.entityManager = entityManager;
		responseFactory = new ResponseFactoryImpl();
		responseFactory.setup(entityManager);

		eventSubscriber = new EventSubscriberImpl();
		eventSubscriber.setup(entityManager);

		eventReceiver = new EventReceiverImpl();
		eventReceiver.setup(responseFactory, entityManager, sparkService);
	}
}
