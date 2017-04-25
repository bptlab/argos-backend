package de.hpi.bpt.argos.properties;

import de.hpi.bpt.argos.util.LoggerUtilImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class PropertyEditorImpl implements PropertyEditor {
	private static final Logger logger = LoggerFactory.getLogger(PropertyEditorImpl.class);

	private static PropertyEditor instance;
	private Map<String, String> properties;

	/**
	 * This constructor initializes all members with their default values.
	 */
	private PropertyEditorImpl() {
		properties = new HashMap<>();
		loadProperties();
	}

	/**
	 * This method returns the singleton instance of this class.
	 * @return - the singleton instance of this class
	 */
	public static PropertyEditor getInstance() {
		if (instance == null) {
			instance = new PropertyEditorImpl();
		}

		return instance;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getProperty(String propertyKey) {
		return properties.get(propertyKey);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean getPropertyAsBoolean(String propertyKey) {
		try {
			return Boolean.parseBoolean(getProperty(propertyKey));
		} catch (Exception e) {
			LoggerUtilImpl.getInstance().error(logger, String.format("cannot parse property value to boolean: '%1$s'", getProperty(propertyKey)), e);
			return false;
		}
	}

	@Override
	public int getPropertyAsInt(String propertyKey) {
		try {
			return Integer.parseInt(getProperty(propertyKey));
		} catch (Exception e) {
			LoggerUtilImpl.getInstance().error(logger, String.format("cannot parse property value to int: '%1$s'", getProperty(propertyKey)), e);
			return 0;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setProperty(String propertyKey, String propertyValue) {
		properties.put(propertyKey, propertyValue);
	}

	/**
	 * This method reads the properties file and stores the content in the property member.
	 */
	private void loadProperties() {
		Properties props = new Properties();

		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(PropertyEditor.PROPERTIES_FILE);

		if (inputStream != null) {
			try {
				props.load(inputStream);
			} catch (IOException e) {
				LoggerUtilImpl.getInstance().error(logger, "cannot read from property file", e);
			}
		} else {
			logger.error(String.format("cannot find property file '%1$s'.", PropertyEditor.PROPERTIES_FILE));
		}

		for (Object key : props.keySet()) {
			properties.put(key.toString(), props.getProperty(key.toString()));
		}
	}
}
