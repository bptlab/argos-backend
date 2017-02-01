package de.hpi.bpt.argos.notifications;

import de.hpi.bpt.argos.persistence.database.PersistenceEntityManagerEventReceiver;
import spark.Service;

import java.time.Duration;

/**
 * This interface represents services, which are responsible to update clients whenever data changes.
 */
public interface ClientUpdateService extends PersistenceEntityManagerEventReceiver {

	/**
	 * This method sets up a web socket route for clients to register.
	 * @param sparkService - the spark service the web socket should be registered for
	 */
	void setup(Service sparkService);

	/**
	 * This method sets the interval between sending notifications, if there are any, to connected clients.
	 * @param interval - the interval between notifications
	 */
	void setClientUpdateInterval(Duration interval);

}
