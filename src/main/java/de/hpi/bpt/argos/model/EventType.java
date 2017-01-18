package de.hpi.bpt.argos.model;

import de.hpi.bpt.argos.serialization.Serializable;

import java.util.Set;

/**
 * This interface represents an EventType. It is serializable.
 */
public interface EventType extends Serializable {
	/**
	 * This method returns the number of events of this particular event type.
	 * @return - number of events as an integer
	 */
	int getNumberOfEvents();

	/**
	 * This method sets the number of events of this particular event type.
	 * @param numberOfEvents - the number of events to be set
	 */
	void setNumberOfEvents(int numberOfEvents);

	/**
	 * This method returns the name of this event type
	 * @return - name of the event type as a string
	 */
	String getName();

	/**
	 * This method sets the name of the event type.
	 * @param name - the name of the event type to be set
	 */
	void setName(String name);

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
	 * This method returns the set of attributes of this event type.
	 * @return - event attributes as a set
	 */
	Set<EventAttribute> getAttributes();

	/**
	 * This method sets the set of aatributes that this event type has.
	 * @param attributes - set of EventAttribute objects that characterize this event type
	 */
	void setAttributes(Set<EventAttribute> attributes);
}
