package de.hpi.bpt.argos.notifications;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.hpi.bpt.argos.notifications.socket.PushNotificationClientHandler;
import de.hpi.bpt.argos.notifications.socket.PushNotificationClientHandlerImpl;
import de.hpi.bpt.argos.persistence.database.PersistenceEntity;
import spark.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class ClientUpdateServiceImpl implements ClientUpdateService {
	protected static final Duration DEFAULT_CLIENT_UPDATE_INTERVAL = Duration.ofSeconds(10);

	protected PushNotificationClientHandler clientHandler;
	protected Duration clientUpdateInterval;
	protected Map<PersistenceEntity, JsonObject> entityUpdates;
	protected ScheduledExecutorService executorService;

	/**
	 * This constructor initializes all members with default values.
	 */
	public ClientUpdateServiceImpl() {
		clientHandler = new PushNotificationClientHandlerImpl();
		clientUpdateInterval = DEFAULT_CLIENT_UPDATE_INTERVAL;
		entityUpdates = new HashMap<>();
		executorService = Executors.newScheduledThreadPool(1);
		executorService.scheduleAtFixedRate(new SendClientNotificationThread(this),
				DEFAULT_CLIENT_UPDATE_INTERVAL.toMillis(),
				DEFAULT_CLIENT_UPDATE_INTERVAL.toMillis(),
				TimeUnit.MILLISECONDS);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setup(Service sparkService) {
		clientHandler.setup(sparkService);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<PersistenceEntity, JsonObject> getEntityUpdates() {
		return entityUpdates;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PushNotificationClientHandler getPushNotificationClientHandler() {
		return clientHandler;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void resetEntityUpdates() {
		// TODO: this might cause problems, since this method is often called from another thread
		entityUpdates.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onEntityModified(PushNotificationType typeOfUpdate, PersistenceEntity entity, String fetchUri) {
		JsonObject jsonUpdate = new JsonObject();

		jsonUpdate.addProperty("updateReason", typeOfUpdate.toString());
		jsonUpdate.addProperty("entityType", entity.getClass().getName());
		jsonUpdate.addProperty("entityId", entity.getId());
		jsonUpdate.addProperty("dataFetchUri", fetchUri);

		entityUpdates.put(entity, jsonUpdate);
	}

	protected class SendClientNotificationThread implements Runnable {
		protected final Gson serializer = new Gson();

		protected ClientUpdateService clientUpdateService;

		/**
		 * This method initializes the client update service.
		 * @param updateService - the client update service to be set
		 */
		public SendClientNotificationThread(ClientUpdateService updateService) {
			this.clientUpdateService = updateService;
		}

		/**
		 * This method get called periodically and sends update notifications to clients.
		 */
		@Override
		public void run() {
			Map<PersistenceEntity, JsonObject> notifications = clientUpdateService.getEntityUpdates();
			clientUpdateService.resetEntityUpdates();

			JsonArray jsonNotifications = new JsonArray();

			for (Map.Entry<PersistenceEntity, JsonObject> notification : notifications.entrySet()) {
				jsonNotifications.add(notification.getValue());
			}

			String json = serializer.toJson(jsonNotifications);
			clientUpdateService.getPushNotificationClientHandler().sendNotification(json);
		}
	}
}
