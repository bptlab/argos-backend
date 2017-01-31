package de.hpi.bpt.argos.notifications.socket;

import de.hpi.bpt.argos.notifications.PushNotification;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import spark.Service;
import spark.Session;

import java.util.ArrayList;
import java.util.List;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
@WebSocket
public class PushNotificationClientHandlerImpl implements PushNotificationClientHandler {

	protected List<Session> clients;

	public PushNotificationClientHandlerImpl() {
		clients = new ArrayList<>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setup(Service sparkService) {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendNotification(PushNotification notification) {

	}
}
