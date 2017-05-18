package de.hpi.bpt.argos.core;


import de.hpi.bpt.argos.properties.PropertyEditorImpl;

import java.util.Enumeration;

public final class Application {

	/**
	 * This constructor hides the implicit public one. This way this class can be instanced from any other class.
	 */
	private Application() {

	}

	/**
	 * Application start method.
	 * @param args - command line arguments
	 */
	public static void main(String[] args) {

		Enumeration systemProperties = System.getProperties().propertyNames();

		while (systemProperties.hasMoreElements()) {
			String propertyKey = (String) systemProperties.nextElement();
			String propertyValue = System.getProperty(propertyKey);

			PropertyEditorImpl.getInstance().setProperty(propertyKey, propertyValue);
		}

		(new ArgosImpl()).start();
	}
}
