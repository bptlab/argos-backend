package de.hpi.bpt.argos.eventHandling;

import de.hpi.bpt.argos.common.RestEndpoint;
import spark.Request;
import spark.Response;

public interface EventReceiver extends RestEndpoint {
	String receiveEvent(Request request, Response response);
}
