package de.hpi.bpt.argos.core;

import de.hpi.bpt.argos.eventHandling.EventSubscriber;
import de.hpi.bpt.argos.properties.PropertyEditor;
import de.hpi.bpt.argos.properties.PropertyEditorImpl;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class ArgosTestParent {
	protected static final int TEST_PORT = 9001;
	protected static final int TEST_NUMBER_OF_THREADS = 9;
	protected static final String TEST_HOST_ADDRESS = "localhost";
	protected static final String TEST_HOST = "http://" + TEST_HOST_ADDRESS + ":" + TEST_PORT;


	public static Argos argos;

	@BeforeClass
	public static void setUp() {
		argos = new ArgosImpl();

		argos.setTestMode(true);

		PropertyEditor propertyEditor = new PropertyEditorImpl();
		propertyEditor.setProperty(EventSubscriber.getEventPlatformHostPropertyKey(), "http://localhost:0"); // make event platform unreachable
		propertyEditor.setProperty(Argos.getArgosBackendLoadBackboneDataPropertyKey(), "false"); // do not load backbone data

		argos.run(TEST_PORT, TEST_NUMBER_OF_THREADS);

		ArgosTestUtil.setup(argos);
	}

	@AfterClass
	public static void tearDown() {
		argos.shutdown();
	}
}
