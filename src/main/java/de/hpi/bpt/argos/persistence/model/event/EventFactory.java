package de.hpi.bpt.argos.persistence.model.event;

import de.hpi.bpt.argos.persistence.database.DatabaseConnection;
import de.hpi.bpt.argos.persistence.model.event.data.Event;
import de.hpi.bpt.argos.persistence.model.event.type.EventType;

/**
 * This interface represents factories which generate events from received json data.
 */
public interface EventFactory {

	/**
	 * This method generates an event from it's json representation.
	 * @param eventType - the type of the event
	 * @param jsonEvent - the json representation of the event
	 * @return - the event
	 */
	Event getEvent(EventType eventType, String jsonEvent);

	/**
	 * This method sets the database connection for this event factory.
	 * @param databaseConnection - the database connection to be set
	 */
	void setDatabaseConnection(DatabaseConnection databaseConnection);
}
