package de.hpi.bpt.argos.parsing;

/**
 * {@inheritDoc}
 * This is an abstract base class.
 */
public abstract class XMLSubParserImpl implements XMLSubParser {

	private XMLFileParser parent;

	/**
	 * This constructor sets the parent parser for this subParser.
	 * @param parentParser - the parent parser to be set
	 */
	public XMLSubParserImpl(XMLFileParser parentParser) {
		parent = parentParser;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public XMLFileParser getParent() {
		return parent;
	}
}
