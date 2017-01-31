package de.hpi.bpt.argos.notifications.socket;

import de.hpi.bpt.argos.notifications.PushNotification;
import spark.Service;

/**
 * This interface represents client handler, which serve to send push notifications.
 */
public interface PushNotificationClientHandler {

	/**
	 * This method sets up the push notification client handler.
	 * @param sparkService - the spark service to register the web socket to
	 */
	void setup(Service sparkService);

			   /**
	 * This method sends a push notification to all connected clients.
	 * @param notification - the notification so send
	 */
	void sendNotification(PushNotification notification);
}
