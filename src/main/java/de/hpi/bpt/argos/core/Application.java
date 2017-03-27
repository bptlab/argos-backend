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

		// edit properties if needed
		PropertyEditor propertyEditor = new PropertyEditorImpl();
		String argosHost = System.getProperty(Argos.getArgosBackendHostPropertyKey());
		String eventPlatformHost = System.getProperty(EventSubscriber.getEventPlatformHostPropertyKey());
		String databaseConnectionHost = System.getProperty(DatabaseConnection.getDatabaseConnectionHostPropertyKey());
		String databaseConnectionUser = System.getProperty(
				DatabaseConnection.getDatabaseConnectionUsernamePropertyKey());
		String databaseConnectionPassword = System.getProperty(
				DatabaseConnection.getDatabaseConnectionPasswordPropertyKey());

		String testMode = System.getProperty(Argos.getArgosBackendTestModePropertyKey());

		// TODO: validate input in a more sophisticated way
		if (argosHost != null && argosHost.length() > 0) {
			propertyEditor.setProperty(Argos.getArgosBackendHostPropertyKey(), argosHost);
		}

		if (eventPlatformHost != null && eventPlatformHost.length() > 0) {
			propertyEditor.setProperty(EventSubscriber.getEventPlatformHostPropertyKey(), eventPlatformHost);
		}

		if (databaseConnectionHost != null && databaseConnectionHost.length() > 0) {
			propertyEditor.setProperty(DatabaseConnection.getDatabaseConnectionHostPropertyKey(), databaseConnectionHost);
		}

		if (databaseConnectionUser != null && databaseConnectionUser.length() > 0) {
			propertyEditor.setProperty(DatabaseConnection.getDatabaseConnectionHostPropertyKey(), databaseConnectionHost);
		}

		if (databaseConnectionPassword != null && databaseConnectionPassword.length() > 0) {
			propertyEditor.setProperty(DatabaseConnection.getDatabaseConnectionPasswordPropertyKey(),
					databaseConnectionPassword);
		}

		if (testMode != null && testMode.length() > 0) {
			propertyEditor.setProperty(Argos.getArgosBackendTestModePropertyKey(), testMode);
			argos.setTestMode(Boolean.parseBoolean(testMode));
		}

		argos.run();
	}
}
