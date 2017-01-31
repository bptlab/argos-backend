package de.hpi.bpt.argos.core;

import de.hpi.bpt.argos.api.EventEndpoint;
import de.hpi.bpt.argos.api.EventEndpointImpl;
import de.hpi.bpt.argos.api.ProductFamilyEndpoint;
import de.hpi.bpt.argos.api.ProductFamilyEndpointImpl;
import de.hpi.bpt.argos.eventHandling.EventReceiver;
import de.hpi.bpt.argos.eventHandling.EventReceiverImpl;
import de.hpi.bpt.argos.eventHandling.EventSubscriber;
import de.hpi.bpt.argos.eventHandling.EventSubscriberImpl;
import de.hpi.bpt.argos.notifications.socket.PushNotificationClientHandler;
import de.hpi.bpt.argos.notifications.socket.PushNotificationClientHandlerImpl;
import de.hpi.bpt.argos.persistence.database.DatabaseConnection;
import de.hpi.bpt.argos.persistence.database.DatabaseConnectionImpl;
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

	protected static final int DEFAULT_PORT = 8989;
	protected static final int DEFAULT_NUMBER_OF_THREADS = 8;

	protected Service sparkService;
	protected EventReceiver eventReceiver;
	protected EventSubscriber eventSubscriber;
	protected ProductFamilyEndpoint productFamilyEndpoint;
	protected EventEndpoint eventEndpoint;
	protected DatabaseConnection databaseConnection;
	protected PushNotificationClientHandler notificationClientHandler;

    /**
     * {@inheritDoc}
     */
	@Override
	public void run(int port, int numberOfThreads) {
		sparkService = startServer(port, numberOfThreads);
		enableCORS(sparkService);

		databaseConnection = new DatabaseConnectionImpl();
		if (!databaseConnection.setup()) {
			shutdown();
		}

		// Keep this first, as spark wants to have all web sockets first
		notificationClientHandler = new PushNotificationClientHandlerImpl();
		notificationClientHandler.setup(sparkService);

		eventReceiver = new EventReceiverImpl(databaseConnection);
		eventReceiver.setup(sparkService);

		eventSubscriber = new EventSubscriberImpl(databaseConnection);
		eventSubscriber.setupEventPlatform();

		productFamilyEndpoint = new ProductFamilyEndpointImpl(databaseConnection);
		productFamilyEndpoint.setup(sparkService);

		eventEndpoint = new EventEndpointImpl(databaseConnection);
		eventEndpoint.setup(sparkService);


		// TODO: setup websocket and security
		sparkService.awaitInitialization();
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
	protected Service startServer(int port, int numberOfThreads) {
		return ignite()
				.port(port)
				.threadPool(numberOfThreads);
	}


	/**
	 * This method enables the CORS handling for every request. This could a security leak.
	 * @param sparkService - the sparkservice to be configured
	 */
	//TODO: fix the valneriddi
	protected void enableCORS(Service sparkService) {
		sparkService.options("/*", (request, response) -> {

			String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
			if (accessControlRequestHeaders != null) {
				response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
			}

			String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
			if (accessControlRequestMethod != null) {
				response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
			}

			return "OK";
		});

		sparkService.before((request, response) -> {
			response.header("Access-Control-Allow-Origin", "*");
			response.header("Access-Control-Request-Method", "*");
			response.header("Access-Control-Allow-Headers", "*");
			response.type("application/json");
		});
	}
    /**
     * This method logs errors on error level.
     * @param head - error message to be logged
     */
	protected void logError(String head, Throwable exception) {
		logger.error(head, exception);
	}
}
