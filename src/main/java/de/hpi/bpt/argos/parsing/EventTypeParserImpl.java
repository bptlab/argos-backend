package de.hpi.bpt.argos.parsing;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.hpi.bpt.argos.core.Argos;
import de.hpi.bpt.argos.storage.PersistenceAdapterImpl;
import de.hpi.bpt.argos.storage.dataModel.attribute.type.TypeAttribute;
import de.hpi.bpt.argos.storage.dataModel.attribute.type.TypeAttributeImpl;
import de.hpi.bpt.argos.storage.dataModel.event.type.EventType;
import de.hpi.bpt.argos.storage.dataModel.event.type.EventTypeImpl;
import de.hpi.bpt.argos.storage.util.DataFile;
import de.hpi.bpt.argos.storage.util.DataFileImpl;
import de.hpi.bpt.argos.util.LoggerUtilImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class EventTypeParserImpl implements EventTypeParser {
	private static final Logger logger = LoggerFactory.getLogger(EventTypeParserImpl.class);
	private static final JsonParser jsonParser = new JsonParser();

	private static final String JSON_NAME = "name";
	private static final String JSON_TIMESTAMP = "timestamp";
	private static final String JSON_ATTRIBUTES = "attributes";

	private static EventTypeParser instance;

	/**
	 * This constructor hides the default public one to implement the singleton pattern.
	 */
	private EventTypeParserImpl() {

	}

	/**
	 * This method returns the singleton instance of this class.
	 * @return - the singleton instance of this class
	 */
	public static EventTypeParser getInstance() {
		if (instance == null) {
			instance = new EventTypeParserImpl();
		}

		return instance;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void loadEventTypes() {
		String eventTypesDirectoryPath = Argos.getEventTypesDirectory();
		if (eventTypesDirectoryPath.length() == 0) {
			logger.info("event types directory not defined in properties");
			return;
		}

		// since the path is delivered as URI, it is represented as a HTML string. Thus we need to replace %20 with file system spaces.
		File eventTypesDirectory = new File(eventTypesDirectoryPath.replaceAll("%20", " "));

		try {
			for (File eventTypeFile : eventTypesDirectory.listFiles()) {
				if (!eventTypeFile.getName().endsWith(".json")) {
					continue;
				}

				loadDefaultEventType(eventTypeFile);
			}

		} catch (NullPointerException e) {
			LoggerUtilImpl.getInstance().error(logger, "cannot find event types directory", e);
		}
	}

	/**
	 * This method loads the content of a specific file and parses this into an event type.
	 * @param eventTypeFile - the file to load
	 */
	private void loadDefaultEventType(File eventTypeFile) {
		try {
			if (!wasModified(eventTypeFile)) {
				logger.info(String.format("file '%1$s' was loaded already and therefore skipped", eventTypeFile.getName()));
				return;
			} else {
				modify(eventTypeFile);
			}

			String fileContent = new String(Files.readAllBytes(Paths.get(eventTypeFile.toURI())), StandardCharsets.UTF_8);

			JsonObject jsonEventType = jsonParser.parse(fileContent).getAsJsonObject();

			if (jsonEventType.get(JSON_NAME).getAsString().isEmpty()) {
				return;
			}

			List<EventType> eventTypes = PersistenceAdapterImpl.getInstance().getEventTypes();

			for (EventType eventType : eventTypes) {
				if (eventType.getName().equalsIgnoreCase(jsonEventType.get(JSON_NAME).getAsString())) {
					return;
				}
			}

			createEventType(jsonEventType);

		} catch (Exception e) {
			LoggerUtilImpl.getInstance().error(logger, String.format("cannot load event type from file '%1$s'", eventTypeFile.getName()), e);
		}
	}

	/**
	 * This method checks whether a file was modified since the last time it was loaded.
	 * @param file - the file to check for modification
	 * @return - true, if the file was modified and therefore should be loaded again
	 */
	private boolean wasModified(File file) {
		DataFile dataFile = PersistenceAdapterImpl.getInstance().getDataFile(file.getAbsolutePath());

		return dataFile == null || dataFile.getModificationTimestamp() != file.lastModified();
	}

	/**
	 * This method updated the modificationTimestamp for a given file.
	 * @param file - the file to be updated
	 */
	private void modify(File file) {
		DataFile dataFile = PersistenceAdapterImpl.getInstance().getDataFile(file.getAbsolutePath());

		if (dataFile == null) {
			dataFile = new DataFileImpl();
			dataFile.setPath(file.getAbsolutePath());
		}

		dataFile.setModificationTimestamp(file.lastModified());

		PersistenceAdapterImpl.getInstance().saveArtifacts(dataFile);
	}

	/**
	 * This method create a new eventType from it's Json representation.
	 * @param jsonEventType - the Json representation of the new eventType
	 */
	private void createEventType(JsonObject jsonEventType) {

		if (jsonEventType.get(JSON_NAME).getAsString().isEmpty()
				|| jsonEventType.get(JSON_TIMESTAMP).getAsString().isEmpty()
				|| jsonEventType.get(JSON_ATTRIBUTES).getAsJsonObject().entrySet().isEmpty()) {
			return;
		}

		EventType newEventType = new EventTypeImpl();
		newEventType.setDeletable(false);
		newEventType.setShouldBeRegistered(true);
		newEventType.setName(jsonEventType.get(JSON_NAME).getAsString());

		PersistenceAdapterImpl.getInstance().saveArtifacts(newEventType);

		List<String> usedNames = new ArrayList<>();
		List<TypeAttribute> eventTypeAttributes = new ArrayList<>();
		TypeAttribute timestampAttribute = createTypeAttribute(newEventType.getId(), jsonEventType.get(JSON_TIMESTAMP).getAsString());

		PersistenceAdapterImpl.getInstance().saveArtifacts(timestampAttribute);

		newEventType.setTimeStampAttributeId(timestampAttribute.getId());
		PersistenceAdapterImpl.getInstance().saveArtifacts(newEventType);

		for (Map.Entry<String, JsonElement> typeAttribute : jsonEventType.get(JSON_ATTRIBUTES).getAsJsonObject().entrySet()) {
			if (usedNames.contains(typeAttribute.getKey())) {
				continue;
			} else {
				usedNames.add(typeAttribute.getKey());
			}

			eventTypeAttributes.add(createTypeAttribute(newEventType.getId(), typeAttribute.getKey()));
		}

		PersistenceAdapterImpl.getInstance().saveArtifacts(eventTypeAttributes.toArray(new TypeAttribute[eventTypeAttributes.size()]));
	}

	/**
	 * This method creates a new instance of typeAttribute.
	 * @param typeId - the type id the new attribute belongs to
	 * @param name - the name of the type attribute
	 * @return - the new typeAttribute
	 */
	private TypeAttribute createTypeAttribute(long typeId, String name) {
		TypeAttribute newTypeAttribute = new TypeAttributeImpl();

		newTypeAttribute.setTypeId(typeId);
		newTypeAttribute.setName(name);

		return newTypeAttribute;
	}
}
