package de.hpi.bpt.argos.notifications;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.hpi.bpt.argos.notifications.socket.PushNotificationClientHandler;
import de.hpi.bpt.argos.notifications.socket.PushNotificationClientHandlerImpl;
import de.hpi.bpt.argos.persistence.database.PersistenceEntity;
import de.hpi.bpt.argos.properties.PropertyEditor;
import de.hpi.bpt.argos.properties.PropertyEditorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class ClientUpdateServiceImpl implements ClientUpdateService {
	protected static final Logger logger = LoggerFactory.getLogger(ClientUpdateServiceImpl.class);
	protected static final Gson serializer = new Gson();

	protected PushNotificationClientHandler clientHandler;
	protected Map<PersistenceEntity, JsonObject> entityUpdates;
	protected ScheduledExecutorService executorService;
	protected PushNotificationUpdateType updateType;

	/**
	 * This constructor initializes all members with default values.
	 */
	public ClientUpdateServiceImpl() {
		clientHandler = new PushNotificationClientHandlerImpl();
		entityUpdates = new HashMap<>();

		PropertyEditor propertyEditor = new PropertyEditorImpl();

		switch (propertyEditor.getProperty(ClientUpdateService.getPushNotificationUpdateTypePropertyKey())) {
			case "IMMEDIATE":
				updateType = PushNotificationUpdateType.IMMEDIATE;
				break;
			case "PERIOD":
				updateType = PushNotificationUpdateType.PERIOD;
				break;
			default:
				logger.error("cannot parse push notification update type");
				updateType = PushNotificationUpdateType.IMMEDIATE;
				break;
		}

		if (updateType == PushNotificationUpdateType.PERIOD) {

			String periodInMs = propertyEditor.getProperty(ClientUpdateService.getPushNotificationUpdatePeriodPropertyKey());
			Duration updatePeriod = Duration.ZERO;

			if (periodInMs != null && periodInMs.length() != 0) {
				try {
					updatePeriod = Duration.ofMillis(Long.parseLong(periodInMs));
				} catch (Exception e) {
					logger.error("cannot parse push notification update period", e);
				}
			}

			executorService = Executors.newScheduledThreadPool(1);
			executorService.scheduleAtFixedRate(new SendClientNotificationThread(this),
					updatePeriod.toMillis(),
					updatePeriod.toMillis(), TimeUnit.MILLISECONDS);
		}
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
		final String implementationSuffix = "Impl";

		JsonObject jsonUpdate = new JsonObject();

		String entityType = entity.getClass().getSimpleName();

		if (entityType.endsWith(implementationSuffix)) {
			entityType = entityType.substring(0, entityType.length() - implementationSuffix.length());
		}


		jsonUpdate.addProperty("updateReason", typeOfUpdate.toString());
		jsonUpdate.addProperty("entityType", entityType);
		jsonUpdate.addProperty("entityId", entity.getId());
		jsonUpdate.addProperty("dataFetchUri", fetchUri);

		entityUpdates.put(entity, jsonUpdate);

		if (updateType == PushNotificationUpdateType.IMMEDIATE) {
			sendEntityUpdates();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendEntityUpdates() {
		Map<PersistenceEntity, JsonObject> notifications = new HashMap<>(getEntityUpdates());
		resetEntityUpdates();

		JsonArray jsonNotifications = new JsonArray();

		for (Map.Entry<PersistenceEntity, JsonObject> notification : notifications.entrySet()) {
			jsonNotifications.add(notification.getValue());
		}

		String json = serializer.toJson(jsonNotifications);
		clientHandler.sendNotification(json);
	}

	protected class SendClientNotificationThread implements Runnable {
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
			clientUpdateService.sendEntityUpdates();
		}
	}
}
