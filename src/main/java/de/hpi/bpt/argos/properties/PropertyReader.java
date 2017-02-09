package de.hpi.bpt.argos.properties;

/**
 * This interface represents an object which is able to read properties from the argos-backend.properties file.
 */
public interface PropertyReader {

	/**
	 * This method returns a property value for a specific property key.
	 * @param propertyKey - the key for the property
	 * @return - the value for the property key
	 */
	String getProperty(String propertyKey);

	/**
	 * This method returns the properties file.
	 * @return - the name of the properties file
	 */
	static String getPropertyFile() {
		return "argos-backend.properties";
	}
}
