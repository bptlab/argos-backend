package de.hpi.bpt.argos.parsing.staticData;

import de.hpi.bpt.argos.core.Argos;
import de.hpi.bpt.argos.parsing.XMLFileParserImpl;
import de.hpi.bpt.argos.parsing.XMLSubParser;
import de.hpi.bpt.argos.parsing.staticData.subParser.EntityInstancesParser;
import de.hpi.bpt.argos.parsing.staticData.subParser.EntityTypesParser;
import de.hpi.bpt.argos.storage.PersistenceAdapterImpl;
import de.hpi.bpt.argos.util.LoggerUtilImpl;
import de.hpi.bpt.argos.util.performance.WatchImpl;
import de.hpi.bpt.argos.util.performance.WatchTask;

import java.io.File;

/**
 * {@inheritDoc}
 * This is the concrete implementation for static data.
 */
public final class StaticDataParserImpl extends XMLFileParserImpl implements StaticDataParser {

	private static final String ENTITY_TYPES_ROOT_ELEMENT = "types";
	private static final String ENTITY_INSTANCES_ROOT_ELEMENT = "instances";

	private static StaticDataParser instance;

	private XMLSubParser activeSubParser;
	private WatchTask activeWatchTask;
	private EntityTypeList entityTypes;

	/**
	 * This constructor hides the implicit public one to implement the singleton pattern.
	 */
	private StaticDataParserImpl() {
		entityTypes = new EntityTypeListImpl();

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
		if (!Argos.shouldLoadStaticData()) {
			return;
		}

		String staticDataDirectory = Argos.getStaticDataDirectory();
		if (staticDataDirectory.length() == 0) {
			logger.info("static data directory not defined in properties");
			return;
		}

		// since the path is delivered as URI, it is represented as a HTML string. Thus we need to replace %20 with file system spaces.
		File dataDirectory = new File(staticDataDirectory.replaceAll("%20", " "));

		if (!dataDirectory.isDirectory()) {
			logger.error("static data directory is not a directory");
			return;
		}

		File[] dataFiles = dataDirectory.listFiles();

		if (dataFiles == null) {
			logger.info("no static data files to parse");
			return;
		}

		for (File staticDataFile : dataFiles) {
			if (!staticDataFile.getName().endsWith(".xml")) {
				continue;
			}

			try {
				int oldEntityTypesCount = PersistenceAdapterImpl.getInstance().getEntityTypesCount();
				int oldEntitiesCount = PersistenceAdapterImpl.getInstance().getEntitiesCount();

				WatchImpl.measure(String.format("parsing static data file: '%1$s'", staticDataFile.getName()),  () -> parse(staticDataFile));

				int newEntityTypesCount = PersistenceAdapterImpl.getInstance().getEntityTypesCount();
				int newEntitiesCount = PersistenceAdapterImpl.getInstance().getEntitiesCount();

				logger.info(String.format("'%1$s' contained [%2$d entityTypes] and [%3$d entities]",
						staticDataFile.getName(),
						newEntityTypesCount - oldEntityTypesCount,
						newEntitiesCount - oldEntitiesCount));

			} catch (Exception e) {
				LoggerUtilImpl.getInstance().error(logger, String.format("cannot parse '%1$s'", staticDataFile.getName()), e);

			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void startElement(String elementName) {
		switch (elementName) {
			case ENTITY_TYPES_ROOT_ELEMENT:
				activeSubParser = new EntityTypesParser(this, entityTypes);

				if (activeWatchTask != null) {
					activeWatchTask.finish();
				}

				activeWatchTask = WatchImpl.start("parsing entity types", () -> {});
				return;

			case ENTITY_INSTANCES_ROOT_ELEMENT:
				activeSubParser = new EntityInstancesParser(this, entityTypes);

				if (activeWatchTask != null) {
					activeWatchTask.finish();
				}

				activeWatchTask = WatchImpl.start("parsing entity instances", () -> {});
				return;

			default:
				break;
		}

		if (activeSubParser != null) {
			activeWatchTask.also(() -> activeSubParser.startElement(elementName));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void element(String elementData) {
		if (elementData.matches("^\\s*$")) {
			return;
		}

		if (activeSubParser == null) {
			return;
		}

		activeWatchTask.also(() -> activeSubParser.elementValue(latestOpenedElement(0), elementData));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void endElement(String elementName) {
		if (activeSubParser == null) {
			return;
		}

		switch (elementName) {
			case ENTITY_TYPES_ROOT_ELEMENT:
				if (activeSubParser instanceof EntityTypesParser) {
					activeSubParser = null;
				}
				if (activeWatchTask != null) {
					activeWatchTask.finish();
				}
				activeWatchTask = null;
				break;

			case ENTITY_INSTANCES_ROOT_ELEMENT:
				if (activeSubParser instanceof EntityInstancesParser) {
					activeSubParser = null;
				}
				if (activeWatchTask != null) {
					activeWatchTask.finish();
				}
				activeWatchTask = null;
				break;

			default:
				break;
		}

		if (activeSubParser != null && activeWatchTask != null) {
			activeWatchTask.also(() -> activeSubParser.endElement(elementName));
		}
	}
}
