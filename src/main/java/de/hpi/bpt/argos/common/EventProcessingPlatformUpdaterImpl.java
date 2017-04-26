package de.hpi.bpt.argos.common;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.hpi.bpt.argos.core.Argos;
import de.hpi.bpt.argos.eventProcessing.EventReceiver;
import de.hpi.bpt.argos.storage.PersistenceAdapterImpl;
import de.hpi.bpt.argos.storage.dataModel.attribute.type.TypeAttribute;
import de.hpi.bpt.argos.storage.dataModel.event.query.EventQuery;
import de.hpi.bpt.argos.storage.dataModel.event.type.EventType;
import de.hpi.bpt.argos.util.XSDParserImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public final class EventProcessingPlatformUpdaterImpl implements EventProcessingPlatformUpdater {
	private static final Logger logger = LoggerFactory.getLogger(EventProcessingPlatformUpdaterImpl.class);
	private static final Gson serializer = new Gson();

	private static EventProcessingPlatformUpdater instance;

	/**
	 * This constructor hides the default public one to implement the singleton pattern.
	 */
	private EventProcessingPlatformUpdaterImpl() {

	}

	/**
	 * This method returns the singleton instance of this class.
	 * @return - the singleton instance of this class
	 */
	public static EventProcessingPlatformUpdater getInstance() {
		if (instance == null) {
			instance = new EventProcessingPlatformUpdaterImpl();
		}

		return instance;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setup() {
		List<EventType> eventTypes = PersistenceAdapterImpl.getInstance().getEventTypes();

		for (EventType eventType : eventTypes) {
			if (!registerEventType(eventType).isSuccessful()) {
				// eventType does already exist
				continue;
			}

			List<EventQuery> eventQueries = PersistenceAdapterImpl.getInstance().getEventQueries(eventType.getId());

			for (EventQuery eventQuery : eventQueries) {
				registerEventQuery(eventType.getId(), eventQuery);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventPlatformFeedback registerEventType(EventType eventType) {
		if (!eventType.shouldBeRegistered()) {
			return new EventPlatformFeedbackImpl("event type should not be registered", true);
		}

		TypeAttribute timestampAttribute = PersistenceAdapterImpl.getInstance().getTypeAttribute(eventType.getTimeStampAttributeId());
		List<TypeAttribute> eventTypeAttributes = PersistenceAdapterImpl.getInstance().getTypeAttributes(eventType.getId());

		if (timestampAttribute == null) {
			return new EventPlatformFeedbackImpl("event type has no valid timestamp attribute", false);
		}

		String host = EventProcessingPlatformUpdater.getHost();
		String uri = EventProcessingPlatformUpdater.getRegisterEventTypeUri();

		RestRequest request = RestRequestFactoryImpl.getInstance().createPostRequest(host, uri);

		JsonObject requestBody = new JsonObject();
		requestBody.addProperty("xsd", XSDParserImpl.getInstance().getEventTypeSchema(eventType, eventTypeAttributes));
		requestBody.addProperty("schemaName", eventType.getName());
		requestBody.addProperty("timestampName", timestampAttribute.getName());

		request.setContent(serializer.toJson(requestBody));

		logger.info(String.format("register event type: '%1$s' -> '%2$d'", eventType.getName(), request.getResponseCode()));

		return new EventPlatformFeedbackImpl(request);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventPlatformFeedback deleteEventType(EventType eventType) {
		if (eventType.getName() == null || eventType.getName().length() == 0) {
			return new EventPlatformFeedbackImpl("event type has no name", false);
		}

		String host = EventProcessingPlatformUpdater.getHost();
		String uri = EventProcessingPlatformUpdater.getDeleteEventTypeUri(eventType.getName());

		RestRequest request = RestRequestFactoryImpl.getInstance().createDeleteRequest(host, uri);

		logger.info(String.format("delete event type: '%1$s' -> '%2$d'", eventType.getName(), request.getResponseCode()));

		return new EventPlatformFeedbackImpl(request);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventPlatformFeedback registerEventQuery(long eventTypeId, EventQuery eventQuery) {
		if (eventQuery.getQuery() == null || eventQuery.getQuery().length() == 0) {
			return new EventPlatformFeedbackImpl("event query was empty", false);
		}

		if (eventQuery.getUuid() != null && eventQuery.getUuid().length() > 0) {
			deleteEventQuery(eventQuery);
		}

		String host = EventProcessingPlatformUpdater.getHost();
		String uri = EventProcessingPlatformUpdater.getRegisterEventQueryUri();
		String notificationPath = String.format("%1$s:%2$d/%3$s", Argos.getHost(), Argos.getPort(), EventReceiver.getReceiveEventUri(eventTypeId));

		RestRequest request = RestRequestFactoryImpl.getInstance().createPostRequest(host, uri);

		JsonObject requestBody = new JsonObject();
		requestBody.addProperty("notificationPath", notificationPath);
		requestBody.addProperty("queryString", eventQuery.getQuery());

		request.setContent(serializer.toJson(requestBody));

		logger.info(String.format("register event query: '%1$s' -> '%2$d'", eventQuery.getQuery(), request.getResponseCode()));

		if (request.isSuccessful()) {
			eventQuery.setUuid(request.getResponse());
			// TODO: update query in database
		}

		return new EventPlatformFeedbackImpl(request);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventPlatformFeedback deleteEventQuery(EventQuery eventQuery) {
		if (eventQuery.getUuid() == null || eventQuery.getUuid().length() == 0) {
			return new EventPlatformFeedbackImpl("event query was not registered yet", false);
		}

		String host = EventProcessingPlatformUpdater.getHost();
		String uri = EventProcessingPlatformUpdater.getDeleteEventQueryUri(eventQuery.getUuid());

		RestRequest request = RestRequestFactoryImpl.getInstance().createDeleteRequest(host, uri);

		logger.info(String.format("delete event query: '%1$s' -> '%2$d'", eventQuery.getUuid(), request.getResponseCode()));

		return new EventPlatformFeedbackImpl(request);
	}
}
