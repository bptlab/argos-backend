package de.hpi.bpt.argos.core;


import de.hpi.bpt.argos.properties.PropertyEditorImpl;

import java.util.Enumeration;

public class Application {

	/**
	 * Application start method.
	 * @param args - command line arguments
	 */
	public static void main(String[] args) {

		Enumeration systemProperties = System.getProperties().propertyNames();

		while (systemProperties.hasMoreElements()) {
			String propertyKey = (String) systemProperties.nextElement();
			PropertyEditorImpl.getInstance().setProperty(propertyKey, System.getProperty(propertyKey));
		}

		(new ArgosImpl()).start();
	}
}
