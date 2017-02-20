package de.hpi.bpt.argos.eventHandling;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import de.hpi.bpt.argos.api.eventTypes.EventTypeEndpoint;
import de.hpi.bpt.argos.common.RestRequest;
import de.hpi.bpt.argos.common.RestRequestFactory;
import de.hpi.bpt.argos.common.RestRequestFactoryImpl;
import de.hpi.bpt.argos.core.Argos;
import de.hpi.bpt.argos.persistence.database.PersistenceEntityManager;
import de.hpi.bpt.argos.persistence.model.event.EventSubscriptionQueryImpl;
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

	@Override
	public void setupEventPlatform() {

		List<EventType> eventTypes = entityManager.getEventTypes();

		for (EventType eventType : eventTypes) {
			registerEventType(eventType);
		}
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public boolean registerEventType(EventType eventType) {

		PropertyEditor propertyReader = new PropertyEditorImpl();

		String eventPlatformHost = propertyReader.getProperty(EventSubscriber.getEventPlatformHostPropertyKey());
		String eventPlatformEventTypeUri = propertyReader.getProperty(EventSubscriber.getEventPlatformEventTypeUriPropertyKey());

		RestRequest createEventTypeRequest = restRequestFactory.createPostRequest(eventPlatformHost, eventPlatformEventTypeUri);

		if (createEventTypeRequest == null) {
			return false;
		}

		JsonObject requestContent = new JsonObject();
		requestContent.addProperty("xsd", eventType.getSchema());
		requestContent.addProperty("schemaName", eventType.getName());
		requestContent.addProperty("timestampName", eventType.getTimestampAttribute().getName());

		createEventTypeRequest.setContent(serializer.toJson(requestContent));

		logRestRequestInfo("register event type: ", createEventTypeRequest);

		if (!createEventTypeRequest.isSuccessful()) {
			return false;
		}

		return registerEventQuery(eventType);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean deleteEventType(EventType eventType) {

		deleteEventQuery(eventType);

		PropertyEditor propertyReader = new PropertyEditorImpl();

		String eventPlatformHost = propertyReader.getProperty(EventSubscriber.getEventPlatformHostPropertyKey());
		String deleteEventTypeUri = EventSubscriber.getEventPlatformDeleteEventTypeUri(eventType.getName());

		RestRequest deleteEventTypeRequest = restRequestFactory.createDeleteRequest(eventPlatformHost, deleteEventTypeUri);

		logger.info(String.format("deleting event type '%1$s' -> Response Code %2$d", eventType.getName(),
				deleteEventTypeRequest.getResponseCode()));

		entityManager.deleteEntity(eventType);
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean updateEventQuery(EventType eventType, String eventQuery) {

		if (eventType.getEventSubscriptionQuery() == null ||
			eventType.getEventSubscriptionQuery().getUuid() == null ||
			eventType.getEventSubscriptionQuery().getUuid().length() == 0)
		{
			return false;
		}

		PropertyEditor propertyReader = new PropertyEditorImpl();

		String eventPlatformHost = propertyReader.getProperty(EventSubscriber.getEventPlatformHostPropertyKey());
		String updateEventQueryUri = EventSubscriber.getEventPlatformUpdateEventQueryUri(eventType.getEventSubscriptionQuery().getUuid());

		RestRequest updateEventQueryRequest = restRequestFactory.createPutRequest(eventPlatformHost, updateEventQueryUri);

		String notificationPath = Argos.getHost() + EventReceiver.getPostEventUri(eventType.getId());

		JsonObject requestContent = new JsonObject();
		requestContent.addProperty("notificationPath", notificationPath);
		requestContent.addProperty("queryString", eventQuery);

		updateEventQueryRequest.setContent(serializer.toJson(requestContent));

		logger.info(String.format("updating event query for event type '%1$s'. New event query '%2$s' -> Response Code %3$s",
				eventType.getName(),
				eventQuery,
				updateEventQueryRequest.getResponseCode()));

		if (updateEventQueryRequest.isSuccessful()) {
			eventType.getEventSubscriptionQuery().setQueryString(eventQuery);
		}

		return updateEventQueryRequest.isSuccessful();
	}

	/**
	 * This method subscribes to the default event platform using an EventSubscriptionQuery.
	 * @param eventType - the event type which contains the event subscription query
	 * @return - true if subscription was successful
	 */
	protected boolean registerEventQuery(EventType eventType) {

		PropertyEditor propertyReader = new PropertyEditorImpl();

		String eventPlatformHost = propertyReader.getProperty(EventSubscriber.getEventPlatformHostPropertyKey());
		String eventPlatformEventQueryUri = propertyReader.getProperty(EventSubscriber.getEventPlatformEventQueryUriPropertyKey());

		RestRequest subscriptionRequest = restRequestFactory.createPostRequest(eventPlatformHost, eventPlatformEventQueryUri);

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
			deleteEventType(eventType);
			return false;
		}

		eventType.getEventSubscriptionQuery().setUuid(subscriptionRequest.getResponse());
		entityManager.updateEntity(eventType);
		return true;
	}

	/**
	 * This method deletes the event subscription query for a given event type.
	 * @param eventType - the event type, for which the event subscription query should be deleted
	 */
	protected void deleteEventQuery(EventType eventType) {
		PropertyEditor propertyReader = new PropertyEditorImpl();

		String eventPlatformHost = propertyReader.getProperty(EventSubscriber.getEventPlatformHostPropertyKey());
		String deleteEventQueryUri = EventSubscriber.getEventPlatformDeleteEventQueryUri(eventType.getEventSubscriptionQuery().getUuid());

		RestRequest deleteEventQueryRequest = restRequestFactory.createDeleteRequest(eventPlatformHost, deleteEventQueryUri);

		logger.info(String.format("deleting event query '%1$s' -> Response Code %2$d",
				eventType.getEventSubscriptionQuery().getQueryString(),
				deleteEventQueryRequest.getResponseCode()));

		entityManager.deleteEntity(eventType.getEventSubscriptionQuery());
		eventType.setEventSubscriptionQuery(new EventSubscriptionQueryImpl());
		entityManager.updateEntity(eventType, EventTypeEndpoint.getEventTypeUri(eventType.getId()));
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
