package de.hpi.bpt.argos.eventHandling;

import de.hpi.bpt.argos.persistence.database.PersistenceEntityManager;
import de.hpi.bpt.argos.persistence.model.event.EventQuery;
import de.hpi.bpt.argos.persistence.model.event.type.EventType;
import de.hpi.bpt.argos.properties.PropertyEditor;
import de.hpi.bpt.argos.properties.PropertyEditorImpl;


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
	 * This method sets up the default event platform by registering all of the event types.
	 */
	void setupEventPlatform();

    /**
     * This method creates a given event type on the default event processing platform.
     * @param eventType - the event type to be registered
     * @return - the feedback of the event platform
     */
	EventPlatformFeedback registerEventType(EventType eventType);

	/**
	 * This method deletes a given event type. This will delete the entry in the event processing platform as well as the entry in our database.
	 * @param eventType - the event type to delete
	 * @return - the feedback of the event platform
	 */
	EventPlatformFeedback deleteEventType(EventType eventType);

	/**
	 * This method updates the event query of a given event type.
	 * @param eventType - the event type, which event query should be updated
	 * @param newQueryString - the new event query string
	 * @return - the feedback of the event platform
	 */
	EventPlatformFeedback updateEventQuery(EventType eventType, String newQueryString);

	/**
	 * This method updates an event query.
	 * @param eventQuery - the event query to update
	 * @param newQueryString - the new event query string
	 * @param notificationUri - the uri to send events for this query to
	 * @return - the feedback of the event platform
	 */
	EventPlatformFeedback updateEventQuery(EventQuery eventQuery, String newQueryString, String notificationUri);

	/**
	 * This method subscribes to the default event platform using an EventQuery.
	 * @param eventType - the event type which contains the event query
	 * @return - the feedback of the event platform
	 */
	EventPlatformFeedback registerEventQuery(EventType eventType);

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

	/**
	 * This method returns the property key for the eventPlatformDeleteEventTypeUri property.
	 * @return - the property key for the eventPlatformDeleteEventTypeUri property
	 */
	static String getEventPlatformDeleteEventTypeUriPropertyKey() {
		return "eventPlatformDeleteEventTypeUri";
	}

	/**
	 * This method returns the property key for the eventPlatformDeleteEventQueryUri property.
	 * @return - the property key for the eventPlatformDeleteEventQueryUri property
	 */
	static String getEventPlatformDeleteEventQueryUriPropertyKey() {
		return "eventPlatformDeleteEventQueryUri";
	}

	/**
	 * This method returns the property key for the eventPlatformUpdateEventQueryUri property.
	 * @return - the property key for the eventPlatformUpdateEventQueryUri property
	 */
	static String getEventPlatformUpdateEventQueryUriPropertyKey() {
		return "eventPlatformUpdateEventQueryUri";
	}

	/**
	 * This method returns the URI to delete one specific event type on the event platform.
	 * @param eventTypeName - the event type name to delete
	 * @return - the URI to delete one specific event type
	 */
	static String getEventPlatformDeleteEventTypeUri(String eventTypeName) {
		PropertyEditor propertyEditor = new PropertyEditorImpl();

		String uri = propertyEditor.getProperty(getEventPlatformDeleteEventTypeUriPropertyKey());

		if (uri == null) {
			return "";
		} else {
			return uri.replaceAll(":1", eventTypeName);
		}
	}

	/**
	 * This method returns the URI to delete one specific event query on the event platform.
	 * @param eventQueryUuid - the event query uuid to delete
	 * @return - the URI to delete one specific event query
	 */
	static String getEventPlatformDeleteEventQueryUri(String eventQueryUuid) {
		PropertyEditor propertyEditor = new PropertyEditorImpl();

		String uri = propertyEditor.getProperty(getEventPlatformDeleteEventQueryUriPropertyKey());

		if (uri == null) {
			return "";
		} else {
			return uri.replaceAll(":1", eventQueryUuid);
		}
	}

	/**
	 * This method returns the URI to update one specific event query on the event platform.
	 * @param eventQueryUuid - the event query uuid to update
	 * @return - the URI to update one specific event query
	 */
	static String getEventPlatformUpdateEventQueryUri(String eventQueryUuid) {
		PropertyEditor propertyEditor = new PropertyEditorImpl();

		String uri = propertyEditor.getProperty(getEventPlatformUpdateEventQueryUriPropertyKey());

		if (uri == null) {
			return "";
		} else {
			return uri.replaceAll(":1", eventQueryUuid);
		}
	}
}
