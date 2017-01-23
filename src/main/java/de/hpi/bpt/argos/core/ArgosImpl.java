package de.hpi.bpt.argos.core;

import de.hpi.bpt.argos.api.ProductFamilyEndpoint;
import de.hpi.bpt.argos.api.ProductFamilyEndpointImpl;
import de.hpi.bpt.argos.eventHandling.EventReceiver;
import de.hpi.bpt.argos.eventHandling.EventReceiverImpl;
import de.hpi.bpt.argos.eventHandling.EventSubscriber;
import de.hpi.bpt.argos.eventHandling.EventSubscriberImpl;

import de.hpi.bpt.argos.persistence.model.event.*;
import de.hpi.bpt.argos.persistence.model.product.ProductFamilyImpl;
import de.hpi.bpt.argos.persistence.model.product.ProductImpl;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Service;

import static spark.Service.ignite;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class ArgosImpl implements Argos {
	protected static final Logger logger = LoggerFactory.getLogger(ArgosImpl.class);
	protected static final String EXAMPLE_EVENT_QUERY = "SELECT * FROM FeedbackData";

	protected static final int DEFAULT_PORT = 8989;
	protected static final int DEFAULT_NUMBER_OF_THREADS = 8;

	protected Service sparkService;
	protected EventReceiver eventReceiver;
	protected EventSubscriber eventSubscriber;
	protected ProductFamilyEndpoint productFamilyEndpoint;
	protected SessionFactory sessionFactory;

    /**
     * {@inheritDoc}
     */
	@Override
	public void run(int port, int numberOfThreads) {
		sparkService = startServer(port, numberOfThreads);

		eventReceiver = new EventReceiverImpl();
		eventReceiver.setup(sparkService);

		eventSubscriber = new EventSubscriberImpl();
		// TODO: subscribe to unicorn
		//eventSubscriber.subscribeToEventPlatform(EXAMPLE_EVENT_QUERY);

		productFamilyEndpoint = new ProductFamilyEndpointImpl();
		productFamilyEndpoint.setup(sparkService);

		// TODO: setup websocket and security

		try {
			sessionFactory = new Configuration()
					.addAnnotatedClass(EventDataImpl.class)
					.addAnnotatedClass(EventSubscriptionQueryImpl.class)
					.addAnnotatedClass(EventAttributeImpl.class)
					.addAnnotatedClass(EventTypeImpl.class)
					.addAnnotatedClass(ProductImpl.class)
					.addAnnotatedClass(ProductFamilyImpl.class)
					.addAnnotatedClass(UpdateProductStateEventImpl.class)
					.addAnnotatedClass(EventImpl.class)
					.configure()
					.buildSessionFactory();
		} catch (ServiceException e) {
			logErrorWhileConnectingToDatabaseServer(e);
			shutdown();
		}
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public void run() {
		run(DEFAULT_PORT, DEFAULT_NUMBER_OF_THREADS);
	}

    /**
     * {@inheritDoc}
     */
    @Override
	public void shutdown() {
		sparkService.stop();
	}

    /**
     * This method starts the Spark service on a given port with a given number of threads.
     * @param port - port to be used as an integer
     * @param numberOfThreads - number of threads to be used as an integer
     * @return - returns a spark service object
     */
	private Service startServer(int port, int numberOfThreads) {
		return ignite()
				.port(port)
				.threadPool(numberOfThreads);
	}

    /**
     * This method logs errors on error level.
     * @param head - error message to be logged
     */
	protected void logError(String head, Throwable exception) {
		logger.error(head, exception);
	}

	protected void logErrorWhileConnectingToDatabaseServer(Throwable exception) {
		logError("can't connect to database server: ", exception);
	}
}
