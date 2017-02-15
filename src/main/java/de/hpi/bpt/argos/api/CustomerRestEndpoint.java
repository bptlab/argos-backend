package de.hpi.bpt.argos.api;

import de.hpi.bpt.argos.api.response.ResponseFactory;
import de.hpi.bpt.argos.persistence.database.PersistenceEntityManager;
import spark.Service;

/**
 * This interface represents a container for all rest endpoints which are offered for customers (like the frontend).
 */
@FunctionalInterface
public interface CustomerRestEndpoint {

	/**
	 * This method sets up all customer rest endpoints.
	 * @param entityManager - the entity manager to access persistence entities
	 * @param sparkService - the spark service to register the rest routes to
	 * @param responseFactory - the response factory
	 */
	void setup(PersistenceEntityManager entityManager, Service sparkService, ResponseFactory responseFactory);
}
