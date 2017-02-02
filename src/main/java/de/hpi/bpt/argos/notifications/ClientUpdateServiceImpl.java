package de.hpi.bpt.argos.notifications;

import com.google.gson.JsonObject;
import de.hpi.bpt.argos.notifications.socket.PushNotificationClientHandler;
import de.hpi.bpt.argos.notifications.socket.PushNotificationClientHandlerImpl;
import de.hpi.bpt.argos.persistence.database.PersistenceEntity;
import spark.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class ClientUpdateServiceImpl implements ClientUpdateService {
	protected static final Duration DEFAULT_CLIENT_UPDATE_INTERVAL = Duration.ofSeconds(10);

	protected PushNotificationClientHandler clientHandler;
	protected Duration clientUpdateInterval;
	protected List<JsonObject> entityUpdates;

	/**
	 * This constructor initializes all members with default values.
	 */
	public ClientUpdateServiceImpl() {
		clientHandler = new PushNotificationClientHandlerImpl();
		clientUpdateInterval = DEFAULT_CLIENT_UPDATE_INTERVAL;
		entityUpdates = new ArrayList<>();
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
	public void setClientUpdateInterval(Duration interval) {
		clientUpdateInterval = interval;
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

		entityUpdates.add(jsonUpdate);
	}

	protected class SendClientNotificationThread extends TimerTask {

		/**
		 * This method get called periodically and sends update notifications to clients.
		 */
		@Override
		public void run() {
			//This functionality is not implemented yet.
		}
	}
}
