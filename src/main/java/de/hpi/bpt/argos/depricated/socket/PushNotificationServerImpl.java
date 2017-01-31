package de.hpi.bpt.argos.depricated.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class PushNotificationServerImpl implements PushNotificationServer {
	protected static final Logger logger = LoggerFactory.getLogger(PushNotificationServerImpl.class);

	protected ServerSocket socket;
	protected ClientHandler clientHandler;
	protected boolean keepAlive;

	public PushNotificationServerImpl() {
		clientHandler = new ClientHandler();
	}

	@Override
	public boolean setup(int port) {
		setKeepAlive(true);

		try {
			socket = new ServerSocket(port);
		} catch (IOException e) {
			logExceptionWhileCreatingSocket(e);
			setKeepAlive(false);
			return false;
		}

		(new Thread(clientHandler)).start();
		logInfoServerStarted();
		return true;
	}

	@Override
	public void run() {
		while (keepAlive) {
			try {
				Socket newClient = socket.accept();
				clientHandler.addClient(newClient);
				logInfoAcceptedClient(newClient);
			} catch (IOException e) {
				logExceptionWhileAcceptingClient(e);
			}
		}

		clientHandler.setKeepAlive(false);
	}

	@Override
	public boolean getKeepAlive() {
		return keepAlive;
	}

	@Override
	public boolean isAlive() {
		return !socket.isClosed();
	}

	@Override
	public void setKeepAlive(boolean keepAlive) {
		this.keepAlive = keepAlive;
		clientHandler.setKeepAlive(keepAlive);
	}

	protected void logException(String head, Throwable exception) {
		logger.error(head, exception);
	}

	protected void logExceptionWhileCreatingSocket(Throwable exception) {
		logException("can't open server socket: ", exception);
	}

	protected void logExceptionWhileAcceptingClient(Throwable exception) {
		logException("can't accept client: ", exception);
	}

	protected void logInfo(String head) {
		logger.info(head);
	}

	protected void logInfoServerStarted() {
		logInfo("notification server started: " + socket.getLocalSocketAddress().toString());
	}

	protected void logInfoAcceptedClient(Socket clientSocket) {
		logInfo("new client accepted: " + clientSocket.getInetAddress().toString());
	}





	protected class ClientHandler implements Runnable {
		protected final Logger logger = LoggerFactory.getLogger(ClientHandler.class);

		protected List<PushNotificationClient> clients;
		protected boolean keepAlive;

		public ClientHandler() {
			clients = new ArrayList<>();
		}

		@Override
		public void run() {
			keepAlive = true;
			while(keepAlive) {

				while (clients.isEmpty()) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						logExceptionWhileThreadPause(e);
					}
				}

				for(int i = 0; i < clients.size(); i++) {
					PushNotificationClient client = clients.get(i);

					if (client.getSocket().isClosed()) {
						logInfoClientConnectionClosed(client);
						clients.remove(i);
						i--;
						continue;
					}

					if (client.isAuthenticated()) {
						continue;
					}

					if (client.receive() && !client.isAuthenticated()) {
						// client received value but is still not authenticated => drop client
						logInfoClientDropped(client);
						client.close();
						clients.remove(i);
						i--;
					}
				}
			}

			for(PushNotificationClient client : clients) {
				client.close();
			}

			clients.clear();
		}

		public void setKeepAlive(boolean keepAlive) {
			this.keepAlive = keepAlive;
		}

		public void addClient(Socket client) {
			clients.add(new PushNotificationClientImpl(client));
			logInfo("new client added - " + client.getInetAddress().toString());
		}

		protected void logException(String head, Throwable exception) {
			logger.error(head, exception);
		}

		protected void logExceptionWhileThreadPause(Throwable exception) {
			logException("error while pausing thread: ", exception);
		}


		protected void logInfo(String head) {
			logger.info(head);
		}

		protected void logInfoClientDropped(PushNotificationClient client) {
			logInfo("dropped client: " + client.getSocket().getInetAddress().toString());
		}

		protected void logInfoClientConnectionClosed(PushNotificationClient client) {
			logInfo("client connection closed: " + client.getSocket().getInetAddress().toString());
		}
	}
}
