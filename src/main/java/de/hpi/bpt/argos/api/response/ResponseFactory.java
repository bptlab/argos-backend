package de.hpi.bpt.argos.api.response;

import de.hpi.bpt.argos.persistence.database.DatabaseConnection;

/**
 * This interface represents factories which produce rest responses.
 */
public interface ResponseFactory {

	void setDatabaseConnection(DatabaseConnection databaseConnection);

	String getAllProductFamilies();
}
