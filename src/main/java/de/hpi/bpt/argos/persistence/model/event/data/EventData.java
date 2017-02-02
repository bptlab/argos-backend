package de.hpi.bpt.argos.persistence.model.event.data;

import de.hpi.bpt.argos.persistence.database.PersistenceEntity;
import de.hpi.bpt.argos.persistence.model.event.attribute.EventAttribute;

/**
 * This interface represents the value for one specific event attribute and one specific event. It extends persistence entity.
 */
public interface EventData extends PersistenceEntity {

	/**
	 * This method returns the related event attribute.
	 * @return - the related event attribute
	 */
	EventAttribute getEventAttribute();

	/**
	 * This method sets the related event attribute.
	 * @param eventAttribute - the attribute event to be set
	 */
	void setEventAttribute(EventAttribute eventAttribute);

	/**
	 * This method return the value of the related event in the related attribute.
	 * @return - the string representation of the value
	 */
	String getValue();

	/**
	 * This method sets the value of the related event in the related attribute.
	 * @param value - the string representation of the value to be set
	 */
	void setValue(String value);
}
