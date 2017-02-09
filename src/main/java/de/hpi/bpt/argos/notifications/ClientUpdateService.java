package de.hpi.bpt.argos.notifications;

import com.google.gson.JsonObject;
import de.hpi.bpt.argos.notifications.socket.PushNotificationClientHandler;
import de.hpi.bpt.argos.persistence.database.PersistenceEntity;
import de.hpi.bpt.argos.persistence.database.PersistenceEntityManagerEventReceiver;
import spark.Service;

import java.time.Duration;
import java.util.Map;

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
	 * This method returns a map of entities to json objects which represents update notifications.
	 * @return - a map of entities to json objects which represents update notifications
	 */
	Map<PersistenceEntity, JsonObject> getEntityUpdates();

	/**
	 * This method returns the push notification client handler.
	 * @return - the push notification client handler
	 */
	PushNotificationClientHandler getPushNotificationClientHandler();

	/**
	 * This method removes all existing entity updates.
	 */
	void resetEntityUpdates();

	/**
	 * This method sends all cached entity updates to the clients.
	 */
	void sendEntityUpdates();

	/**
	 * This method returns the property key for the pushNotificationUpdateType property.
	 * @return - the property key for the pushNotificationUpdateType property
	 */
	static String getPushNotificationUpdateTypePropertyKey() {
		return "pushNotificationUpdateType";
	}

	/**
	 * This method returns the property key for the pushNotificationUpdatePeriod property.
	 * @return - the property key for the pushNotificationUpdatePeriod property
	 */
	static String getPushNotificationUpdatePeriodPropertyKey() {
		return "pushNotificationUpdatePeriodInMS";
	}
}
