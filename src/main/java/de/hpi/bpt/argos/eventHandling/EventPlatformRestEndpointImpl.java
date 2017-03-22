package de.hpi.bpt.argos.eventHandling;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.hpi.bpt.argos.api.response.ResponseFactory;
import de.hpi.bpt.argos.common.parsing.XMLFileParser;
import de.hpi.bpt.argos.core.Argos;
import de.hpi.bpt.argos.persistence.database.PersistenceEntityManager;
import de.hpi.bpt.argos.persistence.model.event.statusUpdate.StatusUpdateEventTypeImpl;
import de.hpi.bpt.argos.persistence.model.event.type.EventType;
import de.hpi.bpt.argos.persistence.model.parsing.BackboneDataParserImpl;
import de.hpi.bpt.argos.properties.PropertyEditor;
import de.hpi.bpt.argos.properties.PropertyEditorImpl;
import org.hibernate.annotations.common.util.impl.LoggerFactory;
import org.jboss.logging.Logger;
import spark.Service;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class EventPlatformRestEndpointImpl implements EventPlatformRestEndpoint {
	protected static final Logger logger = LoggerFactory.logger(EventPlatformRestEndpointImpl.class);
	protected static final JsonParser jsonParser = new JsonParser();

	protected PersistenceEntityManager entityManager;

	protected EventSubscriber eventSubscriber;
	protected ResponseFactory responseFactory;
	protected EventReceiver eventReceiver;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setup(PersistenceEntityManager entityManager, Service sparkService, ResponseFactory responseFactory) {
		this.entityManager = entityManager;
		this.responseFactory = responseFactory;

		loadDefaultEventTypes();
		loadBackboneData();

		eventSubscriber = new EventSubscriberImpl();
		eventSubscriber.setup(entityManager);
		eventSubscriber.setupEventPlatform();

		eventReceiver = new EventReceiverImpl();
		eventReceiver.setup(responseFactory, entityManager, sparkService);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventReceiver getEventReceiver() {
		return eventReceiver;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventSubscriber getEventSubscriber() {
		return eventSubscriber;
	}

	/**
	 * This method loads all default event types from the disk.
	 */
	protected void loadDefaultEventTypes() {
		try {

			loadStatusUpdateEventType();

			PropertyEditor propertyEditor = new PropertyEditorImpl();
			String eventTypesDirectoryPath = propertyEditor.getProperty(Argos.getArgosBackendEventTypeDirectoryPropertyKey());
			if (eventTypesDirectoryPath.length() == 0) {
				throw new NullPointerException();
			}

			// since the path is delivered as URI, it is represented as a HTML string. Thus we need to replace %20 with file system spaces.
			File eventTypesDirectory = new File(eventTypesDirectoryPath.replaceAll("%20", " "));

			for (File eventType : eventTypesDirectory.listFiles()) {
				if (!eventType.getName().endsWith(".json")) {
					continue;
				}

				loadDefaultEventType(eventType);
			}

		} catch (NullPointerException e) {
			logger.error("cannot find directory for default event types or not defined in properties", e);
		}
	}

	/**
	 * THis method loads the status update event type.
	 */
	protected void loadStatusUpdateEventType() {
		EventType statusUpdateEventType = entityManager.getEventType(EventType.getStatusUpdateEventTypeName());

		if (statusUpdateEventType != null) {
			return;
		}

		statusUpdateEventType = new StatusUpdateEventTypeImpl();
		entityManager.updateEntity(statusUpdateEventType);
	}

	/**
	 * This method loads the content of a specific file and parses this into an event type.
	 * @param eventTypeFile - the file to load
	 */
	protected void loadDefaultEventType(File eventTypeFile) {
		try {
			String fileContent = new String(Files.readAllBytes(Paths.get(eventTypeFile.toURI())), StandardCharsets.UTF_8);

			JsonObject jsonEventType = jsonParser.parse(fileContent).getAsJsonObject();
			entityManager.createSimpleEventType(jsonEventType);

		} catch (Exception e) {
			logger.error("cannot load default event type from '" + eventTypeFile.getName() + "'.", e);
		}
	}

	/**
	 * This method loads all backbone data files from the disk.
	 */
	protected void loadBackboneData() {
		try {
			PropertyEditor propertyEditor = new PropertyEditorImpl();

			boolean loadBackboneData = Boolean.parseBoolean(propertyEditor.getProperty(Argos.getArgosBackendLoadBackboneDataPropertyKey()));

			if (!loadBackboneData) {
				logger.debug("loading backbone data skipped");
				return;
			}

			String backboneDataDirectoryPath = propertyEditor.getProperty(Argos.getArgosBackendBackboneDataDirectoryPropertyKey());
			if (backboneDataDirectoryPath.length() == 0) {
				throw new NullPointerException();
			}

			// since the path is delivered as URI, it is represented as a HTML string. Thus we need to replace %20 with file system spaces.
			File backboneDataDirectory = new File(backboneDataDirectoryPath.replaceAll("%20", " "));
			XMLFileParser parser = new BackboneDataParserImpl();
			parser.setup(entityManager);

			for (File backboneData : backboneDataDirectory.listFiles()) {
				if (!backboneData.getName().endsWith(".xml")) {
					continue;
				}

				loadBackboneDataFile(parser, backboneData);
			}

		} catch (Exception e) {
			logger.error("cannot find directory for backbone data or not defined in properties", e);
		}
	}

	/**
	 * This method loads all products and their error predictions from a specific backbone data file.
	 * @param parser - the XML file parser, which should be used to parse the backbone data file
	 * @param backboneDataFile - the backbone data file to parse
	 */
	protected void loadBackboneDataFile(XMLFileParser parser, File backboneDataFile) {
		try {

			parser.parse(backboneDataFile);

		} catch (Exception e) {
			logger.error("cannot load backbone data from '" + backboneDataFile.getName() + "'.", e);
		}
	}
}
