package de.hpi.bpt.argos.eventHandling;

public interface IEventSubscriber {
	boolean subscribeToEventPlatform(String host, String uri, String eventQuery);
}
