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
	protected static final Logger logger = LoggerFactory.getLogger(PropertyEditorImpl.class);

	protected static Map<String, String> propertyMap;

	/**
	 * This constructor loads the properties from the disk.
	 */
	public PropertyEditorImpl() {
		loadProperties();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getProperty(String propertyKey) {
		return propertyMap.get(propertyKey);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setProperty(String propertyKey, String propertyValue) {
		propertyMap.put(propertyKey, propertyValue);
	}

	/**
	 * This method loads all properties from the disk.
	 */
	protected void loadProperties() {
		if (propertyMap != null) {
			return;
		}

		propertyMap = new HashMap<>();

		Properties properties = new Properties();

		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(PropertyEditor.getPropertyFile());

		if (inputStream != null) {
			try {
				properties.load(inputStream);
			} catch (IOException e) {
				logger.error("cannot read from property file");
				logger.trace("Reason: ", e);
			}
		} else {
			logger.error(String.format("cannot find property file '%1$s'.", PropertyEditor.getPropertyFile()));
		}

		for (Object key : properties.keySet()) {
			propertyMap.put(key.toString(), properties.getProperty(key.toString()));
		}
	}
}
