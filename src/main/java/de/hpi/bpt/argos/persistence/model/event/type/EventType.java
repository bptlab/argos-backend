package de.hpi.bpt.argos.persistence.model.event.type;

import de.hpi.bpt.argos.persistence.database.PersistenceEntity;
import de.hpi.bpt.argos.persistence.model.event.Event;
import de.hpi.bpt.argos.persistence.model.event.attribute.EventAttribute;
import de.hpi.bpt.argos.persistence.model.event.EventSubscriptionQuery;

import java.util.List;

/**
 * This interface represents the event types. It extends persistence entity.
 */
public interface EventType extends PersistenceEntity {

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
	 * This method returns the schema representation of this event type.
	 * @return - the schema representation of this event type
	 */
	String getSchema();

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
	 * This method returns the list of attributes of this event eventType.
	 * @return - event attributes as a list
	 */
	List<EventAttribute> getAttributes();

	/**
	 * This method sets the set of attributes that this event eventType has.
	 * @param attributes - a list of EventAttribute objects that characterize this event eventType
	 */
	void setAttributes(List<EventAttribute> attributes);

	/**
	 * This method returns the timestamp attribute for this event type.
	 * @return - the attribute which represents the timestamp
	 */
	EventAttribute getTimestampAttribute();

	/**
	 * This method sets the timestamp attribute for this event type.
	 * @param eventAttribute - the timestamp attribute
	 */
	void setTimestampAttribute(EventAttribute eventAttribute);

	/**
	 * This method returns the product identification attribute for this event type.
	 * @return - the product identification attribute
	 */
	EventAttribute getProductIdentificationAttribute();

	/**
	 * This method sets the product identification attribute for this event type.
	 * @param eventAttribute - the product identification attribute to be set
	 */
	void setProductIdentificationAttribute(EventAttribute eventAttribute);

	/**
	 * This method returns the product family identification attribute for this event type.
	 * @return - the product family identification attribute
	 */
	EventAttribute getProductFamilyIdentificationAttribute();

	/**
	 * This method sets the product family identification attribute for this event type.
	 * @param eventAttribute - the product family identification attribute to be set
	 */
	void setProductFamilyIdentificationAttribute(EventAttribute eventAttribute);

	/**
	 * This method returns a list of events of this event eventType.
	 * @return - a list of events of this event eventType
	 */
	List<Event> getEvents();

	/**
	 * This method sets the list of events of this event eventType.
	 * @param events - a list of events to be set
	 */
	void setEvents(List<Event> events);
}
