package de.hpi.bpt.argos.eventHandling;

import spark.Request;
import spark.Response;
import spark.Service;

public interface EventReceiver {
	void setup(Service sparkService);

	String receiveEvent(Request request, Response response);

	String finishRequest();
}
