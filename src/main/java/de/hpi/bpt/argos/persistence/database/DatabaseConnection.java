package de.hpi.bpt.argos.persistence.database;

/**
 * This interface represents data connections which are used to communicate with the data base.
 */
public interface DatabaseConnection {

	/**
	 * This method sets up the data connection.
	 * @return - true if the connection was established
	 */
	boolean setup();
}
