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
public class PropertyReaderImpl implements PropertyReader {
	protected static final Logger logger = LoggerFactory.getLogger(PropertyReaderImpl.class);

	protected static Map<String, String> propertyMap;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getProperty(String propertyKey) {

		if (propertyMap != null) {
			return propertyMap.get(propertyKey);
		}

		propertyMap = new HashMap<>();

		Properties properties = new Properties();

		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(PropertyReader.getPropertyFile());

		if (inputStream != null) {
			try {
				properties.load(inputStream);
			} catch (IOException e) {
				logger.error("cannot read from property file", e);
			}
		} else {
			logger.error(String.format("cannot find property file '%1$s'.", PropertyReader.getPropertyFile()));
		}

		for(Object key : properties.keySet()) {
			propertyMap.put(key.toString(), properties.getProperty(key.toString()));
		}

		return propertyMap.get(propertyKey);
	}
}
