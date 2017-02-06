package de.hpi.bpt.argos.notifications.socket;

import org.eclipse.jetty.websocket.api.Session;
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
	void sendNotification(String notification);

	/**
	 * This method is called whenever a new client connected to the web socket. This method gets called by the Spark framework.
	 * @param client - the new client
	 */
	void onClientConnected(Session client);

	/**
	 * This method is called whenever a client disconnected from the web socket. This method gets called by the Spark framework.
	 * @param client - the disconnected client
	 * @param statusCode - the status code
	 * @param reason - the reason for the disconnect
	 */
	void onClientDisconnected(Session client, int statusCode, String reason);

	/**
	 * This method is called whenever a message from a client is received. This methid gets called by the Spark framework.
	 * @param client - the sender of the message
	 * @param message - the message
	 */
	void onMessage(Session client, String message);

	/**
	 * This method returns the base uri for the web socket notifications.
	 * @return - the base uri for the web socket notifications
	 */
	static String getWebSocketUriBase() {
		return "/notifications";
	}
}
