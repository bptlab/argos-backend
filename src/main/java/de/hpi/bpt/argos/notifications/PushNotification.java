package de.hpi.bpt.argos.notifications;

import spark.Session;

/**
 * This interface represents push notification for the clients to get notified about data changes.
 */
public interface PushNotification {

	/**
	 * This method sends this notification to a specific client.
	 * @param client - the receiver of this notification
	 */
	void sendTo(Session client);
}
