package de.hpi.bpt.argos.socket;

import java.net.Socket;

public interface PushNotificationClient {
	boolean isAuthenticated();

	boolean receive();

	void close();

	Socket getSocket();
}
