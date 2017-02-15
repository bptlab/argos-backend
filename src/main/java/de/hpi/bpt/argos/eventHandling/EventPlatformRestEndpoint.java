package de.hpi.bpt.argos.eventHandling;

import com.google.gson.JsonObject;
import de.hpi.bpt.argos.api.response.ResponseFactory;
import de.hpi.bpt.argos.persistence.database.PersistenceEntityManager;
import de.hpi.bpt.argos.persistence.model.event.type.EventType;
import spark.Service;

/**
 * This interface represents a rest endpoint for receiving data from the event platform.
 */
public interface EventPlatformRestEndpoint {

	/**
	 * This method sets up all event platform rest endpoints and a client update service.
	 * @param entityManager - the entity manager to retrieve entities from
	 * @param sparkService - the spark service to register the rest routes to
	 * @param responseFactory - the response factory
	 */
	void setup(PersistenceEntityManager entityManager, Service sparkService, ResponseFactory responseFactory);

	/**
	 * This method returns the event receiver.
	 * @return - the event receiver
	 */
	EventReceiver getEventReceiver();

	/**
	 * This method returns the event subscriber.
	 * @return - the event subscriber
	 */
	EventSubscriber getEventSubscriber();
}
