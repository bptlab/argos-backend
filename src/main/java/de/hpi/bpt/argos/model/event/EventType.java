package de.hpi.bpt.argos.model.event;

import de.hpi.bpt.argos.serialization.Serializable;

import java.util.Set;

/**
 * This interface represents the event types. It is serializable.
 */
public interface EventType extends Serializable {
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
	 * This method returns the meta data of this particular event type.
	 * @return - meta data as an EventTypeMetaData object
	 */
	EventTypeMetaData getMetaData();

	/**
	 * This method sets the meta data of this event type.
	 * @param metaData - meta data for this event type
	 */
	void setMetaData(EventTypeMetaData metaData);

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
