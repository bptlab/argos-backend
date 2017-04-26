package de.hpi.bpt.argos.common;

import spark.Service;

/**
 * This interface defines the setup method for all endpoint classes.
 */
@FunctionalInterface
public interface RestEndpoint {
    /**
     * This method sets up the rest endpoint.
     * @param sparkService - the spark service to register routes to
     */
    void setup(Service sparkService);
}
