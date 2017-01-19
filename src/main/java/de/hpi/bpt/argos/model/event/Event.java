package de.hpi.bpt.argos.model.event;

import de.hpi.bpt.argos.serialization.Serializable;

/**
 * This interface represents the events. It is serializable.
 */
public interface Event extends Serializable {

	/**
	 * This method returns the event type of this event.
	 * @return - the event type of this event as an EventyType object
	 */
	EventType getType();

	/**
	 * This method sets the event type of this event.
	 * @param type - the event type
	 */
	void setEventType(EventType type);
}
