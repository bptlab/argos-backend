package de.hpi.bpt.argos.event_handling;

import de.hpi.bpt.argos.common.RestRequest;
import de.hpi.bpt.argos.common.RestRequestFactory;
import de.hpi.bpt.argos.common.RestRequestFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class EventSubscriberImpl implements EventSubscriber {
	private static final Logger logger = LoggerFactory.getLogger(EventSubscriberImpl.class);
	protected static final String DEFAULT_HOST = "http://localhost:8080";
	protected static final String DEFAULT_URI = "/Unicorn/webapi/REST/EventQuery/REST";

    /**
     * {@inheritDoc}
     */
	@Override
	public boolean subscribeToEventPlatform(String host, String uri, String eventQuery) {

		RestRequestFactory restFactory = new RestRequestFactoryImpl();
		RestRequest subscriptionRequest = restFactory.createPostRequest(host, uri);

		if (subscriptionRequest == null) {
			return false;
		}

		EventQuery query = new EventQueryImpl(eventQuery);
		subscriptionRequest.setContent(query.toJson());

		logInfoForSubscription(host, uri, query);

		logInfoForResponse(subscriptionRequest);
		return subscriptionRequest.isSuccessful();
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public boolean subscribeToEventPlatform(String eventQuery) {
		return subscribeToEventPlatform(DEFAULT_HOST, DEFAULT_URI, eventQuery);
	}

    /**
     * This method logs a given head on info level.
     * @param head - string to be logged
     */
	protected void logInfo(String head) {
		logger.info(head);
	}

    /**
     * This method logs an info for the response from the event processing platform.
     * @param request - RestRequest request to be analyzed.
     */
	protected void logInfoForResponse(RestRequest request) {
		logInfo("received response: " + request.getResponseCode() + " -> " + request.getResponse());
	}

    /**
     * This method logs an info for the sending of a subscription to the event processing platform.
     * @param host - the host that the subscription will be sent to
     * @param uri - the uri on the host that the subscription will be sent to
     * @param query - the query that will be registered
     */
	protected void logInfoForSubscription(String host, String uri, EventQuery query) {
		logInfo("sending subscription: (host)" + host + "    (uri) " + uri + "    (query) " + query.toJson());
	}
}
