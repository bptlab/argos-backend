package de.hpi.bpt.argos.core;

import de.hpi.bpt.argos.api.entity.EntityEndpointImpl;
import de.hpi.bpt.argos.api.entityMapping.EntityMappingEndpointImpl;
import de.hpi.bpt.argos.api.entityType.EntityTypeEndpointImpl;
import de.hpi.bpt.argos.api.eventQuery.EventQueryEndpointImpl;
import de.hpi.bpt.argos.api.eventType.EventTypeEndpointImpl;
import de.hpi.bpt.argos.common.EventProcessingPlatformUpdaterImpl;
import de.hpi.bpt.argos.common.RestEndpoint;
import de.hpi.bpt.argos.eventProcessing.EventReceiver;
import de.hpi.bpt.argos.eventProcessing.EventReceiverImpl;
import de.hpi.bpt.argos.eventProcessing.mapping.EventEntityMapper;
import de.hpi.bpt.argos.eventProcessing.mapping.EventEntityMapperImpl;
import de.hpi.bpt.argos.eventProcessing.status.EntityStatusCalculatorImpl;
import de.hpi.bpt.argos.notifications.ClientUpdateServiceImpl;
import de.hpi.bpt.argos.parsing.EventTypeParserImpl;
import de.hpi.bpt.argos.storage.PersistenceAdapterImpl;
import de.hpi.bpt.argos.util.LoggerUtilImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class ArgosImpl implements Argos {
	private static final Logger logger = LoggerFactory.getLogger(ArgosImpl.class);
	private Service sparkService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start() {
		sparkService = Service.ignite()
				.port(Argos.getPort())
				.threadPool(Argos.getThreads())
				.staticFileLocation(Argos.getPublicFiles());

		if (!PersistenceAdapterImpl.getInstance().establishConnection()) {
			stop();
			return;
		}

		// TODO: parse static data
		EventTypeParserImpl.getInstance().loadEventTypes();

		// keep this order, since web sockets should be registered before any web routes get registered
		(new ClientUpdateServiceImpl()).setup(sparkService);
		EventProcessingPlatformUpdaterImpl.getInstance().setup();

		EventReceiver eventReceiver = new EventReceiverImpl();

		Set<RestEndpoint> restEndpoints = new HashSet<>();
		restEndpoints.add(eventReceiver);
		restEndpoints.add(new EntityEndpointImpl());
		restEndpoints.add(new EntityMappingEndpointImpl());
		restEndpoints.add(new EntityTypeEndpointImpl());
		restEndpoints.add(new EventQueryEndpointImpl());
		restEndpoints.add(new EventTypeEndpointImpl());
		// TODO: add more restEndpoints here

		for (RestEndpoint restEndpoint : restEndpoints) {
			setupRestEndpoint(restEndpoint);
		}

		EventEntityMapper eventEntityMapper = new EventEntityMapperImpl();

		eventEntityMapper.setup(eventReceiver);
		(new EntityStatusCalculatorImpl()).setup(eventEntityMapper);

		enableCORS(sparkService);
		sparkService.awaitInitialization();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop() {
		sparkService.stop();
	}

	/**
	 * This method enables the CORS handling for every request. This could a security leak.
	 * @param sparkService - the sparkService to be configured
	 */
	private void enableCORS(Service sparkService) {
		sparkService.options(String.format("%1$s/api/*", Argos.getRoutePrefix()), (request, response) -> {

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
			response.header("Access-Control-Allow-Origin", Argos.getAllowedOrigin());
			response.header("Access-Control-Request-Method", Argos.getAllowedRequestMethod());
			response.type("application/json");
		});
	}

	/**
	 * This method tries to setup a given restEndpoint.
	 * @param restEndpoint - the restEndpoint to set up
	 */
	private void setupRestEndpoint(RestEndpoint restEndpoint) {
		try {
			restEndpoint.setup(sparkService);
		} catch (Exception e) {
			LoggerUtilImpl.getInstance().error(logger, String.format("cannot setup endpoint: '%1$s'", restEndpoint.getClass().getName()), e);
		}
	}
}
