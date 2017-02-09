package de.hpi.bpt.argos.core;

import de.hpi.bpt.argos.eventHandling.EventSubscriber;
import de.hpi.bpt.argos.persistence.database.DatabaseConnection;
import de.hpi.bpt.argos.properties.PropertyEditor;
import de.hpi.bpt.argos.properties.PropertyEditorImpl;

/**
 * The main class that will be called on start of the application. Creates new argos.
 */
public class Application {
	/**
	 * default java main method.
	 * @param args - default parameter
	 */
	public static void main(String[] args) {
		Argos argos = new ArgosImpl();

		// edit properties if needed
		PropertyEditor propertyEditor = new PropertyEditorImpl();
		String eventPlatformHost = System.getProperty(EventSubscriber.getEventPlatformHostPropertyKey());
		String databaseConnectionHost = System.getProperty(DatabaseConnection.getDatabaseConnectionHostPropertyKey());

		// TODO: validate input in a more sophisticated way
		if (eventPlatformHost != null && eventPlatformHost.length() > 0) {
			propertyEditor.setProperty(EventSubscriber.getEventPlatformHostPropertyKey(), eventPlatformHost);
		}

		if (databaseConnectionHost != null && databaseConnectionHost.length() > 0) {
			propertyEditor.setProperty(DatabaseConnection.getDatabaseConnectionHostPropertyKey(), databaseConnectionHost);
		}

		argos.run();
	}
}
