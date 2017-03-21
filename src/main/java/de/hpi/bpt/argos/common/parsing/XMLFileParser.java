package de.hpi.bpt.argos.common.parsing;

import de.hpi.bpt.argos.persistence.database.PersistenceEntityManager;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;

/**
 * This interface represents file parser for the XML data format.
 */
public interface XMLFileParser {

	/**
	 * This method sets this XML file parser up.
	 * @param entityManager - the entity manager to save entities to
	 * @throws ParserConfigurationException - this exception will be thrown if the SAX parser was not configured correctly
	 * @throws SAXException - this exception will be thrown if there was an exception while parsing
	 */
	void setup(PersistenceEntityManager entityManager) throws ParserConfigurationException, SAXException;

	/**
	 * This method parses a given data file and stores entities in the database.
	 * @param dataFile - the file which contains entities to load
	 */
	void parse(File dataFile);
}
