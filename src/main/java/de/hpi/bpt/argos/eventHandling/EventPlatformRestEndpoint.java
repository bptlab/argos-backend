package de.hpi.bpt.argos.eventHandling;

import de.hpi.bpt.argos.persistence.database.DatabaseConnection;
import de.hpi.bpt.argos.persistence.database.PersistenceEntityManager;
import spark.Service;

/**
 * This interface represents a rest endpoint for receiving data from the event platform.
 */
public interface EventPlatformRestEndpoint {

	/**
	 * This method sets up all event platform rest endpoints and a client update service.
	 * @param entityManager - the entity manager to retrieve entities from
	 * @param sparkService - the spark service to register the rest routes to
	 */
	void setup(PersistenceEntityManager entityManager, Service sparkService);
}
