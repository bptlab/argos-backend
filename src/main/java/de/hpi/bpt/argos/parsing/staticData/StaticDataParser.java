package de.hpi.bpt.argos.parsing.staticData;

import de.hpi.bpt.argos.parsing.XMLFileParser;

/**
 * This interface extends the XMLFileParser interface and is responsible for parsing the static xml data.
 */
public interface StaticDataParser extends XMLFileParser {

	/**
	 * This method parses all static data files in the directory, which is configured.
	 */
	void loadStaticData();
}
