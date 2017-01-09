package de.hpi.bpt.argos.socket;

public interface PushNotificationServerFactory {
	PushNotificationServer createServer(int port);

	PushNotificationServer createServer();
}
