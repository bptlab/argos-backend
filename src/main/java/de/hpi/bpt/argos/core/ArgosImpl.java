package de.hpi.bpt.argos.core;

import de.hpi.bpt.argos.eventHandling.*;
import de.hpi.bpt.argos.eventHandling.EventReceiver;
import de.hpi.bpt.argos.eventHandling.EventSubscriber;
import spark.Service;

import static spark.Service.ignite;

public class ArgosImpl implements Argos {
	protected static final String EXAMPLE_EVENT_QUERY = "SELECT * FROM FeedbackData";

	@Override
	public void run() {
		Service sparkService = startServer();

		EventReceiver unicornEventReceiver = new EventReceiverImpl();
		unicornEventReceiver.setup(sparkService);

		EventSubscriber unicornEventSubscriber = new EventSubscriberImpl();
		unicornEventSubscriber.subscribeToEventPlatform(EXAMPLE_EVENT_QUERY);
	}

	private Service startServer() {
		return ignite()
				.port(8989)
				.threadPool(8);
	}
}
