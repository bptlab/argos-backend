package de.hpi.bpt.argos.notifications;

import de.hpi.bpt.argos.properties.PropertyEditorImpl;
import de.hpi.bpt.argos.storage.PersistenceArtifactUpdateObserver;
import spark.Service;

/**
 * This interface represents services, which are responsible to update clients whenever data changes.
 */
public interface ClientUpdateService extends PersistenceArtifactUpdateObserver {

	String ARGOS_NOTIFICATION_SERVICE_INTERVAL_PROPERTY_KEY = "argosNotificationServiceInterval";

	/**
	 * This method sets up a web socket route for clients to register.
	 * @param sparkService - the spark service the web socket should be registered for
	 */
	void setup(Service sparkService);

	/**
	 * This method reads the notificationInterval property from the properties-file and returns its value.
	 * @return - the notificationInterval, specified in the properties-file
	 */
	static long getNotificationInterval() {
		return PropertyEditorImpl.getInstance().getPropertyAsLong(ARGOS_NOTIFICATION_SERVICE_INTERVAL_PROPERTY_KEY);
	}
}
