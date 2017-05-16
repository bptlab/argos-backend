package de.hpi.bpt.argos.parsing.eventType;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.hpi.bpt.argos.core.Argos;
import de.hpi.bpt.argos.parsing.util.FileUtilImpl;
import de.hpi.bpt.argos.storage.PersistenceAdapterImpl;
import de.hpi.bpt.argos.storage.dataModel.attribute.type.TypeAttribute;
import de.hpi.bpt.argos.storage.dataModel.attribute.type.TypeAttributeImpl;
import de.hpi.bpt.argos.storage.dataModel.event.query.EventQuery;
import de.hpi.bpt.argos.storage.dataModel.event.query.EventQueryImpl;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public final class EventTypeParserImpl implements EventTypeParser {
	private static final Logger logger = LoggerFactory.getLogger(EventTypeParserImpl.class);
	private static final JsonParser jsonParser = new JsonParser();

	private static final String JSON_NAME = "name";
	private static final String JSON_QUERIES = "queries";
	private static final String JSON_TIMESTAMP = "timestamp";
	private static final String JSON_ATTRIBUTES = "attributes";

	private static EventTypeParser instance;

	/**
	 * This constructor hides the default public one to implement the singleton pattern.
	 */
	private EventTypeParserImpl() {

		/**
		 * File format from unicorn
		 * Please note, that the given data-types are ignored.
		 *
		 * {
		 *     "name":"EventTypeName",
		 *     "timestamp":"EventTypeTimestamp",
		 *     "attributes":{
		 *			"eventTypeAttribute1":"STRING",
		 *			"eventTypeAttribute2":"STRING",
		 *			"eventTypeAttribute3":"INTEGER",
		 *     },
		 *     "queries":{
		 *     		"Query1Description":"Query1",
		 *     		"Query2Description":"Query2",
		 *     }
		 * }
		 */
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
			if (!FileUtilImpl.getInstance().wasModified(eventTypeFile)) {
				logger.info(String.format("file '%1$s' was loaded already and therefore skipped", eventTypeFile.getName()));
				return;
			} else {
				FileUtilImpl.getInstance().modify(eventTypeFile);
			}

			String fileContent = new String(Files.readAllBytes(Paths.get(eventTypeFile.toURI())), StandardCharsets.UTF_8);

			JsonObject jsonEventType = jsonParser.parse(fileContent).getAsJsonObject();

			if (jsonEventType.get(JSON_NAME).getAsString().isEmpty()) {
				return;
			}

			List<EventType> eventTypes = PersistenceAdapterImpl.getInstance().getEventTypes();

			for (EventType eventType : eventTypes) {
				if (eventType.getName().equalsIgnoreCase(jsonEventType.get(JSON_NAME).getAsString())) {
					// event type name is already in use ==> do not continue creating this event type
					return;
				}
			}

			createEventType(jsonEventType);

		} catch (Exception e) {
			LoggerUtilImpl.getInstance().error(logger, String.format("cannot load event type from file '%1$s'", eventTypeFile.getName()), e);
		}
	}

	/**
	 * This method creates a new eventType from its Json representation.
	 * @param jsonEventType - the Json representation of the new eventType
	 */
	private void createEventType(JsonObject jsonEventType) {

		if (jsonEventType.get(JSON_NAME).getAsString().isEmpty()
				|| jsonEventType.get(JSON_TIMESTAMP).getAsString().isEmpty()
				|| jsonEventType.get(JSON_ATTRIBUTES).getAsJsonObject().entrySet().isEmpty()
				|| jsonEventType.get(JSON_QUERIES).getAsJsonObject().entrySet().isEmpty()) {
			return;
		}

		EventType newEventType = new EventTypeImpl();
		newEventType.setDeletable(false);
		newEventType.setShouldBeRegistered(true);
		newEventType.setName(jsonEventType.get(JSON_NAME).getAsString());
		PersistenceAdapterImpl.getInstance().saveArtifacts(newEventType);

		TypeAttribute timestampAttribute = createTypeAttribute(newEventType.getId(), jsonEventType.get(JSON_TIMESTAMP).getAsString());
		PersistenceAdapterImpl.getInstance().saveArtifacts(timestampAttribute);

		newEventType.setTimeStampAttributeId(timestampAttribute.getId());
		PersistenceAdapterImpl.getInstance().saveArtifacts(newEventType);

		Set<String> usedNames = new HashSet<>();
		Set<TypeAttribute> eventTypeAttributes = new HashSet<>();
		for (Map.Entry<String, JsonElement> typeAttribute : jsonEventType.get(JSON_ATTRIBUTES).getAsJsonObject().entrySet()) {
			if (usedNames.contains(typeAttribute.getKey())) {
				continue;
			}

			usedNames.add(typeAttribute.getKey());
			eventTypeAttributes.add(createTypeAttribute(newEventType.getId(), typeAttribute.getKey()));
		}

		List<EventQuery> eventQueries = new ArrayList<>();
		for (Map.Entry<String, JsonElement> query : jsonEventType.get(JSON_QUERIES).getAsJsonObject().entrySet()) {
			EventQuery newQuery = new EventQueryImpl();

			newQuery.setTypeId(newEventType.getId());
			newQuery.setDescription(query.getKey());
			newQuery.setQuery(query.getValue().getAsString());
			eventQueries.add(newQuery);
		}

		PersistenceAdapterImpl.getInstance().saveArtifacts(eventTypeAttributes.toArray(new TypeAttribute[eventTypeAttributes.size()]));
		PersistenceAdapterImpl.getInstance().saveArtifacts(eventQueries.toArray(new EventQuery[eventQueries.size()]));
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
