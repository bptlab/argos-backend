package de.hpi.bpt.argos.core;

import de.hpi.bpt.argos.common.EventProcessingPlatformUpdaterImpl;
import de.hpi.bpt.argos.common.RestEndpoint;
import de.hpi.bpt.argos.eventProcessing.EventReceiverImpl;
import de.hpi.bpt.argos.storage.PersistenceAdapterImpl;
import spark.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class ArgosImpl implements Argos {
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

		// TODO: parse default event types and entity data

		EventProcessingPlatformUpdaterImpl.getInstance().setup();

		Set<RestEndpoint> restEndpoints = new HashSet<>();
		restEndpoints.add(new EventReceiverImpl());
		// TODO: add more restEndpoints here

		for (RestEndpoint restEndpoint : restEndpoints) {
			restEndpoint.setup(sparkService);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop() {
		sparkService.stop();
		System.exit(1);
	}
}
