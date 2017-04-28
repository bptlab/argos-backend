package de.hpi.bpt.argos.core;

import de.hpi.bpt.argos.common.EventProcessingPlatformUpdater;
import de.hpi.bpt.argos.properties.PropertyEditorImpl;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class ArgosTestParent {
	public static final int ARGOS_PORT = 9001;
	public static final String ARGOS_HOST_ADDRESS = "http://localhost";
	public static final String ARGOS_HOST = ARGOS_HOST_ADDRESS + ":" + ARGOS_PORT;
	public static final int ARGOS_THREADS = 8;

	private static Argos argos;

	@BeforeClass
	public static void setup() {
		argos = new ArgosImpl();

		PropertyEditorImpl.getInstance().setProperty(Argos.ARGOS_BACKEND_HOST_PROPERTY_KEY, ARGOS_HOST_ADDRESS);
		PropertyEditorImpl.getInstance().setProperty(Argos.ARGOS_BACKEND_PORT_PROPERTY_KEY, Integer.toString(ARGOS_PORT));
		PropertyEditorImpl.getInstance().setProperty(Argos.ARGOS_BACKEND_THREADS_PROPERTY_KEY, Integer.toString(ARGOS_THREADS));
		PropertyEditorImpl.getInstance().setProperty(Argos.ARGOS_BACKEND_TEST_MODE_PROPERTY_KEY, Boolean.TRUE.toString());
		PropertyEditorImpl.getInstance().setProperty(EventProcessingPlatformUpdater.EVENT_PROCESSING_PLATFORM_HOST_PROPERTY_KEY,
				"http://localhost:0"); // make event processing platform unreachable

		argos.start();
	}

	@AfterClass
	public static void tearDown() {
		argos.stop();
	}
}
