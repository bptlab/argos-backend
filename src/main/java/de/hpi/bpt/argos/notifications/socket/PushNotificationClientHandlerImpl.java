package de.hpi.bpt.argos.notifications.socket;

import de.hpi.bpt.argos.util.LoggerUtilImpl;
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
	private static final long CLIENT_LOCK_TIME_OUT = 1000;
	private static final TimeUnit CLIENT_LOCK_TIME_UNIT = TimeUnit.MILLISECONDS;

	private List<Session> clients;
	private Lock clientsLock;

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
		sparkService.webSocket(PushNotificationClientHandler.getWebSocketBaseUri(), this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendNotification(String notification) {
		try {
			clientsLock.tryLock(CLIENT_LOCK_TIME_OUT, CLIENT_LOCK_TIME_UNIT);

			if (clients.isEmpty()) {
				logger.debug("no web socket clients to send notification to");
				logNotification(notification);
				return;
			}

			logger.info(String.format("sending web socket notification to %1$d client", clients.size()));
			logNotification(notification);

			for (Iterator<Session> it = clients.iterator(); it.hasNext();) {
				Session client = it.next();

				sendNotificationToClient(client, it, notification);
			}

		} catch (Exception e) {
			logErrorWhileTryingToAcquireClientsLock(e);

		} finally {
			clientsLock.unlock();
		}
	}

	/**
	 * This method sends a notification to a single client.
	 * @param client - the receiving client
	 * @param it - an iterator on the receiving client
	 * @param notification - the notification that the client should receive
	 */
	private void sendNotificationToClient(Session client, Iterator<Session> it, String notification) {
		try {
			if (!client.isOpen()) {
				it.remove();
				logger.debug(String.format("web socket client connection lost: '%1$s' -> %2$d clients left",
						client.getRemoteAddress().getHostString(),
						clients.size()));
				return;
			}

			client.getRemote().sendString(notification);

		} catch (Exception e) {
			LoggerUtilImpl.getInstance().error(logger, "cannot send notification to client", e);
			client.close();
			it.remove();
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

			logger.debug(String.format("new web socket client connected: '%1$s' -> %2$d total clients",
					client.getRemoteAddress().getHostString(),
					clients.size()));

		} catch (Exception e) {
			logErrorWhileTryingToAcquireClientsLock(e);

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

			logger.debug(String.format("web socket client disconnected: '%1$s' -> '%2$s' : '%3$s' -> %4$d clients left",
					client.getRemoteAddress().getHostString(),
					statusCode,
					reason,
					clients.size()));

		} catch (Exception e) {
			logErrorWhileTryingToAcquireClientsLock(e);

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
		//Is empty, since we don't react to the messages of the connected clients.
	}

	/**
	 * This method logs an exception which occurs while trying to acquire a lock for the clients list.
	 * @param exception - the thrown exception
	 */
	private void logErrorWhileTryingToAcquireClientsLock(Throwable exception) {
		LoggerUtilImpl.getInstance().error(logger,"cannot acquire client list lock", exception);
	}

	/**
	 * This method logs a message, which was sent over the web socket at trace-level.
	 * @param notification - the notification message to log
	 */
	private void logNotification(String notification) {
		logger.trace(String.format("web socket notification: '%1$s'", notification));
	}
}
