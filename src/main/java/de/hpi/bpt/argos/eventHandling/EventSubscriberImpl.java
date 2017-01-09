package de.hpi.bpt.argos.eventHandling;

import de.hpi.bpt.argos.common.RestRequest;
import de.hpi.bpt.argos.common.RestRequestFactory;
import de.hpi.bpt.argos.common.RestRequestFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventSubscriberImpl implements EventSubscriber {
	private static final Logger logger = LoggerFactory.getLogger(EventSubscriberImpl.class);
	protected static final String DEFAULT_HOST = "http://localhost:8080";
	protected static final String DEFAULT_URI = "/Unicorn/webapi/REST/EventQuery/REST";

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

	@Override
	public boolean subscribeToEventPlatform(String eventQuery) {
		return subscribeToEventPlatform(DEFAULT_HOST, DEFAULT_URI, eventQuery);
	}

	protected void logInfo(String head) {
		logger.info(head);
	}

	protected void logInfoForResponse(RestRequest request) {
		logInfo("received response: " + request.getResponseCode() + " -> " + request.getResponse());
	}

	protected void logInfoForSubscription(String host, String uri, EventQuery query) {
		logInfo("sending subscription: (host)" + host + "    (uri) " + uri + "    (query) " + query.toJson());
	}
}
