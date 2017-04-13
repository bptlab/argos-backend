package de.hpi.bpt.argos.core;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.hpi.bpt.argos.api.CustomerRestEndpoint;
import de.hpi.bpt.argos.api.CustomerRestEndpointImpl;
import de.hpi.bpt.argos.api.response.ResponseFactory;
import de.hpi.bpt.argos.api.response.ResponseFactoryImpl;
import de.hpi.bpt.argos.common.parsing.XMLFileParser;
import de.hpi.bpt.argos.eventHandling.EventPlatformRestEndpoint;
import de.hpi.bpt.argos.eventHandling.EventPlatformRestEndpointImpl;
import de.hpi.bpt.argos.persistence.database.PersistenceEntityManager;
import de.hpi.bpt.argos.persistence.database.PersistenceEntityManagerImpl;
import de.hpi.bpt.argos.persistence.model.event.statusUpdate.StatusUpdateEventTypeImpl;
import de.hpi.bpt.argos.persistence.model.event.type.EventType;
import de.hpi.bpt.argos.persistence.model.parsing.BackboneDataParserImpl;
import de.hpi.bpt.argos.persistence.model.parsing.DataFile;
import de.hpi.bpt.argos.properties.PropertyEditor;
import de.hpi.bpt.argos.properties.PropertyEditorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Service;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static spark.Service.ignite;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class ArgosImpl implements Argos {
	private static final Logger logger = LoggerFactory.getLogger(ArgosImpl.class);

	protected static final JsonParser jsonParser = new JsonParser();
	protected Service sparkService;
	protected PersistenceEntityManager entityManager;

	protected CustomerRestEndpoint customerRestEndpoint;
	protected EventPlatformRestEndpoint eventPlatformRestEndpoint;

    /**
     * {@inheritDoc}
     */
	@Override
	public void run(int port, int numberOfThreads) {

		sparkService = startServer(port, numberOfThreads);

		entityManager = new PersistenceEntityManagerImpl();
		if (!entityManager.setup()) {
			shutdown();
			return;
		}

		loadDefaultEventTypes();
		loadBackboneData();

		ResponseFactory responseFactory = new ResponseFactoryImpl();
		customerRestEndpoint = new CustomerRestEndpointImpl();
		eventPlatformRestEndpoint = new EventPlatformRestEndpointImpl();

		// Keep this order, as spark wants to have all web sockets first
		responseFactory.setup(entityManager, eventPlatformRestEndpoint);
		customerRestEndpoint.setup(entityManager, sparkService, responseFactory);
		eventPlatformRestEndpoint.setup(entityManager, sparkService, responseFactory);

		enableCORS(sparkService);
		sparkService.awaitInitialization();
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public void run() {
		run(Argos.getPort(), Argos.getThreads());
	}

    /**
     * {@inheritDoc}
     */
    @Override
	public void shutdown() {
		sparkService.stop();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTestMode(boolean testMode) {
		PropertyEditor propertyEditor = new PropertyEditorImpl();
		propertyEditor.setProperty(Argos.getArgosBackendTestModePropertyKey(), String.valueOf(testMode));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PersistenceEntityManager getPersistenceEntityManager() {
		return entityManager;
	}

	/**
     * This method starts the Spark service on a given port with a given number of threads.
     * @param port - port to be used as an integer
     * @param numberOfThreads - number of threads to be used as an integer
     * @return - returns a spark service object
     */
	protected Service startServer(int port, int numberOfThreads) {
		PropertyEditor propertyEditor = new PropertyEditorImpl();

		Service service = ignite()
								.port(port)
								.threadPool(numberOfThreads);

		String publicFiles = propertyEditor.getProperty(Argos.getArgosBackendPublicFilesPropertyKey());
		service.staticFileLocation(publicFiles);

		return service;
	}


	/**
	 * This method enables the CORS handling for every request. This could a security leak.
	 * @param sparkService - the sparkservice to be configured
	 */
	protected void enableCORS(Service sparkService) {

		PropertyEditor propertyReader = new PropertyEditorImpl();

		String allowedOrigin = propertyReader.getProperty(Argos.getCORSAllowedOriginPropertyKey());
		String allowedRequestMethod = propertyReader.getProperty(Argos.getCORSAllowedRequestMethodPropertyKey());


		sparkService.options("/api/*", (request, response) -> {

			String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
			if (accessControlRequestHeaders != null) {
				response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
			}

			String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
			if (accessControlRequestMethod != null) {
				response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
			}

			return "OK";
		});

		sparkService.before((request, response) -> {
			response.header("Access-Control-Allow-Origin", allowedOrigin);
			response.header("Access-Control-Request-Method", allowedRequestMethod);
			response.type("application/json");
		});
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

			for (File eventTypeFile : eventTypesDirectory.listFiles()) {
				if (!eventTypeFile.getName().endsWith(".json")) {
					continue;
				}

				loadDefaultEventType(eventTypeFile);
			}

		} catch (NullPointerException e) {
			logger.error("cannot find directory for default event types or not defined in properties");
			logger.trace("Reason: ", e);
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
			DataFile file = entityManager.getDataFile(eventTypeFile.getAbsolutePath());

			if (file.getModificationTimestamp() == eventTypeFile.lastModified()) {
				logger.info(String.format("skipped default event type file '%1$s', since it was loaded already", eventTypeFile.getName()));
				return;
			}

			String fileContent = new String(Files.readAllBytes(Paths.get(eventTypeFile.toURI())), StandardCharsets.UTF_8);

			JsonObject jsonEventType = jsonParser.parse(fileContent).getAsJsonObject();
			entityManager.createSimpleEventType(jsonEventType);

			file.setModificationTimestamp(eventTypeFile.lastModified());
			entityManager.updateEntity(file);

		} catch (Exception e) {
			logger.error("cannot load default event type from '" + eventTypeFile.getName() + "'.");
			logger.trace("Reason: ", e);
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
			logger.error("cannot find directory for backbone data or not defined in properties");
			logger.trace("Reason: ", e);
		}
	}

	/**
	 * This method loads all products and their error predictions from a specific backbone data file.
	 * @param parser - the XML file parser, which should be used to parse the backbone data file
	 * @param backboneDataFile - the backbone data file to parse
	 */
	protected void loadBackboneDataFile(XMLFileParser parser, File backboneDataFile) {
		try {

			DataFile file = entityManager.getDataFile(backboneDataFile.getAbsolutePath());

			if (file.getModificationTimestamp() == backboneDataFile.lastModified()) {
				logger.info(String.format("skipped backbone data file '%1$s', since it was loaded already", backboneDataFile.getName()));
				return;
			}

			parser.parse(backboneDataFile);

			file.setModificationTimestamp(backboneDataFile.lastModified());
			entityManager.updateEntity(file);

		} catch (Exception e) {
			logger.error("cannot load backbone data from '" + backboneDataFile.getName() + "'.");
			logger.trace("Reason: ", e);
		}
	}
}
