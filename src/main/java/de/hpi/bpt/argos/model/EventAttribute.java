package de.hpi.bpt.argos.model.event;

import de.hpi.bpt.argos.serialization.Serializable;

/**
 * This interface represents an event attribute that every event type has a list of. It is serializable.
 */
public interface EventAttribute extends Serializable {
	/**
	 * This method returns the name of the event attribute.
	 * @return - the name of the event attribute as a string
	 */
	String getName();

	/**
	 * This method sets the name of the event attribute.
	 * @param name - the event attribute's name
	 */
	void setName(String name);

	/**
	 * This method returns the type of the event attribute.
	 * @return - the type of the event attribute as a string
	 */
	String getType();

	/**
	 * This method sets the type of the event attribute.
	 * @param type - the event attribute's type
	 */
	void setType(String type);
}
