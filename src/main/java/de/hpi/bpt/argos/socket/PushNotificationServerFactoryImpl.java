package de.hpi.bpt.argos.socket;

public class PushNotificationServerFactoryImpl implements PushNotificationServerFactory {
	protected static final int DEFAULT_PORT = 9898;

	@Override
	public PushNotificationServer createServer(int port) {
		PushNotificationServer newServer = new PushNotificationServerImpl();
		if (!newServer.setup(port)) {
			return null;
		}

		(new Thread(newServer)).start();
		return newServer;
	}

	@Override
	public PushNotificationServer createServer() {
		return createServer(DEFAULT_PORT);
	}
}
