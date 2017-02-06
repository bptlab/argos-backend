package de.hpi.bpt.argos.eventHandling.schema;

import de.hpi.bpt.argos.persistence.model.event.type.EventType;

/**
 * This interface represents generators which generate event type schema for the event platform.
 */
public interface EventTypeSchemaGenerator {

	/**
	 * This method returns a string representation of the specific event type.
	 * @param eventType - the event type
	 * @return - a string representation of the event type
	 */
	String getEventTypeSchema(EventType eventType);
}
