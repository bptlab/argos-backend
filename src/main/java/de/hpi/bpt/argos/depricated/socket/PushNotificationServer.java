package de.hpi.bpt.argos.depricated.socket;

public interface PushNotificationServer extends Runnable {
	boolean setup(int port);

	boolean getKeepAlive();

	void setKeepAlive(boolean keepAlive);

	boolean isAlive();
}
