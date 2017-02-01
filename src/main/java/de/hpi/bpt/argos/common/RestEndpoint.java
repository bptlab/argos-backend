package de.hpi.bpt.argos.common;

import de.hpi.bpt.argos.api.response.ResponseFactory;
import de.hpi.bpt.argos.persistence.database.PersistenceEntityManager;
import spark.Service;

/**
 * This interface is a base class for endpoint implementations, it provides setup and finishing methods.
 */
public interface RestEndpoint {

	/**
	 * This method sets up the rest endpoint.
	 * @param responseFactory - the factory to retrieve responses from
	 * @param entityManager - the entity manager to access persistence entities
	 * @param sparkService - the spark service to register routes to
	 */
	void setup(ResponseFactory responseFactory, PersistenceEntityManager entityManager, Service sparkService);
}
