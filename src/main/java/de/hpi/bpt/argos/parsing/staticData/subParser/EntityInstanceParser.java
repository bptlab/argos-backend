package de.hpi.bpt.argos.parsing.staticData.subParser;

import de.hpi.bpt.argos.parsing.XMLFileParser;
import de.hpi.bpt.argos.parsing.staticData.ArtifactParser;
import de.hpi.bpt.argos.parsing.staticData.ArtifactParserImpl;

/**
 * {@inheritDoc}
 * This subParser is responsible for parsing entityInstances.
 */
public class EntityInstanceParserImpl extends ArtifactParserImpl implements ArtifactParser {

	/**
	 * This constructor sets the parent parser.
	 * @param parent - the parent parser to be set
	 */
	public EntityInstanceParserImpl(XMLFileParser parent) {
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
