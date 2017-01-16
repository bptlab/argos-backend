package de.hpi.bpt.argos.core;

/**
 * The main class that will be called on start of the application. Creates new argos.
 */
public class Application {
	public static void main(String[] args) {
		Argos argos = new ArgosImpl();
		argos.run();
	}
}
