package de.hpi.bpt.argos.model.event;

import de.hpi.bpt.argos.serialization.Serializable;

/**
 * This interface represents the metadata for an event type. It is serializable.
 */
public interface EventTypeMetaData extends Serializable {
	/**
	 * This method returns the number of events of the related event type.
	 * @return - number of events as an integer
	 */
	int getNumberOfEvents();

	/**
	 * This method sets the number of events of the related event type.
	 * @param numberOfEvents - the number of events to be set
	 */
	void setNumberOfEvents(int numberOfEvents);

	/**
	 * This method returns the name of the related event type.
	 * @return - name of the related event type as a string
	 */
	String getName();

	/**
	 * This method sets the name of the related event type.
	 * @param name - the name of the related event type to be set
	 */
	void setName(String name);
}
