package de.hpi.bpt.argos.core;

import de.hpi.bpt.argos.api.ProductFamilyEndpoint;
import de.hpi.bpt.argos.api.ProductFamilyEndpointImpl;
import de.hpi.bpt.argos.eventHandling.*;
import de.hpi.bpt.argos.eventHandling.EventReceiver;
import de.hpi.bpt.argos.eventHandling.EventSubscriber;
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

	@Override
	public void run() {
		Service sparkService = startServer();

		EventReceiver unicornEventReceiver = new EventReceiverImpl();
		unicornEventReceiver.setup(sparkService);

		EventSubscriber unicornEventSubscriber = new EventSubscriberImpl();
		//unicornEventSubscriber.subscribeToEventPlatform(EXAMPLE_EVENT_QUERY);

		ProductFamilyEndpoint productFamilyEndpoint = new ProductFamilyEndpointImpl();
		productFamilyEndpoint.setup(sparkService);

		PushNotificationServerFactory notificationServerFactory = new PushNotificationServerFactoryImpl();
		PushNotificationServer notificationServer = notificationServerFactory.createServer();

		if (notificationServer == null) {
			logError("can't create notification server");
		}
	}

	private Service startServer() {
		return ignite()
				.port(8989)
				.threadPool(8);
	}

	protected void logError(String head) {
		logger.error(head);
	}
}
