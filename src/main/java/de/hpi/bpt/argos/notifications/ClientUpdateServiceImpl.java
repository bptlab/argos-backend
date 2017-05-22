package de.hpi.bpt.argos.notifications;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.hpi.bpt.argos.notifications.socket.PushNotificationClientHandler;
import de.hpi.bpt.argos.notifications.socket.PushNotificationClientHandlerImpl;
import de.hpi.bpt.argos.storage.PersistenceAdapterImpl;
import de.hpi.bpt.argos.storage.PersistenceArtifactUpdateType;
import de.hpi.bpt.argos.storage.dataModel.PersistenceArtifact;
import de.hpi.bpt.argos.storage.dataModel.entity.Entity;
import de.hpi.bpt.argos.storage.dataModel.event.Event;
import spark.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class ClientUpdateServiceImpl implements ClientUpdateService {
	private static final Gson serializer = new Gson();
	private static final String IMPLEMENTATION_SUFFIX = "Impl";

	private PushNotificationClientHandler clientHandler;
	private Map<PersistenceArtifact, JsonObject> artifactUpdates;

	/**
	 * This constructor initializes all members with default values.
	 */
	public ClientUpdateServiceImpl() {
		clientHandler = new PushNotificationClientHandlerImpl();
		artifactUpdates = new HashMap<>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setup(Service sparkService) {
		clientHandler.setup(sparkService);
		PersistenceAdapterImpl.getInstance().subscribe(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onArtifactUpdated(PersistenceArtifactUpdateType updateType, PersistenceArtifact updatedArtifact, String fetchUri) {
		String artifactTypeName = updatedArtifact.getClass().getSimpleName();

		if (artifactTypeName.endsWith(IMPLEMENTATION_SUFFIX)) {
			artifactTypeName = artifactTypeName.substring(0, artifactTypeName.length() - IMPLEMENTATION_SUFFIX.length());
		}

		JsonObject notification = createBasicNotification(updateType, artifactTypeName, updatedArtifact.getId(), fetchUri);

		artifactUpdates.put(updatedArtifact, notification);
		sendArtifactUpdates();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onEventCreation(Entity eventOwner, Event event, String fetchUri) {
		JsonObject notification = createBasicNotification(PersistenceArtifactUpdateType.CREATE, Event.class.getSimpleName(), event.getId(), fetchUri);
		notification.addProperty("EventTypeId", event.getTypeId());
		notification.addProperty("EntityId", event.getEntityId());

		artifactUpdates.put(event, notification);
		sendArtifactUpdates();
	}

	/**
	 * This method sends all cached entity updates to the clients.
	 */
	private void sendArtifactUpdates() {
		// clone map to avoid threading problems
		Map<PersistenceArtifact, JsonObject> notifications = new HashMap<>(artifactUpdates);
		artifactUpdates.clear();

		JsonArray jsonNotifications = new JsonArray();

		for (Map.Entry<PersistenceArtifact, JsonObject> notification : notifications.entrySet()) {
			jsonNotifications.add(notification.getValue());
		}

		String json = serializer.toJson(jsonNotifications);
		clientHandler.sendNotification(json);
	}

	/**
	 * This method creates a basic push notification.
	 * @param updateType - the type of the notification
	 * @param artifactTypeName - the name of the updated artifact type
	 * @param artifactId - the id of the updated artifact
	 * @param fetchUri - the uri where to get the updated artifact
	 * @return - a basic push notification
	 */
	private JsonObject createBasicNotification(PersistenceArtifactUpdateType updateType, String artifactTypeName, long artifactId, String fetchUri) {
		JsonObject notification = new JsonObject();
		notification.addProperty("UpdateReason", updateType.toString());
		notification.addProperty("ArtifactType", artifactTypeName);
		notification.addProperty("ArtifactId", artifactId);
		notification.addProperty("FetchUri", fetchUri);

		return notification;
	}
}
