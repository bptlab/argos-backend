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
}
