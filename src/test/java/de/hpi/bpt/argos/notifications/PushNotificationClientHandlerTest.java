package de.hpi.bpt.argos.notifications;

import de.hpi.bpt.argos.core.ArgosTestParent;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.junit.AfterClass;
import org.junit.Test;

import java.net.URI;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;

public class PushNotificationClientHandlerTest extends ArgosTestParent {
	protected static final String WEB_SOCKET_TEST_URI = "ws://" + TEST_HOST_ADDRESS + ":" + TEST_PORT + "/notifications";

	protected static WebSocketClient client;

	@Test
	public void connectToServer() throws Exception {
		URI uri = URI.create(WEB_SOCKET_TEST_URI);
		client = new WebSocketClient();

		client.start();
		WebSocketAdapter webSocket = new WebSocketAdapter();

		Future<Session> fut = client.connect(webSocket,uri);
		Session session = fut.get();

		assertEquals(session.isOpen(), true);

		session.close();
	}

	@AfterClass
	public static void closeConnection() throws Exception {
		client.stop();
	}
}
