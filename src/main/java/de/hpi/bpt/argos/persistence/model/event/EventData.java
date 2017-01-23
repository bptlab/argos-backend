package de.hpi.bpt.argos.persistence.model.event;

/**
 * This interface represents the value for one specific event attribute and one specific event.
 */
public interface EventData {

	/**
	 * This method return the unique identifier for this event data.
	 * @return - the unique identifier for this event data
	 */
	int getId();

	/**
	 * This method sets the unique identifier for this event data.
	 * @param id - the unique identifier to be set
	 */
	void setId(int id);

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
	 * This method return the related event.
	 * @return - the related event
	 */
	Event getEvent();

	/**
	 * This method sets the related event.
	 * @param event - the event to be set
	 */
	void setEvent(Event event);

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
