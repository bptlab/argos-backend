package de.hpi.bpt.argos.parsing;

/**
 * This interface is meant to split the parsing process into little pieces.
 */
public interface XMLSubParser {

	/**
	 * This method returns the parent parser for this subParser.
	 * @return - the parent parser for this subParser
	 */
	XMLFileParser getParentParser();

	/**
	 * This method gets called whenever a new xml element is started.
	 * @param element - the name of the new xml element
	 */
	void startElement(String element);

	/**
	 * This method gets called whenever a xml element contains data.
	 * @param element - the name of the current xml element
	 * @param value - the value of the current xml element
	 */
	void elementValue(String element, String value);

	/**
	 * This method gets called whenever a xml element is closed.
	 * @param element - the name of the closed xml element
	 */
	void endElement(String element);
}
