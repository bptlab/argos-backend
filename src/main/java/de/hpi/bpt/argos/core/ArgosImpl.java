package de.hpi.bpt.argos.core;

import de.hpi.bpt.argos.api.CustomerRestEndpoint;
import de.hpi.bpt.argos.api.CustomerRestEndpointImpl;
import de.hpi.bpt.argos.eventHandling.EventPlatformRestEndpoint;
import de.hpi.bpt.argos.eventHandling.EventPlatformRestEndpointImpl;
import de.hpi.bpt.argos.persistence.database.PersistenceEntityManager;
import de.hpi.bpt.argos.persistence.database.PersistenceEntityManagerImpl;
import de.hpi.bpt.argos.properties.PropertyEditor;
import de.hpi.bpt.argos.properties.PropertyEditorImpl;
import spark.Service;

import static spark.Service.ignite;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class ArgosImpl implements Argos {

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
		run(Argos.getPort(), Argos.getThreads());
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
		PropertyEditor propertyEditor = new PropertyEditorImpl();

		Service service = ignite()
								.port(port)
								.threadPool(numberOfThreads);

		String publicFiles = propertyEditor.getProperty(Argos.getArgosBackendPublicFilesPropertyKey());
		service.staticFileLocation(publicFiles);

		return service;
	}


	/**
	 * This method enables the CORS handling for every request. This could a security leak.
	 * @param sparkService - the sparkservice to be configured
	 */
	protected void enableCORS(Service sparkService) {

		PropertyEditor propertyReader = new PropertyEditorImpl();

		String allowedOrigin = propertyReader.getProperty(Argos.getCORSAllowedOriginPropertyKey());
		String allowedRequestMethod = propertyReader.getProperty(Argos.getCORSAllowedRequestMethodPropertyKey());


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
			response.header("Access-Control-Allow-Origin", allowedOrigin);
			response.header("Access-Control-Request-Method", allowedRequestMethod);
			response.type("application/json");
		});
	}
}
