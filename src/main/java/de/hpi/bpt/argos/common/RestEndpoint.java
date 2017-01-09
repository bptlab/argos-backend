package de.hpi.bpt.argos.common;

import spark.Service;

public interface RestEndpoint {
	void setup(Service sparkService);

	String finishRequest();
}
