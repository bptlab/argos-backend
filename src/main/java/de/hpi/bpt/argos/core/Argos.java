package de.hpi.bpt.argos.core;

import de.hpi.bpt.argos.persistence.database.PersistenceEntityManager;
import de.hpi.bpt.argos.properties.PropertyEditor;
import de.hpi.bpt.argos.properties.PropertyEditorImpl;

/**
 * This interface is the Argos application and provides methods for Argos administration.
 */
public interface Argos {
    /**
     * This method starts the Argos application on the given port with a given number of threads.
     * @param port - port to be launched on as integer
     * @param numberOfThreads - number of threads to use to run Argos application as integer
     */
	void run(int port, int numberOfThreads);

    /**
     * This method starts the Argos application on a default port with a default number of threads.
     */
	void run();

    /**
     * This method shuts the Argos application down and frees the used port again.
     */
	void shutdown();

	/**
	 * This method sets the argos backend into test mode, or disables test mode.
	 * @param testMode - the test mode status to set
	 */
	void setTestMode(boolean testMode);

	/**
	 * This method returns the persistence entity manager of argos.
	 * @return - the persistence entity manager of argos
	 */
	PersistenceEntityManager getPersistenceEntityManager();

	/**
	 * This method returns the property key for the argosBackendPort property.
	 * @return - the property key for the argosBackendPort property
	 */
	static String getArgosBackendPortPropertyKey() {
		return "argosBackendPort";
	}

	/**
	 * This method returns the property key for the argosBackendThreads property.
	 * @return - the property key for the argosBackendThreads property
	 */
	static String getArgosBackendThreadsPropertyKey() {
		return "argosBackendThreads";
	}

	/**
	 * This method returns the property key for the argosBackendPublicFiles property.
	 * @return - the property key for the argosBackendPublicFiles property
	 */
	static String getArgosBackendPublicFilesPropertyKey() {
		return "argosBackendPublicFiles";
	}

	/**
	 * This method returns the property key for the argosBackendTestMode property.
	 * @return - the property key for the argosBackendTestMode property
	 */
	static String getArgosBackendTestModePropertyKey() {
		return "argosBackendTestMode";
	}

	/**
	 * This method returns the property key for the argosBackendEventTypeDirectory property.
	 * @return - the property key for the argosBackendEventTypeDirectory property
	 */
	static String getArgosBackendEventTypeDirectoryPropertyKey() {
		return "argosBackendEventTypeDirectory";
	}

	/**
	 * This method returns the port of the argos application as configured in the argos-backend.properties file.
	 * @return - the port of the argos application as configured in the argos-backend.properties file
	 */
	static int getPort() {
		PropertyEditor propertyReader = new PropertyEditorImpl();

		String port = propertyReader.getProperty(getArgosBackendPortPropertyKey());

		if (port.length() == 0) {
			return 0;
		} else {
			return Integer.parseInt(port);
		}
	}

	/**
	 * This method returns the number of threads of the argos application as configured in the argos-backend.properties file.
	 * @return - the number of threads of the argos application as configured in the argos-backend.properties file
	 */
	static int getThreads() {
		PropertyEditor propertyReader = new PropertyEditorImpl();

		String threads = propertyReader.getProperty(getArgosBackendThreadsPropertyKey());

		if (threads.length() == 0) {
			return 0;
		} else {
			return Integer.parseInt(threads);
		}
	}

	/**
	 * This method returns the host name of the argos application.
	 * @return - the host name of the argos application
	 */
	static String getHost() {
		return String.format("http://localhost:%1$d", getPort());
	}

	/**
	 * This method returns the property key for the corsAllowedOrigin property.
	 * @return - the property key for the corsAllowedOrigin property
	 */
	static String getCORSAllowedOriginPropertyKey() {
		return "corsAllowedOrigin";
	}

	/**
	 * This method returns the property key for the corsAllowedRequestMethod property.
	 * @return - the property key for the corsAllowedRequestMethod property
	 */
	static String getCORSAllowedRequestMethodPropertyKey() {
		return "corsAllowedRequestMethod";
	}
}
