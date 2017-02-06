package de.hpi.bpt.argos.notifications.socket;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
@WebSocket
public class PushNotificationClientHandlerImpl implements PushNotificationClientHandler {
	private static final Logger logger = LoggerFactory.getLogger(PushNotificationClientHandlerImpl.class);
	protected static final long CLIENT_LOCK_TIME_OUT = 1000;
	protected static final TimeUnit CLIENT_LOCK_TIME_UNIT = TimeUnit.MILLISECONDS;

	protected List<Session> clients;
	protected Lock clientsLock;

	/**
	 * This constructor initializes all members with default values.
	 */
	public PushNotificationClientHandlerImpl() {
		clients = new ArrayList<>();
		clientsLock = new ReentrantLock();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setup(Service sparkService) {
		sparkService.webSocket(PushNotificationClientHandler.getWebSocketUriBase(), this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendNotification(String notification) {
		try {
			clientsLock.tryLock(CLIENT_LOCK_TIME_OUT, CLIENT_LOCK_TIME_UNIT);

			for (Iterator<Session> it = clients.iterator(); it.hasNext();) {
				Session client = it.next();

				try {
					if (!client.isOpen()) {
						it.remove();
						continue;
					}

					client.getRemote().sendString(notification);

				} catch (Exception exception) {
					logger.error("Cannot send notification to client", exception);
					client.close();
					it.remove();
				}
			}

		} catch (Exception exception) {
			logErrorWhileTryingToAcquireClientsLock(exception);

		} finally {
			clientsLock.unlock();

		}
	}

	/**
	 * {@inheritDoc}
	 */
	@OnWebSocketConnect
	@Override
	public void onClientConnected(Session client) {
		try {
			clientsLock.tryLock(CLIENT_LOCK_TIME_OUT, CLIENT_LOCK_TIME_UNIT);
			clients.add(client);

			logger.info("new web socket client connected: " + client.getRemoteAddress().getHostString());

		} catch (Exception exception) {
			logErrorWhileTryingToAcquireClientsLock(exception);

		} finally {
			clientsLock.unlock();

		}
	}

	/**
	 * {@inheritDoc}
	 */
	@OnWebSocketClose
	@Override
	public void onClientDisconnected(Session client, int statusCode, String reason) {
		try {
			clientsLock.tryLock(CLIENT_LOCK_TIME_OUT, CLIENT_LOCK_TIME_UNIT);
			clients.remove(client);

		} catch (Exception exception) {
			logErrorWhileTryingToAcquireClientsLock(exception);

		} finally {
			clientsLock.unlock();

		}
	}

	/**
	 * {@inheritDoc}
	 */
	@OnWebSocketMessage
	@Override
	public void onMessage(Session client, String message) {
		//Is empty, since we don't react to the messages of the connected client.
	}

	/**
	 * This method logs an exception which occurs while trying to acquire a lock for the clients list.
	 * @param exception - the thrown exception
	 */
	protected void logErrorWhileTryingToAcquireClientsLock(Throwable exception) {
		logger.error("can't acquire client list lock", exception);
	}
}
