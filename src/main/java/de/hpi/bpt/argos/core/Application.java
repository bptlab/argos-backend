package de.hpi.bpt.argos.core;

import de.hpi.bpt.argos.eventHandling.EventSubscriber;
import de.hpi.bpt.argos.persistence.database.DatabaseConnection;
import de.hpi.bpt.argos.properties.PropertyEditor;
import de.hpi.bpt.argos.properties.PropertyEditorImpl;

/**
 * The main class that will be called on start of the application. Creates new argos.
 */
public final class Application {

	/**
	 * This method informs the user that this class should not be instantiated, as it is a utility class.
	 */
	private Application() {
		throw new IllegalAccessError("Utility class");
	}

	/**
	 * default java main method.
	 * @param args - default parameter
	 */
	public static void main(String[] args) {
		Argos argos = new ArgosImpl();

		PropertyEditor propertyEditor = updateProperties();

		if (isTestMode()) {
			argos.setTestMode(true);
			propertyEditor.setProperty(Argos.getArgosBackendTestModePropertyKey(), "true");
		}

		argos.run();
	}

	/**
	 * Updates all properties given from the command line and returns a property editor.
	 * @return - property editor
	 */
	private static PropertyEditor updateProperties() {

		PropertyEditor propertyEditor = new PropertyEditorImpl();

		// edit properties if needed
		String argosHost = System.getProperty(Argos.getArgosBackendHostPropertyKey());
		String eventPlatformHost = System.getProperty(EventSubscriber.getEventPlatformHostPropertyKey());
		String databaseConnectionHost = System.getProperty(DatabaseConnection.getDatabaseConnectionHostPropertyKey());
		String databaseConnectionUser = System.getProperty(
				DatabaseConnection.getDatabaseConnectionUsernamePropertyKey());
		String databaseConnectionPassword = System.getProperty(
				DatabaseConnection.getDatabaseConnectionPasswordPropertyKey());

		if (isPropertySet(argosHost)) {
			propertyEditor.setProperty(Argos.getArgosBackendHostPropertyKey(), argosHost);
		}

		if (isPropertySet(eventPlatformHost)) {
			propertyEditor.setProperty(EventSubscriber.getEventPlatformHostPropertyKey(), eventPlatformHost);
		}
		if (isPropertySet(databaseConnectionHost)) {
			propertyEditor.setProperty(DatabaseConnection.getDatabaseConnectionHostPropertyKey(), databaseConnectionHost);
		}

		if (isPropertySet(databaseConnectionUser)) {
			propertyEditor.setProperty(DatabaseConnection.getDatabaseConnectionUsernamePropertyKey(),
					databaseConnectionUser);
		}

		if (isPropertySet(databaseConnectionPassword)) {
			propertyEditor.setProperty(DatabaseConnection.getDatabaseConnectionPasswordPropertyKey(),
					databaseConnectionPassword);
		}
		return propertyEditor;
	}

	/**
	 * Checks if a property is set from a command line argument correctly.
	 * @param property - the property to verify
	 * @return - boolean if property is set
	 */
	private static boolean isPropertySet(String property) {
		return property != null && property.length() > 0;
	}

	/**
	 * Checks if the system is in test mode.
	 * @return - boolean if the system is in test mode.
	 */
	private static boolean isTestMode() {
		String testMode = System.getProperty(Argos.getArgosBackendTestModePropertyKey());
		return ("true".equals(testMode) || "false".equals(testMode)) && Boolean.parseBoolean(testMode);
	}
}
