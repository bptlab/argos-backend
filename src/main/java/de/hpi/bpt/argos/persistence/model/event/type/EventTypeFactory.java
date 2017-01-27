package de.hpi.bpt.argos.persistence.model.event.type;

import de.hpi.bpt.argos.persistence.database.DatabaseConnection;


/**
 * This interface represents factories to create new event types.
 */
public interface EventTypeFactory {

	/**
	 * This method creates all simple event types needed if they are not existing already.
	 * @param databaseConnection - the database connection to store the event types
	 */
	void createSimpleEventTypes();

	/**
	 * This method sets the database connection for this event type factory.
	 * @param databaseConnection - the database connection to be set
	 */
	void setDatabaseConnection(DatabaseConnection databaseConnection);
}
