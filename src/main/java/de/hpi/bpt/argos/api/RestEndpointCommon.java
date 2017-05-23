package de.hpi.bpt.argos.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.hpi.bpt.argos.storage.PersistenceAdapterImpl;
import de.hpi.bpt.argos.storage.dataModel.attribute.type.TypeAttribute;
import de.hpi.bpt.argos.storage.dataModel.event.query.EventQuery;
import de.hpi.bpt.argos.storage.dataModel.event.type.EventType;
import de.hpi.bpt.argos.storage.dataModel.mapping.EventEntityMapping;
import de.hpi.bpt.argos.storage.dataModel.mapping.MappingCondition;
import de.hpi.bpt.argos.util.LoggerUtilImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * This class includes all responses, which are required by more than one endpoint.
 */
public final class RestEndpointCommon {
	private static final Logger logger = LoggerFactory.getLogger(RestEndpointCommon.class);

	/**
	 * This constructor hides the default public constructor.
	 */
	private RestEndpointCommon() {

	}

	/**
	 * This method returns an event type as a JsonObject.
	 * @param eventType - the event type
	 * @return - a json representation of the event type
	 */
	public static JsonObject getEventTypeJson(EventType eventType) {
		try {
			JsonObject jsonEventType = new JsonObject();

			jsonEventType.addProperty("Id", eventType.getId());
			jsonEventType.addProperty("Name", eventType.getName());
			jsonEventType.addProperty("NumberOfEvents",
					PersistenceAdapterImpl.getInstance().getEventCountOfEventType(eventType.getId()));
			jsonEventType.addProperty("TimestampAttributeId", eventType.getTimeStampAttributeId());

			return jsonEventType;
		} catch (Exception e) {
			LoggerUtilImpl.getInstance().error(logger, "cannot parse event type", e);
			return new JsonObject();
		}
	}

	/**
	 * This method returns the json representation of a list of typeAttributes.
	 * @param typeAttributes - the typeAttributes to convert
	 * @return - the json representation of the given typeAttributes
	 */
	public static JsonArray getTypeAttributesJson(List<TypeAttribute> typeAttributes) {
		JsonArray jsonTypeAttributes = new JsonArray();
		for (TypeAttribute attribute : typeAttributes) {
			JsonObject jsonAttribute = new JsonObject();
			jsonAttribute.addProperty("Id", attribute.getId());
			jsonAttribute.addProperty("Name", attribute.getName());
			jsonTypeAttributes.add(jsonAttribute);
		}

		return jsonTypeAttributes;
	}

	/**
	 * This method returns the json representation of a list of eventEntityMappings.
	 * @param eventEntityMappings - the eventEntityMappings to convert
	 * @return - the json representation of the given eventEntityMappings
	 */
	public static JsonArray getEventEntityMappingsJson(List<EventEntityMapping> eventEntityMappings) {
		JsonArray jsonEventEntityMappings = new JsonArray();
		for (EventEntityMapping entityMapping : eventEntityMappings) {
			JsonObject jsonEntityMapping = new JsonObject();

			jsonEntityMapping.addProperty("Id", entityMapping.getId());
			jsonEntityMapping.addProperty("EventTypeId", entityMapping.getEventTypeId());
			jsonEntityMapping.addProperty("EntityTypeId", entityMapping.getEntityTypeId());
			jsonEntityMapping.addProperty("TargetStatus", entityMapping.getTargetStatus());

			// add mapping conditions as array
			List<MappingCondition> mappingConditions = PersistenceAdapterImpl.getInstance().getMappingConditions(entityMapping.getId());
			jsonEntityMapping.add("EventEntityMappingConditions", RestEndpointCommon.getMappingConditionsJson(mappingConditions));

			jsonEventEntityMappings.add(jsonEntityMapping);
		}

		return jsonEventEntityMappings;
	}

	/**
	 * This method returns the json representation of an eventQuery.
	 * @param eventQuery - the eventQuery to convert
	 * @return - the json representation of the given eventQuery
	 */
	public static JsonObject getEventQueryJson(EventQuery eventQuery) {
		JsonObject jsonQuery = new JsonObject();

		jsonQuery.addProperty("Id", eventQuery.getId());
		jsonQuery.addProperty("TypeId", eventQuery.getTypeId());
		jsonQuery.addProperty("Description", eventQuery.getDescription());
		jsonQuery.addProperty("Query", eventQuery.getQuery());

		return jsonQuery;
	}

	/**
	 * This method returns the json representation of a list of mappingConditions.
	 * @param mappingConditions - the mappingConditions to convert
	 * @return - the json representation of the given mappingConditions
	 */
	private static JsonArray getMappingConditionsJson(List<MappingCondition> mappingConditions) {
		JsonArray jsonMappingConditions = new JsonArray();
		for (MappingCondition condition : mappingConditions) {
			JsonObject jsonCondition = new JsonObject();
			jsonCondition.addProperty("EventTypeAttributeId", condition.getEventTypeAttributeId());
			jsonCondition.addProperty("EntityTypeAttributeId", condition.getEntityTypeAttributeId());
			jsonMappingConditions.add(jsonCondition);
		}

		return jsonMappingConditions;
	}
}
