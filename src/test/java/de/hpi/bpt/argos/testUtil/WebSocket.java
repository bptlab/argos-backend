package de.hpi.bpt.argos.testUtil;

import de.hpi.bpt.argos.core.ArgosTestParent;
import de.hpi.bpt.argos.notifications.socket.PushNotificationClientHandler;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public class WebSocket {
	private static final Logger logger = LoggerFactory.getLogger(WebSocket.class);

	private WebSocketClient client;
	private List<String> receivedMessages;

	public WebSocket() {
		receivedMessages = new ArrayList<>();
	}

	public static WebSocket buildWebSocket() throws Exception {
		WebSocket webSocket = new WebSocket();
		if (!webSocket.connectToServer()) {
			throw new Exception("web socket connection was not established");
		}

		return webSocket;
	}

	public boolean connectToServer() {
		try {
			URI uri = URI.create(
					String.format("ws://%1$s:%2$d%3$s",
							ArgosTestParent.ARGOS_HOST_ADDRESS,
							ArgosTestParent.ARGOS_PORT,
							PushNotificationClientHandler.getWebSocketBaseUri())
			);

			client = new WebSocketClient();

			client.start();
			WebSocketAdapter webSocketAdapter = new WebSocketAdapter() {
				@Override
				public void onWebSocketText(String message) {
					super.onWebSocketText(message);

					receivedMessages.add(message);
				}
			};

			Future<Session> fut = client.connect(webSocketAdapter, uri);
			Session session = fut.get();

			return session.isOpen();
		} catch (Exception e) {
			logger.error("cannot establish web socket connection", e);
			return false;
		}
	}

	public List<String> awaitMessages(int expectedMessages, long timeoutInMs) throws Exception {
		long startMs = System.currentTimeMillis();

		while (receivedMessages.size() < expectedMessages) {
			Thread.sleep(50);

			if (receivedMessages.size() >= expectedMessages) {
				break;
			}

//			if (System.currentTimeMillis() >= startMs + timeoutInMs) {
//				throw new Exception("timeout passed");
//			}
		}

		if (receivedMessages.size() > expectedMessages) {
			throw new Exception("more than expected messages received");
		}

		return receivedMessages;
	}
}
