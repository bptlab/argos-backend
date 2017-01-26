package de.hpi.bpt.argos.eventHandling;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.hpi.bpt.argos.common.RestRequest;
import de.hpi.bpt.argos.common.RestRequestFactory;
import de.hpi.bpt.argos.common.RestRequestFactoryImpl;
import de.hpi.bpt.argos.persistence.database.DatabaseConnection;
import de.hpi.bpt.argos.persistence.model.event.EventSubscriptionQuery;
import de.hpi.bpt.argos.persistence.model.event.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class EventSubscriberImpl implements EventSubscriber {
	private static final Logger logger = LoggerFactory.getLogger(EventSubscriberImpl.class);
	protected static final Gson serializer = new Gson();

	protected static final RestRequestFactory restRequestFactory = new RestRequestFactoryImpl();
	protected static final String DEFAULT_HOST = "http://localhost:8080";
	protected static final String DEFAULT_EVENT_QUERY_URI = "/Unicorn/webapi/REST/EventQuery/REST";
	protected static final String DEFAULT_EVENT_TYPE_URI = "/Unicorn/webapi/REST/EventType";
	protected static final String EVENT_NOTIFICATION_PATH = "http://localhost:8989/api/events/receiver";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setupEventPlatform(String host, String eventUri, String queryUri, DatabaseConnection databaseConnection) {
		// TODO: get all event types and register them in event platform
		List<EventType> eventTypes = null;

		// TODO: catch occuring errors
		for(EventType eventType : eventTypes) {
			if (registerEventType(host, eventUri, eventType)) {
				registerEventQuery(host, eventUri, eventType);
			}
		}
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

		return createEventTypeRequest.isSuccessful();
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public boolean registerEventType(EventType eventType) {
		return registerEventType(DEFAULT_HOST, DEFAULT_EVENT_TYPE_URI, eventType);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean registerEventQuery(String host, String uri, EventType eventType) {
		RestRequest subscriptionRequest = restRequestFactory.createGetRequest(host, uri);

		if (subscriptionRequest == null) {
			return false;
		}

		JsonObject requestContent = new JsonObject();
		requestContent.addProperty("notificationPath", String.format("%1$s/%2$s", EVENT_NOTIFICATION_PATH, eventType.getId()));
		requestContent.addProperty("queryString", eventType.getEventSubscriptionQuery().getQueryString());

		if (!subscriptionRequest.isSuccessful()) {
			return false;
		}

		eventType.getEventSubscriptionQuery().setUuid(subscriptionRequest.getResponse());
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean registerEventQuery(EventType eventType) {
		return registerEventQuery(DEFAULT_HOST, DEFAULT_EVENT_QUERY_URI, eventType);
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

		return getRequest.getResponseCode() == 200;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEventTypeRegistered(EventType eventType) {
		return isEventTypeRegistered(DEFAULT_HOST, DEFAULT_EVENT_TYPE_URI, eventType);
	}
}
