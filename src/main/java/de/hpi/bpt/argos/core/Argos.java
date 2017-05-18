package de.hpi.bpt.argos.core;

import de.hpi.bpt.argos.eventProcessing.EventCreationObserver;
import de.hpi.bpt.argos.eventProcessing.mapping.EventMappingObserver;
import de.hpi.bpt.argos.properties.PropertyEditorImpl;

/**
 * This interface represents the argos backend application.
 */
public interface Argos {

	String ARGOS_BACKEND_EXTERNAL_HOST_PROPERTY_KEY = "argosBackendExternalHost";
	String ARGOS_BACKEND_PORT_PROPERTY_KEY = "argosBackendPort";
	String ARGOS_BACKEND_ROUTE_PREFIX_PROPERTY_KEY = "argosBackendRoutePrefix";
	String ARGOS_BACKEND_THREADS_PROPERTY_KEY = "argosBackendThreads";
	String ARGOS_BACKEND_PUBLIC_FILES_PROPERTY_KEY = "argosBackendPublicFiles";
	String ARGOS_BACKEND_TEST_MODE_PROPERTY_KEY = "argosBackendTestMode";
	String ARGOS_BACKEND_ALLOWED_ORIGIN_PROPERTY_KEY = "argosBackendAllowedOrigin";
	String ARGOS_BACKEND_ALLOWED_REQUEST_METHOD_PROPERTY_KEY = "argosBackendAllowedRequestMethod";
	String ARGOS_BACKEND_EVENT_TYPES_DIRECTORY_PROPERTY_KEY = "argosBackendEventTypesDirectory";
	String ARGOS_BACKEND_STATIC_DATA_DIRECTORY_PROPERTY_KEY = "argosBackendStaticDataDirectory";
	String ARGOS_BACKEND_LOAD_STATIC_DATA_PROPERTY_KEY = "argosBackendLoadStaticData";
	String ARGOS_BACKEND_MEASURE_PERFORMANCE_PROPERTY_KEY = "argosBackendMeasurePerformance";

	/**
	 * This method starts the argos backend.
	 */
	void start();

	/**
	 * This method stops the argos backend.
	 */
	void stop();

	/**
	 * This method adds a new eventEntityMapper to the argos system. This will NOT override the default implementation, however it will be executed
	 * before the default behavior.
	 * @param mapper - the mapper to add
	 * @throws ArgosNotRunningException - thrown when the start method was not yet called
	 */
	void addEventEntityMapper(EventCreationObserver mapper) throws ArgosNotRunningException;

	/**
	 * This method removes a custom eventEntityMapper from the argos system.
	 * @param mapper - the mapper to remove
	 * @throws ArgosNotRunningException - thrown when the start method was not yet called
	 */
	void removeEventEntityMapper(EventCreationObserver mapper) throws ArgosNotRunningException;

	/**
	 * This method adds a new statusCalculator to the argos system. This will NOT override the default implementation, however it will be executed
	 * before the default behavior.
	 * @param statusCalculator - the statusCalculator to add
	 * @throws ArgosNotRunningException - thrown when the start method was not yet called
	 */
	void addEntityStatusCalculator(EventMappingObserver statusCalculator) throws ArgosNotRunningException;

	/**
	 * This method removes a custom statusCalculator from the argos system.
	 * @param statusCalculator - the statusCalculator to remove
	 * @throws ArgosNotRunningException - thrown when the start method was not yet called
	 */
	void removeEntityStatusCalculator(EventMappingObserver statusCalculator) throws ArgosNotRunningException;

	/**
	 * This method reads the host property from the properties-file and returns its value.
	 * @return - the host, specified in the properties-file
	 */
	static String getExternalHost() {
		return PropertyEditorImpl.getInstance().getProperty(ARGOS_BACKEND_EXTERNAL_HOST_PROPERTY_KEY);
	}

	/**
	 * This method reads the port property from the properties-file and returns its value.
	 * @return - the port, specified in the properties-file
	 */
	static int getPort() {
		return PropertyEditorImpl.getInstance().getPropertyAsInt(ARGOS_BACKEND_PORT_PROPERTY_KEY);
	}

	/**
	 * This method reads the routePrefix property from the properties-file and returns its value.
	 * @return - the routePrefix, specified in the properties-file
	 */
	static String getRoutePrefix() {
		return PropertyEditorImpl.getInstance().getProperty(ARGOS_BACKEND_ROUTE_PREFIX_PROPERTY_KEY);
	}

	/**
	 * This method reads the threads property from the properties-file and returns its value.
	 * @return - the threads, specified in the properties-file
	 */
	static int getThreads() {
		return PropertyEditorImpl.getInstance().getPropertyAsInt(ARGOS_BACKEND_THREADS_PROPERTY_KEY);
	}

	/**
	 * This method reads the publicFiles property from the properties-file and returns its value.
	 * @return - the publicFiles, specified in the properties-file
	 */
	static String getPublicFiles() {
		return PropertyEditorImpl.getInstance().getProperty(ARGOS_BACKEND_PUBLIC_FILES_PROPERTY_KEY);
	}

	/**
	 * This method reads the testMode property from the properties-file and returns its value.
	 * @return - the testMode, specified in the properties-file
	 */
	static boolean isInTestMode() {
		return PropertyEditorImpl.getInstance().getPropertyAsBoolean(ARGOS_BACKEND_TEST_MODE_PROPERTY_KEY);
	}

	/**
	 * This method reads the allowedOrigin property from the properties-file and returns its value.
	 * @return - the allowedOrigin, specified in the properties-file
	 */
	static String getAllowedOrigin() {
		return PropertyEditorImpl.getInstance().getProperty(ARGOS_BACKEND_ALLOWED_ORIGIN_PROPERTY_KEY);
	}

	/**
	 * This method reads the allowedRequestMethod property from the properties-file and returns its value.
	 * @return - the allowedRequestMethod, specified in the properties-file
	 */
	static String getAllowedRequestMethod() {
		return PropertyEditorImpl.getInstance().getProperty(ARGOS_BACKEND_ALLOWED_REQUEST_METHOD_PROPERTY_KEY);
	}

	/**
	 * This method reads the eventTypeDirectory property from the properties-file and returns its value.
	 * @return - the eventTypeDirectory, specified in the properties-file
	 */
	static String getEventTypesDirectory() {
		return PropertyEditorImpl.getInstance().getProperty(ARGOS_BACKEND_EVENT_TYPES_DIRECTORY_PROPERTY_KEY);
	}

	/**
	 * This method reads the staticDataDirectory property from the properties-file and returns its value.
	 * @return - the staticDataDirectory, specified in the properties-file
	 */
	static String getStaticDataDirectory() {
		return PropertyEditorImpl.getInstance().getProperty(ARGOS_BACKEND_STATIC_DATA_DIRECTORY_PROPERTY_KEY);
	}

	/**
	 * This method reads the loadStaticData property from the properties-file and returns its value.
	 * @return - the loadStaticData, specified in the properties-file
	 */
	static boolean shouldLoadStaticData() {
		return PropertyEditorImpl.getInstance().getPropertyAsBoolean(ARGOS_BACKEND_LOAD_STATIC_DATA_PROPERTY_KEY);
	}

	/**
	 * This method reads the measurePerformance property from the properties-file and returns its value.
	 * @return - the measurePerformance, specified in the properties-file
	 */
	static boolean shouldMeasurePerformance() {
		return PropertyEditorImpl.getInstance().getPropertyAsBoolean(ARGOS_BACKEND_MEASURE_PERFORMANCE_PROPERTY_KEY);
	}
}
