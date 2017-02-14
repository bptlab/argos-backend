package de.hpi.bpt.argos.core;

import org.junit.AfterClass;
import org.junit.BeforeClass;

public class ArgosTestParent {
	protected static final int TEST_PORT = 9001;
	protected static final int TEST_NUMBER_OF_THREADS = 9;
	protected static final String TEST_HOST_ADDRESS = "localhost";
	protected static final String TEST_HOST = "http://" + TEST_HOST_ADDRESS + ":" + TEST_PORT;


	protected static Argos argos;

	@BeforeClass
	public static void setUp() {
		argos = new ArgosImpl();
		argos.run(TEST_PORT, TEST_NUMBER_OF_THREADS);
	}

	@AfterClass
	public static void tearDown() {
		argos.shutdown();
	}
}
