package de.hpi.bpt.argos.core;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import de.hpi.bpt.argos.common.EventProcessingPlatformUpdater;
import de.hpi.bpt.argos.notifications.ClientUpdateService;
import de.hpi.bpt.argos.properties.PropertyEditorImpl;

public class ArgosTestParent {
	public static final int ARGOS_PORT = 9001;
	public static final String ARGOS_HOST_ADDRESS = "localhost";
	public static final String ARGOS_REST_HOST = "http://" + ARGOS_HOST_ADDRESS + ":" + ARGOS_PORT;
	public static final int ARGOS_THREADS = 9;

	protected static final Gson serializer = new Gson();
	protected static final JsonParser jsonParser = new JsonParser();

	protected static Argos argos;

	public static void setup() {
		setup(0);
	}

	public static void setup(long notificationInterval) {
		argos = new ArgosImpl();

		PropertyEditorImpl.getInstance().setProperty(Argos.ARGOS_BACKEND_EXTERNAL_HOST_PROPERTY_KEY, ARGOS_REST_HOST);
		PropertyEditorImpl.getInstance().setProperty(Argos.ARGOS_BACKEND_PORT_PROPERTY_KEY, Integer.toString(ARGOS_PORT));
		PropertyEditorImpl.getInstance().setProperty(Argos.ARGOS_BACKEND_THREADS_PROPERTY_KEY, Integer.toString(ARGOS_THREADS));
		PropertyEditorImpl.getInstance().setProperty(Argos.ARGOS_BACKEND_TEST_MODE_PROPERTY_KEY, Boolean.TRUE.toString());
		PropertyEditorImpl.getInstance().setProperty(Argos.ARGOS_BACKEND_LOAD_EVENT_TYPES_PROPERTY_KEY, Boolean.FALSE.toString());
		PropertyEditorImpl.getInstance().setProperty(Argos.ARGOS_BACKEND_LOAD_STATIC_DATA_PROPERTY_KEY, Boolean.FALSE.toString());
		PropertyEditorImpl.getInstance().setProperty(EventProcessingPlatformUpdater.EVENT_PROCESSING_PLATFORM_HOST_PROPERTY_KEY,
				"http://localhost:0"); // make event processing platform unreachable
		PropertyEditorImpl.getInstance().setProperty(ClientUpdateService.ARGOS_NOTIFICATION_SERVICE_INTERVAL_PROPERTY_KEY, String.valueOf(notificationInterval));

		argos.start();
	}

	public static void tearDown() {
		argos.stop();
	}
}
