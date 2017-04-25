package de.hpi.bpt.argos.core;

import de.hpi.bpt.argos.storage.PersistenceAdapterImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Service;

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

		// TODO: check if unicorn is up
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
