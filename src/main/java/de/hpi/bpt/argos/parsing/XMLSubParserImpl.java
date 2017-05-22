package de.hpi.bpt.argos.parsing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@inheritDoc}
 * This is an abstract base class.
 */
public abstract class XMLSubParserImpl implements XMLSubParser {
	protected static final Logger logger = LoggerFactory.getLogger(XMLSubParserImpl.class);

	protected XMLFileParser parentParser;

	/**
	 * This constructor sets the parentParser of this subParser.
	 * @param parentParser - the parentParser to be set
	 */
	public XMLSubParserImpl(XMLFileParser parentParser) {
		this.parentParser = parentParser;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public XMLFileParser getParentParser() {
		return parentParser;
	}
}
