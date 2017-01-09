package de.hpi.bpt.argos.core;

import de.hpi.bpt.argos.eventHandling.*;
import de.hpi.bpt.argos.eventHandling.EventReceiver;
import de.hpi.bpt.argos.eventHandling.EventSubscriber;
import spark.Service;

import static spark.Service.ignite;

public class ArgosImpl implements Argos {

	@Override
	public void run() {
		Service sparkService = startServer();

		EventReceiver unicornEventReceiver = new EventReceiverImpl();
		unicornEventReceiver.setup(sparkService);

		EventSubscriber unicornEventSubscriber = new EventSubscriberImpl();
		unicornEventSubscriber.subscribeToEventPlatform("http://localhost:8989", "/api/events/receiver", "{key: \"key\", value: \"value\"}");
	}

	private Service startServer() {
		return ignite()
				.port(8989)
				.threadPool(8);
	}
}
