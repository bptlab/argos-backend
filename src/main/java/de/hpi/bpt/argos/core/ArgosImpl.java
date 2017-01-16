package de.hpi.bpt.argos.core;

import de.hpi.bpt.argos.api.ProductFamilyEndpoint;
import de.hpi.bpt.argos.api.ProductFamilyEndpointImpl;
import de.hpi.bpt.argos.eventHandling.*;
import de.hpi.bpt.argos.eventHandling.EventReceiver;
import de.hpi.bpt.argos.eventHandling.EventSubscriber;
import de.hpi.bpt.argos.model.ProductFamily;
import de.hpi.bpt.argos.socket.PushNotificationServer;
import de.hpi.bpt.argos.socket.PushNotificationServerFactory;
import de.hpi.bpt.argos.socket.PushNotificationServerFactoryImpl;
import org.omg.PortableServer.ThreadPolicyOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Service;

import static spark.Service.ignite;

public class ArgosImpl implements Argos {
	protected static final Logger logger = LoggerFactory.getLogger(ArgosImpl.class);
	protected static final String EXAMPLE_EVENT_QUERY = "SELECT * FROM FeedbackData";

	protected static final int DEFAULT_PORT = 8989;
	protected static final int DEFAULT_NUMBER_OF_THREADS = 8;

	protected Service sparkService;
	protected EventReceiver eventReceiver;
	protected EventSubscriber eventSubscriber;
	protected ProductFamilyEndpoint productFamilyEndpoint;

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
	}

	@Override
	public void run() {
		run(DEFAULT_PORT, DEFAULT_NUMBER_OF_THREADS);
	}

	@Override
	public void shutdown() {
		sparkService.stop();
	}

	private Service startServer(int port, int numberOfThreads) {
		return ignite()
				.port(port)
				.threadPool(numberOfThreads);
	}

	protected void logError(String head) {
		logger.error(head);
	}
}
