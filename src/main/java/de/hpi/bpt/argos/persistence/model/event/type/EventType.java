package de.hpi.bpt.argos.persistence.model.event.type;

import de.hpi.bpt.argos.persistence.database.PersistenceEntity;
import de.hpi.bpt.argos.persistence.model.event.EventQuery;
import de.hpi.bpt.argos.persistence.model.event.attribute.EventAttribute;

import java.util.List;

/**
 * This interface represents the event types. It extends persistence entity.
 */
public interface EventType extends PersistenceEntity {

	/**
	 * This method returns the name of this event type.
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
	 * This method return the event query of this event eventType.
	 * @return - the event query of this event eventType
	 */
	EventQuery getEventQuery();

	/**
	 * This method sets the event query of this event eventType.
	 * @param eventQuery - the event query to be set
	 */
	void setEventQuery(EventQuery eventQuery);

	/**
	 * This method returns the list of attributes of this event type.
	 * @return - event attributes as a list
	 */
	List<EventAttribute> getAttributes();

	/**
	 * This method returns a specific event attribute of this event type.
	 * @param attributeName - the name of the requested attribute
	 * @return - the requested attribute, or null if no attributes exists
	 */
	EventAttribute getAttribute(String attributeName);

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
	 * This method indicates whether this event type is editable.
	 * @return - true, if the event type can be edited
	 */
	boolean isEditable();

	/**
	 * This method sets the editable property.
	 * @param editable - the value to be set
	 */
	void setEditable(boolean editable);

	/**
	 * This method indicates whether this event type is deletable.
	 * @return - true, if the event type can be deleted
	 */
	boolean isDeletable();

	/**
	 * This method sets the deletable property.
	 * @param deletable - the value to be set
	 */
	void setDeletable(boolean deletable);

	/**
	 * This method indicates, whether this event type should be registered in the event platform.
	 * @return - true, if the event type should be registered in the event platform
	 */
	boolean shouldBeRegistered();

	/**
	 * This method set, whether this event type should be registered in the event platform.
	 * @param shouldBeRegistered - the value to be set
	 */
	void setShouldBeRegistered(boolean shouldBeRegistered);

	/**
	 * This method returns the name of the productIdentification attribute.
	 * @return - the name of the productIdentification attribute
	 */
	static String getProductIdentificationAttributeName() {
		return "productId";
	}

	/**
	 * This method returns the name of the productFamilyIdentification attribute.
	 * @return - the name of the productFamilyIdentification attribute
	 */
	static String getProductFamilyIdentificationAttributeName() {
		return "productFamilyId";
	}

	/**
	 * This method returns the name of the codingPlugId attribute.
	 * @return - the name of the codingPlugId attribute
	 */
	static String getCodingPlugIdentificationAttributeName() {
		return "codingPlugId";
	}

	/**
	 * This method returns the name of codingPlugSoftwareVersion attribute.
	 * @return - the name of the codingPlugSoftwareVersion attribute
	 */
	static String getCodingPlugSoftwareVersionAttributeName() {
		return "codingPlugSoftwareVersion";
	}

	/**
	 * This method returns the cause identifier attribute name.
	 * @return - the name of the cause identifier attribute
	 */
	static String getCauseIdentifierAttributeName() {
		return "causeId";
	}

	/**
	 * This method returns the name of the errorDescription attribute.
	 * @return - the name of the errorDescription attribute
	 */
	static String getErrorDescriptionAttributeName() {
		return "errorDescription";
	}

	/**
	 * This method returns the special name for the status update event type.
	 * @return - the special name for the status update event type
	 */
	static String getStatusUpdateEventTypeName() {
		return "StatusUpdate";
	}
}
