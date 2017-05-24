package de.hpi.bpt.argos.common;

import de.hpi.bpt.argos.core.Argos;
import de.hpi.bpt.argos.core.ArgosTestParent;
import de.hpi.bpt.argos.properties.PropertyEditorImpl;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RestRequestFactoryTest extends ArgosTestParent {

	@BeforeClass
	public static void initialize() {
		ArgosTestParent.setup();
		PropertyEditorImpl.getInstance().setProperty(Argos.ARGOS_BACKEND_TEST_MODE_PROPERTY_KEY, Boolean.FALSE.toString());
	}

	@AfterClass
	public static void tearDown() {
		ArgosTestParent.tearDown();
	}

	@Test
	public void testIsReachable() {
		assertTrue(RestRequestFactoryImpl.getInstance().isReachable(ARGOS_REST_HOST));
	}

	@Test
	public void testIsReachable_InvalidHost_False() {
		String uri = "not_a_host";
		assertFalse(RestRequestFactoryImpl.getInstance().isReachable(uri));
	}

	@Test
	public void testIsReachable_UnreachableHost_False() {
		String uri = "http://localhost:0";
		assertFalse(RestRequestFactoryImpl.getInstance().isReachable(uri));
	}
}
