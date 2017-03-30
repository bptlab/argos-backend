package de.hpi.bpt.argos.eventHandling;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import de.hpi.bpt.argos.common.RestRequest;
import de.hpi.bpt.argos.common.RestRequestFactory;
import de.hpi.bpt.argos.common.RestRequestFactoryImpl;
import de.hpi.bpt.argos.core.Argos;
import de.hpi.bpt.argos.persistence.database.PersistenceEntityManager;
import de.hpi.bpt.argos.persistence.model.event.EventQuery;
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
	public EventPlatformFeedback registerEventType(EventType eventType) {

		if (!eventType.shouldBeRegistered()) {
			return registerEventQuery(eventType);
		}

		PropertyEditor propertyReader = new PropertyEditorImpl();

		String eventPlatformHost = propertyReader.getProperty(EventSubscriber.getEventPlatformHostPropertyKey());
		String eventPlatformEventTypeUri = propertyReader.getProperty(EventSubscriber.getEventPlatformEventTypeUriPropertyKey());

		RestRequest createEventTypeRequest = restRequestFactory.createPostRequest(eventPlatformHost, eventPlatformEventTypeUri);

		if (createEventTypeRequest == null) {
			return new EventPlatformFeedbackImpl("cannot create request", false);
		}

		JsonObject requestContent = new JsonObject();
		requestContent.addProperty("xsd", eventType.getSchema());
		requestContent.addProperty("schemaName", eventType.getName());
		requestContent.addProperty("timestampName", eventType.getTimestampAttribute().getName());

		createEventTypeRequest.setContent(serializer.toJson(requestContent));

		logRestRequestInfo("register event type: ", createEventTypeRequest);

		if (!createEventTypeRequest.isSuccessful()) {
			return new EventPlatformFeedbackImpl(createEventTypeRequest.getResponse(), false);
		}

		return  registerEventQuery(eventType);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventPlatformFeedback deleteEventType(EventType eventType) {

		deleteEventQuery(eventType);

		PropertyEditor propertyReader = new PropertyEditorImpl();

		String eventPlatformHost = propertyReader.getProperty(EventSubscriber.getEventPlatformHostPropertyKey());
		String deleteEventTypeUri = EventSubscriber.getEventPlatformDeleteEventTypeUri(eventType.getName());

		RestRequest deleteEventTypeRequest = restRequestFactory.createDeleteRequest(eventPlatformHost, deleteEventTypeUri);

		logger.info(String.format("deleting event type '%1$s' -> Response Code %2$d", eventType.getName(),
				deleteEventTypeRequest.getResponseCode()));

		entityManager.deleteEntity(eventType);
		return new EventPlatformFeedbackImpl(deleteEventTypeRequest.getResponse(), true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventPlatformFeedback updateEventQuery(EventType eventType, String newQueryString) {

		String notificationPath = Argos.getHost() + EventReceiver.getReceiveEventUri(eventType.getId());

		return updateEventQuery(eventType.getEventQuery(), newQueryString, notificationPath);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventPlatformFeedback updateEventQuery(EventQuery eventQuery, String newQueryString, String notificationUri) {

		if (eventQuery == null) {
			return new EventPlatformFeedbackImpl("event query was null", false);
		}

		PropertyEditor propertyReader = new PropertyEditorImpl();

		String eventPlatformHost = propertyReader.getProperty(EventSubscriber.getEventPlatformHostPropertyKey());
		String updateEventQueryUri = EventSubscriber.getEventPlatformUpdateEventQueryUri(eventQuery.getUuid());

		RestRequest updateEventQueryRequest = restRequestFactory.createPutRequest(eventPlatformHost, updateEventQueryUri);

		JsonObject requestContent = new JsonObject();
		requestContent.addProperty("notificationPath", notificationUri);
		requestContent.addProperty("queryString", newQueryString);

		updateEventQueryRequest.setContent(serializer.toJson(requestContent));

		logger.info(String.format("updating event query '%1$s'. New event query '%2$s' -> Response Code %3$s",
				eventQuery.getQueryString(),
				newQueryString,
				updateEventQueryRequest.getResponseCode()));

		if (updateEventQueryRequest.isSuccessful()) {
			eventQuery.setQueryString(newQueryString);
			eventQuery.setUuid(updateEventQueryRequest.getResponse());
			entityManager.updateEntity(eventQuery);
		}

		return new EventPlatformFeedbackImpl(updateEventQueryRequest.getResponse(), updateEventQueryRequest.isSuccessful());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventPlatformFeedback registerEventQuery(EventType eventType) {

		if (eventType.getName().equals(EventType.getStatusUpdateEventTypeName())) {
			return new EventPlatformFeedbackImpl("no query to be registered", true);
		}

		PropertyEditor propertyReader = new PropertyEditorImpl();

		String eventPlatformHost = propertyReader.getProperty(EventSubscriber.getEventPlatformHostPropertyKey());
		String eventPlatformEventQueryUri = propertyReader.getProperty(EventSubscriber.getEventPlatformEventQueryUriPropertyKey());

		RestRequest subscriptionRequest = restRequestFactory.createPostRequest(eventPlatformHost, eventPlatformEventQueryUri);

		String notificationPath = Argos.getHost() + EventReceiver.getReceiveEventUri(eventType.getId());

		if (subscriptionRequest == null) {
			return new EventPlatformFeedbackImpl("cannot create request", false);
		}

		JsonObject requestContent = new JsonObject();
		requestContent.addProperty("notificationPath", notificationPath);
		requestContent.addProperty("queryString", eventType.getEventQuery().getQueryString());

		subscriptionRequest.setContent(serializer.toJson(requestContent));

		logRestRequestInfo("register event query: ", subscriptionRequest);

		if (!subscriptionRequest.isSuccessful()) {
			deleteEventType(eventType);
			return new EventPlatformFeedbackImpl(subscriptionRequest.getResponse(), false);
		}

		deleteEventQuery(eventType);

		eventType.getEventQuery().setUuid(subscriptionRequest.getResponse());
		entityManager.updateEntity(eventType);
		return new EventPlatformFeedbackImpl(subscriptionRequest.getResponse(), true);
	}

	/**
	 * This method deletes the event query for a given event type.
	 * @param eventType - the event type, for which the event query should be deleted
	 * @return - the feedback of the event platform
	 */
	protected EventPlatformFeedback deleteEventQuery(EventType eventType) {

		if (eventType.getEventQuery() == null || eventType.getEventQuery().getUuid().length() == 0) {
			return new EventPlatformFeedbackImpl("no event query given", false);
		}

		PropertyEditor propertyReader = new PropertyEditorImpl();

		String eventPlatformHost = propertyReader.getProperty(EventSubscriber.getEventPlatformHostPropertyKey());
		String deleteEventQueryUri = EventSubscriber.getEventPlatformDeleteEventQueryUri(eventType.getEventQuery().getUuid());

		RestRequest deleteEventQueryRequest = restRequestFactory.createDeleteRequest(eventPlatformHost, deleteEventQueryUri);

		logger.info(String.format("deleting event query '%1$s' -> Response Code %2$d",
				eventType.getEventQuery().getQueryString(),
				deleteEventQueryRequest.getResponseCode()));

		return new EventPlatformFeedbackImpl(deleteEventQueryRequest.getResponse(), deleteEventQueryRequest.isSuccessful());
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
