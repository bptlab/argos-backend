package de.hpi.bpt.argos.notifications;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.hpi.bpt.argos.notifications.socket.PushNotificationClientHandler;
import de.hpi.bpt.argos.notifications.socket.PushNotificationClientHandlerImpl;
import de.hpi.bpt.argos.storage.PersistenceAdapterImpl;
import de.hpi.bpt.argos.storage.PersistenceArtifactUpdateType;
import de.hpi.bpt.argos.storage.dataModel.PersistenceArtifact;
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
		JsonObject notification = new JsonObject();

		String artifactTypeName = updatedArtifact.getClass().getSimpleName();

		if (artifactTypeName.endsWith(IMPLEMENTATION_SUFFIX)) {
			artifactTypeName = artifactTypeName.substring(0, artifactTypeName.length() - IMPLEMENTATION_SUFFIX.length());
		}

		notification.addProperty("UpdateReason", updateType.toString());
		notification.addProperty("ArtifactType", artifactTypeName);
		notification.addProperty("ArtifactId", updatedArtifact.getId());
		notification.addProperty("FetchUri", fetchUri);

		artifactUpdates.put(updatedArtifact, notification);
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
}
