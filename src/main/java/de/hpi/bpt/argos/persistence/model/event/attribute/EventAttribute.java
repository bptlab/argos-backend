package de.hpi.bpt.argos.persistence.model.event.attribute;

import de.hpi.bpt.argos.persistence.database.PersistenceEntity;
import de.hpi.bpt.argos.persistence.model.event.data.EventDataType;

/**
 * This interface represents an event attribute that every event eventType has a list of. It extends persistence entity.
 */
public interface EventAttribute extends PersistenceEntity {

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
	 * This method returns the EventDataType of the event attribute.
	 * @return - the EventDataType of the event attribute
	 */
	EventDataType getType();

	/**
	 * This method sets the EventDataType of the event attribute.
	 * @param type - the event attribute's EventDataType
	 */
	void setType(EventDataType type);
}
