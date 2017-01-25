package de.hpi.bpt.argos.persistence.model.event;

import java.util.Set;

/**
 * This interface represents the event types.
 */
public interface EventType {
	/**
	 * This method returns the id of this particular event eventType.
	 * @return - id as an integer
	 */
	int getId();

	/**
	 * This method sets the id of this event eventType.
	 * @param id - id to be set
	 */
	void setId(int id);

	/**
	 * This method returns the name of this event eventType.
	 * @return - name of this event eventType as a string
	 */
	String getName();

	/**
	 * This method sets the name of this event eventType.
	 * @param name - the name of this event eventType to be set
	 */
	void setName(String name);

	/**
	 * This method return the event subscription query of this event eventType.
	 * @return - the event subscription query of this event eventType
	 */
	EventSubscriptionQuery getEventSubscriptionQuery();

	/**
	 * This method sets the event subscription query of this event eventType.
	 * @param eventSubscriptionQuery - the event subscription query to be set
	 */
	void setEventSubscriptionQuery(EventSubscriptionQuery eventSubscriptionQuery);

	/**
	 * This method returns the set of attributes of this event eventType.
	 * @return - event attributes as a set
	 */
	Set<EventAttribute> getAttributes();

	/**
	 * This method sets the set of aatributes that this event eventType has.
	 * @param attributes - set of EventAttribute objects that characterize this event eventType
	 */
	void setAttributes(Set<EventAttribute> attributes);

	/**
	 * This method returns a set of events of this event eventType.
	 * @return - a set of events of this event eventType
	 */
	Set<Event> getEvents();

	/**
	 * This method sets the set of events of this event eventType.
	 * @param events - a set of events to be set
	 */
	void setEvents(Set<Event> events);
}
