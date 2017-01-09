package de.hpi.bpt.argos.eventHandling;

import de.hpi.bpt.argos.common.RestRequest;
import de.hpi.bpt.argos.common.RestRequestFactory;
import de.hpi.bpt.argos.common.RestRequestFactoryImpl;

public class EventSubscriberImpl implements EventSubscriber {

	@Override
	public boolean subscribeToEventPlatform(String host, String uri, String eventQuery) {

		RestRequestFactory restFactory = new RestRequestFactoryImpl();
		RestRequest subscriptionRequest = restFactory.createPostRequest(host, uri);

		if (subscriptionRequest == null) {
			return false;
		}

		subscriptionRequest.setContent(eventQuery);

		System.out.println("response: " + subscriptionRequest.getResponseCode() + " -> " + subscriptionRequest.getResponse());

		return true;
	}
}
