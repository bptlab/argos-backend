package de.hpi.bpt.argos.parsing.staticData;

import de.hpi.bpt.argos.core.Argos;
import de.hpi.bpt.argos.parsing.XMLFileParserImpl;
import de.hpi.bpt.argos.parsing.XMLSubParser;
import de.hpi.bpt.argos.parsing.staticData.subParser.EntityTypesParser;
import de.hpi.bpt.argos.util.LoggerUtilImpl;

import java.io.File;

/**
 * {@inheritDoc}
 * This is the concrete implementation for static data.
 */
public class StaticDataParserImpl extends XMLFileParserImpl implements StaticDataParser {

	private static final String ROOT_ELEMENT = "staticData";
	private static final String ENTITY_TYPES_ROOT_ELEMENT = "types";
	private static final String ENTITY_INSTANCES_ROOT_ELEMENT = "instances";

	private static StaticDataParser instance;

	private XMLSubParser activeSubParser;

	/**
	 * This constructor hides the implicit public one to implement the singleton pattern.
	 */
	private StaticDataParserImpl() {
		/**
		 * File format
		 *
		 * <staticData>
		 *     <types>
		 *         <type>
		 *             <name>SuperTypeA</name>
		 *             <attributes>
		 *                 <attribute>
		 *                     <name>AttributeA1</name>
		 *                 </attribute>
		 *                 <attribute>
		 *                     <name>AttributeA2</name>
		 *                 </attribute>
		 *                 		.
		 *                 		.
		 *                 		.
		 *             </attributes>
		 *             <childTypes>
		 *                 <type>
		 *                     <name>SubTypeA</name>
		 *                     <attributes>
		 *                         .
		 *                         .
		 *                         .
		 *                     </attributes>
		 *                     <childTypes>
		 *                         .
		 *                         .
		 *                         .
		 *                     </childTypes>
		 *                 </type>
		 *             </childTypes>
		 *         </type>
		 *         		.
		 *         		.
		 *         		.
		 *     </types>
		 *
		 *     <instances>
		 *         <instance>
		 *             <name>InstanceA</name>
		 *             <type>SuperTypeA</type>
		 *             <attributes>
		 *                 <AttributeA1>ValueA1</AttributeA1>
		 *                 <AttributeA2>ValueA2</AttributeA2>
		 *             </attributes>
		 *             <childInstances>
		 *                 <instance>
		 *                     <name>SubInstanceA</name>
		 *                     <type>SubTypeA</type>
		 *                     <attributes>
		 *                         .
		 *                         .
		 *                         .
		 *                     </attributes>
		 *                     <childInstances>
		 *                         .
		 *                         .
		 *                         .
		 *                     </childInstances>
		 *                 </instance>
		 *                 		.
		 *                 		.
		 *                 		.
		 *             </childInstances>
		 *         </instance>
		 *         		.
		 *         		.
		 *         		.
		 *     </instances>
		 * </staticData>
		 */
	}

	/**
	 * This method returns the singleton instance of this class.
	 * @return - the singleton instance of this class
	 */
	public static StaticDataParser getInstance() {
		if (instance == null) {
			instance = new StaticDataParserImpl();
		}

		return instance;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void loadStaticData() {
		String staticDataDirectory = Argos.getStaticDataDirectory();
		if (staticDataDirectory.length() == 0) {
			logger.info("static data directory not defined in properties");
			return;
		}

		// since the path is delivered as URI, it is represented as a HTML string. Thus we need to replace %20 with file system spaces.
		File dataDirectory = new File(staticDataDirectory.replaceAll("%20", " "));

		try {
			for (File staticDataFile : dataDirectory.listFiles()) {
				if (!staticDataFile.getName().endsWith(".xml")) {
					continue;
				}

				parse(staticDataFile);
			}

		} catch (NullPointerException e) {
			LoggerUtilImpl.getInstance().error(logger, "cannot find static data directory", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void startElement(String elementName) {
		if (activeSubParser != null) {
			activeSubParser.startElement(elementName);
			return;
		}

		switch (elementName) {
			case ENTITY_TYPES_ROOT_ELEMENT:
				activeSubParser = new EntityTypesParser();
				break;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void element(String elementData) {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void endElement(String elementName) {

	}
}
