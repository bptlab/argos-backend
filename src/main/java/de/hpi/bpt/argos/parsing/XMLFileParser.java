package de.hpi.bpt.argos.parsing;

import java.io.File;

/**
 * This interface represents file parser for the XML data format.
 */
@FunctionalInterface
public interface XMLFileParser {

	/**
	 * This method parses a given data file and stores entities in the database.
	 * @param dataFile - the file which contains entities to load
	 */
	void parse(File dataFile);
}
