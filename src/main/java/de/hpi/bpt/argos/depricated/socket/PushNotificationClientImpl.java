package de.hpi.bpt.argos.depricated.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class PushNotificationClientImpl implements PushNotificationClient {
	protected static final Logger logger = LoggerFactory.getLogger(PushNotificationClientImpl.class);

	protected Socket socket;
	protected boolean authenticated;

	public PushNotificationClientImpl(Socket socket) {
		authenticated = false;
		this.socket = socket;
	}

	@Override
	public boolean isAuthenticated() {
		return authenticated;
	}

	@Override
	public boolean receive() {
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			String message = "";
			String line = input.readLine();

			while (line != null && line.length() > 0) {
				message += line + "\n";
				line = input.readLine();
			}

			input.close();

			if (message.length() == 0) {
				return true;
			}

			receiveMessage(message);
		} catch (IOException e) {
			logExceptionWhileReceiving(e);
			return false;
		}

		return true;
	}

	@Override
	public void close() {
		if (socket.isConnected()) {
			try {
				socket.close();
			} catch (IOException e) {
				logExceptionWhileClosingSocket(e);
			}
		}
	}

	@Override
	public Socket getSocket() {
		return socket;
	}

	protected void receiveMessage(String message) {
		message = decryptMessage(message);

		logInfoReceivedMessage(message);

		// TODO: authenticate client

		authenticated = true;
	}

	protected String decryptMessage(String message) {
		// TODO: decrypt message symmetrically
		return message;
	}

	protected void logException(String head, Throwable exception) {
		logger.error(head, exception);
	}

	protected void logExceptionWhileClosingSocket(Throwable exception) {
		logException("can't close socket: ", exception);
	}

	protected void logExceptionWhileReceiving(Throwable exception) {
		logException("can't receive from socket: ", exception);
	}

	protected void logInfo(String head) {
		logger.info(head);
	}

	protected void logInfoReceivedMessage(String message) {
		logInfo("received message: " + message);
	}
}
