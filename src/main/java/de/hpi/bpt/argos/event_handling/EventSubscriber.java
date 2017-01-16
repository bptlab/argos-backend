package de.hpi.bpt.argos.event_handling;

/**
 * This interface is used to subscribe an event query on the event processing platform.
 */
public interface EventSubscriber {
	/**
	 * This method subscribes a given event query on the event processing platform (defined by host and uri).
	 * @param host - the event processing platform's host
	 * @param uri - the event processing platform api's uri
	 * @param eventQuery - the event query to be registered
	 * @return - boolean if the subscription was successful
	 */
	boolean subscribeToEventPlatform(String host, String uri, String eventQuery);

    /**
     * This methods subscribes a given event query on the default event processing platform.
     * @param eventQuery - the event query to be registered
     * @return - boolean if the subscription was successful
     */
	boolean subscribeToEventPlatform(String eventQuery);
}
