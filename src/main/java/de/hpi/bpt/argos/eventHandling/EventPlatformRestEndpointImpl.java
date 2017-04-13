package de.hpi.bpt.argos.eventHandling;

import com.google.gson.JsonParser;
import de.hpi.bpt.argos.api.response.ResponseFactory;
import de.hpi.bpt.argos.persistence.database.PersistenceEntityManager;
import org.hibernate.annotations.common.util.impl.LoggerFactory;
import org.jboss.logging.Logger;
import spark.Service;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class EventPlatformRestEndpointImpl implements EventPlatformRestEndpoint {
	protected static final Logger logger = LoggerFactory.logger(EventPlatformRestEndpointImpl.class);
	protected static final JsonParser jsonParser = new JsonParser();

	protected PersistenceEntityManager entityManager;

	protected EventSubscriber eventSubscriber;
	protected ResponseFactory responseFactory;
	protected EventReceiver eventReceiver;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setup(PersistenceEntityManager entityManager, Service sparkService, ResponseFactory responseFactory) {
		this.entityManager = entityManager;
		this.responseFactory = responseFactory;

		eventSubscriber = new EventSubscriberImpl();
		eventSubscriber.setup(entityManager);
		eventSubscriber.setupEventPlatform();

		eventReceiver = new EventReceiverImpl();
		eventReceiver.setup(responseFactory, entityManager, sparkService);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventReceiver getEventReceiver() {
		return eventReceiver;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventSubscriber getEventSubscriber() {
		return eventSubscriber;
	}
}
