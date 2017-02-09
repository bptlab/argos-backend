package de.hpi.bpt.argos.core;

import de.hpi.bpt.argos.api.CustomerRestEndpoint;
import de.hpi.bpt.argos.api.CustomerRestEndpointImpl;
import de.hpi.bpt.argos.eventHandling.EventPlatformRestEndpoint;
import de.hpi.bpt.argos.eventHandling.EventPlatformRestEndpointImpl;
import de.hpi.bpt.argos.persistence.database.PersistenceEntityManager;
import de.hpi.bpt.argos.persistence.database.PersistenceEntityManagerImpl;

import spark.Service;

import static spark.Service.ignite;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class ArgosImpl implements Argos {

	protected static final int DEFAULT_PORT = 8989;
	protected static final int DEFAULT_NUMBER_OF_THREADS = 8;

	protected Service sparkService;
	protected PersistenceEntityManager entityManager;

	protected CustomerRestEndpoint customerRestEndpoint;
	protected EventPlatformRestEndpoint eventPlatformRestEndpoint;

    /**
     * {@inheritDoc}
     */
	@Override
	public void run(int port, int numberOfThreads) {
		sparkService = startServer(port, numberOfThreads);

		entityManager = new PersistenceEntityManagerImpl();
		if (!entityManager.setup()) {
			shutdown();
			return;
		}

		// Keep this first, as spark wants to have all web sockets first
		customerRestEndpoint = new CustomerRestEndpointImpl();
		customerRestEndpoint.setup(entityManager, sparkService);

		eventPlatformRestEndpoint = new EventPlatformRestEndpointImpl();
		eventPlatformRestEndpoint.setup(entityManager, sparkService);

		enableCORS(sparkService);
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
	//TODO: fix the vulnerability
	protected void enableCORS(Service sparkService) {
		final String ALLOWED_ORIGIN = "http://localhost:3000";
		final String ALLOWED_REQUEST_METHOD = "GET";

		sparkService.options("/api/*", (request, response) -> {

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
			response.header("Access-Control-Allow-Origin", ALLOWED_ORIGIN);
			response.header("Access-Control-Request-Method", ALLOWED_REQUEST_METHOD);
			response.type("application/json");
		});
	}
}
