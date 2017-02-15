package de.hpi.bpt.argos.eventHandling;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.hpi.bpt.argos.api.response.ResponseFactory;
import de.hpi.bpt.argos.persistence.database.PersistenceEntityManager;
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

	protected static final String EVENT_TYPES_DIRECTORY = "/event_types";


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
			// since the path is delivered as URI, it is represented as a HTML string. Thus we need to replace %20 with file system spaces.
			File eventTypesDirectory = new File(this.getClass().getResource(EVENT_TYPES_DIRECTORY).getPath().replaceAll("%20", " "));

			for (File eventType : eventTypesDirectory.listFiles()) {
				if (!eventType.getName().endsWith(".json")) {
					continue;
				}

				loadDefaultEventType(eventType);
			}

		} catch (NullPointerException e) {
			logger.error("cannot find directory for default event types", e);
		}
	}

	/**
	 * This method loads the content of a specific file and parses this into an event type.
	 * @param eventTypeFile - the file to load
	 */
	protected void loadDefaultEventType(File eventTypeFile) {
		try {
			String fileContent = new String(Files.readAllBytes(Paths.get(eventTypeFile.toURI())), StandardCharsets.UTF_8);

			JsonObject jsonEventType = jsonParser.parse(fileContent).getAsJsonObject();
			entityManager.createSimpleEventType(jsonEventType, true);

		} catch (Exception e) {
			logger.error("cannot load default event type '" + eventTypeFile.getName() + "'.", e);
		}
	}
}
