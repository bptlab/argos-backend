package de.hpi.bpt.argos.parsing.staticData.subParser;

import de.hpi.bpt.argos.parsing.XMLFileParser;
import de.hpi.bpt.argos.parsing.XMLSubParser;
import de.hpi.bpt.argos.parsing.XMLSubParserImpl;

/**
 * {@inheritDoc}
 * This subParser is responsible for parsing entityInstances.
 */
public class EntityInstanceParser extends XMLSubParserImpl implements XMLSubParser {

	/**
	 * This constructor sets the parent parser.
	 * @param parent - the parent parser to be set
	 */
	public EntityInstanceParser(XMLFileParser parent) {
		super(parent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startElement(String element) {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void elementValue(String element, String value) {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void endElement(String element) {

	}
}
