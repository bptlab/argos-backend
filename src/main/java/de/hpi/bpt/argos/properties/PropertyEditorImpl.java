package de.hpi.bpt.argos.properties;

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

	private static PropertyEditorImpl instance;
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
	public static PropertyEditorImpl getInstance() {
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
	public void setProperty(String propertyKey, String propertyValue) {
		properties.put(propertyKey, propertyValue);
	}

	/**
	 * This method reads the properties file and stores the content in the property member.
	 */
	private void loadProperties() {
		Properties props = new Properties();

		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(PropertyEditor.getPropertyFile());

		if (inputStream != null) {
			try {
				props.load(inputStream);
			} catch (IOException e) {
				logger.error("cannot read from property file");
				logger.trace("Reason: ", e);
			}
		} else {
			logger.error(String.format("cannot find property file '%1$s'.", PropertyEditor.getPropertyFile()));
		}

		for (Object key : props.keySet()) {
			properties.put(key.toString(), props.getProperty(key.toString()));
		}
	}
}
