package de.hpi.bpt.argos.persistence.model.parsing;

import de.hpi.bpt.argos.common.parsing.XMLFileParserImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * This class parses backbone data from XML files.
 */
public class BackboneDataParserImpl extends XMLFileParserImpl {

	protected List<String> openElements;

	/**
	 * This constructor initializes all members with default values.
	 */
	public BackboneDataParserImpl() {
		openElements = new ArrayList<>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void element(String elementData) {

	}
}
