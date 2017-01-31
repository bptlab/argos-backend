package de.hpi.bpt.argos.depricated.socket;

public interface PushNotificationServerFactory {
	PushNotificationServer createServer(int port);

	PushNotificationServer createServer();
}
