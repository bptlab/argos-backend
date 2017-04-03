package de.hpi.bpt.argos.common.parsing;

import de.hpi.bpt.argos.persistence.database.PersistenceEntityManager;

import java.io.File;

/**
 * This interface represents file parser for the XML data format.
 */
public interface XMLFileParser {

	/**
	 * This method sets this XML file parser up.
	 * @param entityManager - the entity manager to save entities to
	 */
	void setup(PersistenceEntityManager entityManager);

	/**
	 * This method parses a given data file and stores entities in the database.
	 * @param dataFile - the file which contains entities to load
	 */
	void parse(File dataFile);
}
