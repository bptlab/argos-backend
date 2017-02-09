package de.hpi.bpt.argos.eventHandling;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import de.hpi.bpt.argos.common.RestRequest;
import de.hpi.bpt.argos.common.RestRequestFactory;
import de.hpi.bpt.argos.common.RestRequestFactoryImpl;
import de.hpi.bpt.argos.core.Argos;
import de.hpi.bpt.argos.persistence.database.PersistenceEntityManager;
import de.hpi.bpt.argos.persistence.model.event.type.EventType;
import de.hpi.bpt.argos.properties.PropertyEditor;
import de.hpi.bpt.argos.properties.PropertyEditorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class EventSubscriberImpl implements EventSubscriber {
	private static final Logger logger = LoggerFactory.getLogger(EventSubscriberImpl.class);
	protected static final Gson serializer = new GsonBuilder().disableHtmlEscaping().create();
	protected static final RestRequestFactory restRequestFactory = new RestRequestFactoryImpl();

	protected PersistenceEntityManager entityManager;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setup(PersistenceEntityManager entityManager) {
		this.entityManager = entityManager;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setupEventPlatform(String host, String eventTypeUri, String eventQueryUri) {
		entityManager.createDefaultEventTypes();
		List<EventType> eventTypes = entityManager.getEventTypes();

		for (EventType eventType : eventTypes) {
			if (registerEventType(host, eventTypeUri, eventType)) {
				registerEventQuery(host, eventQueryUri, eventType);
			}
		}
	}

	@Override
	public void setupEventPlatform() {

		PropertyEditor propertyReader = new PropertyEditorImpl();

		String eventPlatformHost = propertyReader.getProperty(EventSubscriber.getEventPlatformHostPropertyKey());
		String eventPlatformEventQueryUri = propertyReader.getProperty(EventSubscriber.getEventPlatformEventQueryUriPropertyKey());
		String eventPlatformEventTypeUri = propertyReader.getProperty(EventSubscriber.getEventPlatformEventTypeUriPropertyKey());

		setupEventPlatform(eventPlatformHost, eventPlatformEventTypeUri, eventPlatformEventQueryUri);
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public boolean registerEventType(String host, String uri, EventType eventType) {
		RestRequest createEventTypeRequest = restRequestFactory.createPostRequest(host, uri);

		if (createEventTypeRequest == null) {
			return false;
		}

		JsonObject requestContent = new JsonObject();
		requestContent.addProperty("xsd", eventType.getSchema());
		requestContent.addProperty("schemaName", eventType.getName());
		requestContent.addProperty("timestampName", eventType.getTimestampAttribute().getName());

		createEventTypeRequest.setContent(serializer.toJson(requestContent));

		logRestRequestInfo("register event type: ", createEventTypeRequest);
		return createEventTypeRequest.isSuccessful();
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public boolean registerEventType(EventType eventType) {

		PropertyEditor propertyReader = new PropertyEditorImpl();

		String eventPlatformHost = propertyReader.getProperty(EventSubscriber.getEventPlatformHostPropertyKey());
		String eventPlatformEventTypeUri = propertyReader.getProperty(EventSubscriber.getEventPlatformEventTypeUriPropertyKey());

		return registerEventType(eventPlatformHost, eventPlatformEventTypeUri, eventType);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean registerEventQuery(String host, String uri, EventType eventType) {
		RestRequest subscriptionRequest = restRequestFactory.createPostRequest(host, uri);

		String notificationPath = Argos.getHost() + EventReceiver.getPostEventUri(eventType.getId());

		if (subscriptionRequest == null) {
			return false;
		}

		JsonObject requestContent = new JsonObject();
		requestContent.addProperty("notificationPath", notificationPath);
		requestContent.addProperty("queryString", eventType.getEventSubscriptionQuery().getQueryString());

		subscriptionRequest.setContent(serializer.toJson(requestContent));

		logRestRequestInfo("register event query: ", subscriptionRequest);

		if (!subscriptionRequest.isSuccessful()) {
			return false;
		}

		eventType.getEventSubscriptionQuery().setUuid(subscriptionRequest.getResponse());
		entityManager.updateEntity(eventType);
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean registerEventQuery(EventType eventType) {

		PropertyEditor propertyReader = new PropertyEditorImpl();

		String eventPlatformHost = propertyReader.getProperty(EventSubscriber.getEventPlatformHostPropertyKey());
		String eventPlatformEventQueryUri = propertyReader.getProperty(EventSubscriber.getEventPlatformEventQueryUriPropertyKey());

		return registerEventQuery(eventPlatformHost, eventPlatformEventQueryUri, eventType);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEventTypeRegistered(String host, String uri, EventType eventType) {
		RestRequest getRequest = restRequestFactory.createGetRequest(host, String.format("%1$s/%2$s", uri, eventType.getName()));

		if (getRequest == null) {
			return false;
		}

		return getRequest.getResponseCode() == RestRequest.getHttpSuccessCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEventTypeRegistered(EventType eventType) {

		PropertyEditor propertyReader = new PropertyEditorImpl();

		String eventPlatformHost = propertyReader.getProperty(EventSubscriber.getEventPlatformHostPropertyKey());
		String eventPlatformEventTypeUri = propertyReader.getProperty(EventSubscriber.getEventPlatformEventTypeUriPropertyKey());

		return isEventTypeRegistered(eventPlatformHost, eventPlatformEventTypeUri, eventType);
	}

	/**
	 * This method logs an info when sending rest requests.
	 * @param head - the title of the log message
	 * @param request - the rest request to log
	 */
	protected void logRestRequestInfo(String head, RestRequest request) {
		logger.info(String.format("%1$s : (request) %2$s -> (response) %3$d : %4$s",
				head,
				request.getContent(),
				request.getResponseCode(),
				request.getResponse()));
	}
}
