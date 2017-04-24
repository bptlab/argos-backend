package de.hpi.bpt.argos.properties;

/**
 * This interface represents the property editor, which can be used to access and modify the property file.
 */
public interface PropertyEditor {

	/**
	 * This method returns a property value for a specific property key.
	 * @param propertyKey - the key for the property
	 * @return - the value for the property key
	 */
	String getProperty(String propertyKey);

	/**
	 * This method sets a value for a specific property. Caution: This will not be stored permanently. You need to do this for every restart.
	 * @param propertyKey - the key of the property to edit
	 * @param propertyValue - the new value for the property
	 */
	void setProperty(String propertyKey, String propertyValue);

	/**
	 * This method returns the properties file.
	 * @return - the name of the properties file
	 */
	static String getPropertyFile() {
		return "argos-backend.properties";
	}
}
