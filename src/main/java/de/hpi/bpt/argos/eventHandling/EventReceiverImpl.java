package de.hpi.bpt.argos.eventHandling;

import spark.Request;
import spark.Response;
import spark.Service;

public class EventReceiverImpl implements EventReceiver {
	protected final String EVENT_RECEIVER_ENDPOINT = "/api/events/receiver";
	protected final String REQUEST_HANDLED = "request handled.";

	@Override
	public void setup(Service sparkService) {
		sparkService.post(EVENT_RECEIVER_ENDPOINT, (request, response) -> receiveEvent(request, response));
	}

	@Override
	public String receiveEvent(Request request, Response response) {
		System.out.println(request.body());

		return finishRequest();
	}

	@Override
	public String finishRequest() {
		return REQUEST_HANDLED;
	}
}
