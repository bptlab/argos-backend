package de.hpi.bpt.argos.eventHandling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Service;

public class EventReceiverImpl implements EventReceiver {
	private static final Logger logger = LoggerFactory.getLogger(EventReceiverImpl.class);
	protected static final String EVENT_RECEIVER_ENDPOINT = "/api/events/receiver";
	protected static final String REQUEST_HANDLED = "request handled.";

	@Override
	public void setup(Service sparkService) {
		sparkService.post(EVENT_RECEIVER_ENDPOINT, this::receiveEvent);
	}

	@Override
	public String receiveEvent(Request request, Response response) {
		logInfoForReceivedEvent(request);

		return finishRequest();
	}

	@Override
	public String finishRequest() {
		return REQUEST_HANDLED;
	}

	protected void logInfo(String head) {
		logger.info(head);
	}

	protected void logInfoForReceivedEvent(Request request) {
		logInfo("received event: (body) " + request.body() + "	(sender) " + request.ip());
	}
}
