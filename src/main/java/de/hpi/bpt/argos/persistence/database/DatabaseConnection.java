package de.hpi.bpt.argos.persistence.database;

/**
 * This interface represents data connections which are used to communicate with the data base.
 */
public interface DatabaseConnection extends PersistenceEntityRetriever {

	/**
	 * This method makes the database call to save a set of entities in the database server.
	 * @param entities - a list of entities to save
	 */
	void saveEntities(PersistenceEntity... entities);

	/**
	 * This method makes the database call to delete a set of entities in the database server.
	 * @param entities - a list of entities to delete
	 */
	void deleteEntities(PersistenceEntity... entities);

	/**
	 * This method returns which property key to use for the database connection host.
	 * @return - the database connection host as a string
	 */
	static String getDatabaseConnectionHostPropertyKey() {
		return "databaseConnectionHost";
	}

	/**
	 * This method returns which property key to use for the database connection username.
	 * @return - the database connection username as a string
	 */
	static String getDatabaseConnectionUsernamePropertyKey() {
		return "databaseConnectionUsername";
	}

	/**
	 * This method returns which property key to use for the database connection password.
	 * @return - the database connection password as a string
	 */
	static String getDatabaseConnectionPasswordPropertyKey() {
		return "databaseConnectionPassword";
	}
}
