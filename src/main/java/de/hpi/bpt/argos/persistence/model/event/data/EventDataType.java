package de.hpi.bpt.argos.persistence.model.event.data;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This enum is used to determine which type an event data has.
 */
public enum EventDataType {
	INTEGER,
	LONG,
	STRING,
	FLOAT,
	DATE;

	protected static final Logger logger = LoggerFactory.getLogger(EventDataType.class);
	protected static final Gson serializer = new Gson();

	/**
	 * This method returns the XSD type name for this EventDataType.
	 * @return - the XSD type name
	 */
	public String getXSDTypeName() {

		switch (this) {
			case STRING:
				return "xs:string";

			case INTEGER:
				return "xs:int";

			case LONG:
				return "xs:long";

			case FLOAT:
				return "xs:float";

			case DATE:
				return "xs:date";

			default:
				logger.error(String.format("unsupported DataType '%1$s'", this.toString()));
				return "xs:string";

		}
	}

	/**
	 * This method adds a new property with a specified name to an existing json object by casting the given value accordingly to the EventDataType.
	 * @param jsonObject - the json object to extend
	 * @param propertyName - the name for the new property
	 * @param jsonValue - the value to add as string
	 */
	public void addJSONProperty(JsonObject jsonObject, String propertyName, String jsonValue) {

		switch (this) {
			case STRING:
				jsonObject.addProperty(propertyName, jsonValue);
				break;

			case INTEGER:
				jsonObject.addProperty(propertyName, serializer.fromJson(jsonValue, Integer.class));
				break;

			case LONG:
				jsonObject.addProperty(propertyName, serializer.fromJson(jsonValue, Long.class));
				break;

			case FLOAT:
				jsonObject.addProperty(propertyName, serializer.fromJson(jsonValue, Float.class));
				break;

			case DATE:
				jsonObject.addProperty(propertyName, jsonValue);
				break;

			default:
				logger.error(String.format("unsupported DataType '%1$s'", this.toString()));
				jsonObject.addProperty(propertyName, jsonValue);
				break;

		}
	}
}
