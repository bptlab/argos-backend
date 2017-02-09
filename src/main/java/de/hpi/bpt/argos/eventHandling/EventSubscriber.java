package de.hpi.bpt.argos.eventHandling;

import de.hpi.bpt.argos.persistence.database.PersistenceEntityManager;
import de.hpi.bpt.argos.persistence.model.event.type.EventType;
import de.hpi.bpt.argos.properties.PropertyReader;


/**
 * This interface is used to subscribe an event query on the event processing platform.
 */
public interface EventSubscriber {

	/**
	 * This method sets up the event subscriber.
	 * @param entityManager - the entity manager to retrieve entities from
	 */
	void setup(PersistenceEntityManager entityManager);

	/**
	 * This method sets up the event platform by registering all of the event types.
	 * @param host - the event platform's host
	 * @param eventTypeUri - the event type register uri
	 * @param eventQueryUri - the event query register uri
	 */
	void setupEventPlatform(String host, String eventTypeUri, String eventQueryUri);

	/**
	 * This method sets up the default event platform by registering all of the event types.
	 */
	void setupEventPlatform();

	/**
	 * This method creates a given event type on the event processing platform (defined by host and uri).
	 * @param host - the event processing platform's host
	 * @param uri - the event processing platform api's uri
	 * @param eventType - the event type to be registered
	 * @return - boolean if the creation was successful
	 */
	boolean registerEventType(String host, String uri, EventType eventType);

    /**
     * This method creates a given event type on the default event processing platform.
     * @param eventType - the event type to be registered
     * @return - boolean if the subscription was successful
     */
	boolean registerEventType(EventType eventType);

	/**
	 * This method subscribes to the event platform using an EventSubscriptionQuery.
	 * @param host - the event processing platform's host
	 * @param uri - the event processing platform api's uri
	 * @param eventType - the event type which contains the event subscription query
	 * @return - true if subscription was successful
	 */
	boolean registerEventQuery(String host, String uri, EventType eventType);

	/**
	 * This method subscribes to the default event platform using an EventSubscriptionQuery.
	 * @param eventType - the event type which contains the event subscription query
	 * @return - true if subscription was successful
	 */
	boolean registerEventQuery(EventType eventType);

	/**
	 * This method checks whether a specific event type is already registered within the event platform.
	 * @param host - the event processing platform's host
	 * @param uri - the event processing platform api's uri
	 * @param eventType - the event type to be registered
	 * @return - true if the event type is already registered
	 */
	boolean isEventTypeRegistered(String host, String uri, EventType eventType);

	/**
	 * This method checks whether a specific event type is already registered within the event platform.
	 * @param eventType - the event type to be checked
	 * @return - true if the event type is already registered
	 */
	boolean isEventTypeRegistered(EventType eventType);


	/**
	 * This method returns the property key for the eventPlatformHost property.
	 * @return - the property key for the eventPlatformHost property
	 */
	static String getEventPlatformHostPropertyKey() {
		return "eventPlatformHost";
	}

	/**
	 * This method returns the property key for the eventPlatformEventQueryUri property.
	 * @return - the property key for the eventPlatformEventQuery property
	 */
	static String getEventPlatformEventQueryUriPropertyKey() {
		return "eventPlatformEventQueryUri";
	}

	/**
	 * This method returns the property key for the eventPlatformEventTypeUri property.
	 * @return - the property key for the eventPlatformEventTypeUri property
	 */
	static String getEventPlatformEventTypeUriPropertyKey() {
		return "eventPlatformEventTypeUri";
	}
}
