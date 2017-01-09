package de.hpi.bpt.argos.eventHandling;

public interface EventSubscriber {
	boolean subscribeToEventPlatform(String host, String uri, String eventQuery);
}
