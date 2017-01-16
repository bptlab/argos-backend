package de.hpi.bpt.argos.event_handling;

import de.hpi.bpt.argos.common.RestEndpointImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Service;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class EventReceiverImpl extends RestEndpointImpl implements EventReceiver {
	private static final Logger logger = LoggerFactory.getLogger(EventReceiverImpl.class);
	protected static final String POST_EVENT = "/api/events/receiver";

    /**
     * {@inheritDoc}
     */
	@Override
	public void setup(Service sparkService) {
		sparkService.post(POST_EVENT, this::receiveEvent);
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public String receiveEvent(Request request, Response response) {
		logInfoForReceivedEvent(request);

		return finishRequest();
	}


    /**
     * This message logs a given head on info level.
     * @param head - string to be logged
     */
    protected void logInfo(String head) {
		logger.info(head);
	}

    /**
     * This method logs an info for every received event.
     * @param request - request received from event processing platform
     */
	protected void logInfoForReceivedEvent(Request request) {
		logInfo("received event: (body) " + request.body() + "	(sender) " + request.ip());
	}
}
