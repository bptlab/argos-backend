package de.hpi.bpt.argos.properties;

/**
 * This interface represents the property editor, which can be used to access and modify the property file.
 */
public interface PropertyEditor {

	String PROPERTIES_FILE = "argos-backend.properties";

	/**
	 * This method returns a property value for a specific property key.
	 * @param propertyKey - the key for the property
	 * @return - the value for the property key
	 */
	String getProperty(String propertyKey);

	/**
	 * This method returns a property value for a specific property key as boolean.
	 * @param propertyKey - the key for the property
	 * @return - the value for the property key as boolean
	 */
	boolean getPropertyAsBoolean(String propertyKey);

	/**
	 * This method returns a property value for a specific property key as int.
	 * @param propertyKey - the key for the property
	 * @return - the value for the property key as int
	 */
	int getPropertyAsInt(String propertyKey);

	/**
	 * This method returns a property value for a specific property key as long.
	 * @param propertyKey - the key for the property
	 * @return - the value for the property key as long
	 */
	long getPropertyAsLong(String propertyKey);

	/**
	 * This method sets a value for a specific property. Caution: This will not be stored permanently. You need to do this for every restart.
	 * @param propertyKey - the key of the property to edit
	 * @param propertyValue - the new value for the property
	 */
	void setProperty(String propertyKey, String propertyValue);
}
