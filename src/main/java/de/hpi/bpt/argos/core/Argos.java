package de.hpi.bpt.argos.core;

import de.hpi.bpt.argos.eventHandling.EventReceiver;
import de.hpi.bpt.argos.eventHandling.EventSubscriber;
import de.hpi.bpt.argos.eventHandling.IEventReceiver;
import de.hpi.bpt.argos.eventHandling.IEventSubscriber;
import spark.Service;

import static spark.Service.ignite;

public class Argos implements IArgos {

	@Override
	public void run() {
		Service sparkService = startServer();

		IEventReceiver unicornEventReceiver = new EventReceiver();
		unicornEventReceiver.setup(sparkService);

		IEventSubscriber unicornEventSubscriber = new EventSubscriber();
		unicornEventSubscriber.subscribeToEventPlatform("http://localhost:8989", "/api/events/receiver", "{key: \"key\", value: \"value\"}");
	}

	private Service startServer() {
		return ignite()
				.port(8989)
				.threadPool(8);
	}
}
