package de.hpi.bpt.argos.notifications.socket;

import de.hpi.bpt.argos.notifications.PushNotification;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Service;
import spark.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
@WebSocket
public class PushNotificationClientHandlerImpl implements PushNotificationClientHandler {
	private static final Logger logger = LoggerFactory.getLogger(PushNotificationClientHandlerImpl.class);
	protected static final String WEB_SOCKET_ROUTE = "/notifications";
	protected static final long CLIENT_LOCK_TIME_OUT = 1000;
	protected static final TimeUnit CLIENT_LOCK_TIME_UNIT = TimeUnit.MILLISECONDS;

	protected List<Session> clients;
	protected Lock clientsLock;

	public PushNotificationClientHandlerImpl() {
		clients = new ArrayList<>();
		clientsLock = new ReentrantLock();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setup(Service sparkService) {
		sparkService.webSocket(WEB_SOCKET_ROUTE, this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendNotification(PushNotification notification) {
		try {
			clientsLock.tryLock(CLIENT_LOCK_TIME_OUT, CLIENT_LOCK_TIME_UNIT);

			for (Session client : clients) {
				notification.sendTo(client);
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

	}

	/**
	 * This methods logs exceptions to the console.
	 * @param head - a description what happened
	 * @param exception - the thrown exception
	 */
	protected void logError(String head, Throwable exception) {
		logger.error(head, exception);
	}

	/**
	 * This method logs an exception which occurs while trying to acquire a lock for the clients list.
	 * @param exception - the thrown exception
	 */
	protected void logErrorWhileTryingToAcquireClientsLock(Throwable exception) {
		logError("can't acquire client list lock", exception);
	}
}
