package de.hpi.bpt.argos.eventHandling;

import de.hpi.bpt.argos.common.RestEndpointImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Service;

public class EventReceiverImpl extends RestEndpointImpl implements EventReceiver {
	private static final Logger logger = LoggerFactory.getLogger(EventReceiverImpl.class);
	protected static final String POST_EVENT = "/api/events/receiver";

	@Override
	public void setup(Service sparkService) {
		sparkService.post(POST_EVENT, this::receiveEvent);
	}

	@Override
	public String receiveEvent(Request request, Response response) {
		logInfoForReceivedEvent(request);

		return finishRequest();
	}

	protected void logInfo(String head) {
		logger.info(head);
	}

	protected void logInfoForReceivedEvent(Request request) {
		logInfo("received event: (body) " + request.body() + "	(sender) " + request.ip());
	}
}
