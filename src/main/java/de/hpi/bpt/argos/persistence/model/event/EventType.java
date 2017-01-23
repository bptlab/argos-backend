package de.hpi.bpt.argos.persistence.model.event;

import java.util.Set;

/**
 * This interface represents the event types.
 */
public interface EventType {
	/**
	 * This method returns the id of this particular event type.
	 * @return - id as an integer
	 */
	int getId();

	/**
	 * This method sets the id of this event type.
	 * @param id - id to be set
	 */
	void setId(int id);

	/**
	 * This method returns the name of this event type.
	 * @return - name of this event type as a string
	 */
	String getName();

	/**
	 * This method sets the name of this event type.
	 * @param name - the name of this event type to be set
	 */
	void setName(String name);

	/**
	 * This method return the event subscription query of this event type.
	 * @return - the event subscription query of this event type
	 */
	EventSubscriptionQuery getEventSubscriptionQuery();

	/**
	 * This method sets the event subscription query of this event type.
	 * @param eventSubscriptionQuery - the event subscription query to be set
	 */
	void setEventSubscriptionQuery(EventSubscriptionQuery eventSubscriptionQuery);

	/**
	 * This method returns the set of attributes of this event type.
	 * @return - event attributes as a set
	 */
	Set<EventAttribute> getAttributes();

	/**
	 * This method sets the set of aatributes that this event type has.
	 * @param attributes - set of EventAttribute objects that characterize this event type
	 */
	void setAttributes(Set<EventAttribute> attributes);

	/**
	 * This method returns a set of events of this event type.
	 * @return - a set of events of this event type
	 */
	Set<Event> getEvents();

	/**
	 * This method sets the set of events of this event type.
	 * @param events - a set of events to be set
	 */
	void setEvents(Set<Event> events);
}
