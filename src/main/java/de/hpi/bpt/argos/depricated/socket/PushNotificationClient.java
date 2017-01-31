package de.hpi.bpt.argos.depricated.socket;

import java.net.Socket;

public interface PushNotificationClient {
	boolean isAuthenticated();

	boolean receive();

	void close();

	Socket getSocket();
}
