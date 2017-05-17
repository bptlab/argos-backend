package de.hpi.bpt.argos.parsing;

import java.io.File;

/**
 * This interface represents file parser for the XML data format.
 */
public interface XMLFileParser {

	/**
	 * This method parses a given data file and stores entities in the database.
	 * @param dataFile - the file which contains entities to load
	 */
	void parse(File dataFile);

	/**
	 * This method return the latest opened element name.
	 * @param topOffset - the element offset (0 -> latest, 1 -> one before latest, ...)
	 * @return - the element name
	 */
	String latestOpenedElement(int topOffset);
}
