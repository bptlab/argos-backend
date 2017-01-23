package de.hpi.bpt.argos.core;

import de.hpi.bpt.argos.api.ProductFamilyEndpoint;
import de.hpi.bpt.argos.api.ProductFamilyEndpointImpl;
import de.hpi.bpt.argos.persistence.DatabaseConnection;
import de.hpi.bpt.argos.persistence.DatabaseConnectionImpl;
import de.hpi.bpt.argos.eventHandling.EventReceiver;
import de.hpi.bpt.argos.eventHandling.EventReceiverImpl;
import de.hpi.bpt.argos.eventHandling.EventSubscriber;
import de.hpi.bpt.argos.eventHandling.EventSubscriberImpl;

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
	protected DatabaseConnection databaseService;


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

		productFamilyEndpoint = new ProductFamilyEndpointImpl();
		productFamilyEndpoint.setup(sparkService);

		// TODO: setup websocket and security

		databaseService = new DatabaseConnectionImpl();
		if (!databaseService.createArgosDatabase()) {
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
	protected void logError(String head) {
		logger.error(head);
	}
}
