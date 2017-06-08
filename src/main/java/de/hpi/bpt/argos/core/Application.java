package de.hpi.bpt.argos.core;


import de.hpi.bpt.argos.properties.PropertyEditorImpl;

public final class Application {

	private static final String PROPERTY_SEPARATOR = "=";
	private static final int ARG_SPLIT_LENGTH = 2;

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

		for (String arg : args) {
			String[] splitArg = arg.split(PROPERTY_SEPARATOR);

			if (splitArg.length != ARG_SPLIT_LENGTH) {
				continue;
			}

			PropertyEditorImpl.getInstance().setProperty(splitArg[0], splitArg[1]);
		}

		Argos argos = ArgosImpl.run();
		// you may add custom mappings and custom status logic here
	}
}
