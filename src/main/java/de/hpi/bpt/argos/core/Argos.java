package de.hpi.bpt.argos.core;

import de.hpi.bpt.argos.properties.PropertyEditorImpl;

/**
 * This interface represents the argos backend application.
 */
public interface Argos {

	String ARGOS_BACKEND_HOST_PROPERTY_KEY = "argosBackendHost";
	String ARGOS_BACKEND_PORT_PROPERTY_KEY = "argosBackendPort";
	String ARGOS_BACKEND_ROUTE_PREFIX_PROPERTY_KEY = "argosBackendRoutePrefix";
	String ARGOS_BACKEND_THREADS_PROPERTY_KEY = "argosBackendThreads";
	String ARGOS_BACKEND_PUBLIC_FILES_PROPERTY_KEY = "argosBackendPublicFiles";
	String ARGOS_BACKEND_TEST_MODE_PROPERTY_KEY = "argosBackendTestMode";

	/**
	 * This method starts the argos backend.
	 */
	void start();

	/**
	 * This method stops the argos backend.
	 */
	void stop();

	/**
	 * This method reads the host property from the properties-file and returns it's value.
	 * @return - the host, specified in the properties-file
	 */
	static String getHost() {
		return PropertyEditorImpl.getInstance().getProperty(ARGOS_BACKEND_HOST_PROPERTY_KEY);
	}

	/**
	 * This method reads the port property from the properties-file and returns it's value.
	 * @return - the port, specified in the properties-file
	 */
	static int getPort() {
		return PropertyEditorImpl.getInstance().getPropertyAsInt(ARGOS_BACKEND_PORT_PROPERTY_KEY);
	}

	/**
	 * This method reads the routePrefix property from the properties-file and returns it's value.
	 * @return - the routePrefix, specified in the properties-file
	 */
	static String getRoutePrefix() {
		return PropertyEditorImpl.getInstance().getProperty(ARGOS_BACKEND_ROUTE_PREFIX_PROPERTY_KEY);
	}

	/**
	 * This method reads the threads property from the properties-file and returns it's value.
	 * @return - the threads, specified in the properties-file
	 */
	static int getThreads() {
		return PropertyEditorImpl.getInstance().getPropertyAsInt(ARGOS_BACKEND_THREADS_PROPERTY_KEY);
	}

	/**
	 * This method reads the publicFiles property from the properties-file and returns it's value.
	 * @return - the publicFiles, specified in the properties-file
	 */
	static String getPublicFiles() {
		return PropertyEditorImpl.getInstance().getProperty(ARGOS_BACKEND_PUBLIC_FILES_PROPERTY_KEY);
	}

	/**
	 * This method reads the testMode property from the properties-file and returns it's value.
	 * @return - the testMode, specified in the properties-file
	 */
	static boolean getTestMode() {
		return PropertyEditorImpl.getInstance().getPropertyAsBoolean(ARGOS_BACKEND_TEST_MODE_PROPERTY_KEY);
	}
}
