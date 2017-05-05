package de.hpi.bpt.argos.testUtil;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.hpi.bpt.argos.storage.PersistenceAdapterImpl;
import de.hpi.bpt.argos.storage.PersistenceArtifactUpdateType;
import de.hpi.bpt.argos.storage.dataModel.attribute.Attribute;
import de.hpi.bpt.argos.storage.dataModel.attribute.AttributeImpl;
import de.hpi.bpt.argos.storage.dataModel.attribute.type.TypeAttribute;
import de.hpi.bpt.argos.storage.dataModel.attribute.type.TypeAttributeImpl;
import de.hpi.bpt.argos.storage.dataModel.entity.Entity;
import de.hpi.bpt.argos.storage.dataModel.entity.EntityImpl;
import de.hpi.bpt.argos.storage.dataModel.entity.type.EntityType;
import de.hpi.bpt.argos.storage.dataModel.entity.type.EntityTypeImpl;
import de.hpi.bpt.argos.storage.dataModel.event.Event;
import de.hpi.bpt.argos.storage.dataModel.event.EventImpl;
import de.hpi.bpt.argos.storage.dataModel.event.query.EventQuery;
import de.hpi.bpt.argos.storage.dataModel.event.query.EventQueryImpl;
import de.hpi.bpt.argos.storage.dataModel.event.type.EventType;
import de.hpi.bpt.argos.storage.dataModel.event.type.EventTypeImpl;
import de.hpi.bpt.argos.storage.dataModel.mapping.EventEntityMapping;
import de.hpi.bpt.argos.storage.dataModel.mapping.EventEntityMappingImpl;
import de.hpi.bpt.argos.storage.dataModel.mapping.MappingCondition;
import de.hpi.bpt.argos.storage.dataModel.mapping.MappingConditionImpl;
import javafx.util.Pair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class ArgosTestUtil {
	private static final Random random = new Random();
	private static final JsonParser jsonParser = new JsonParser();

	public static String getCurrentTimestamp() {
		return new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss_SSS").format(new Date());
	}

	public static int getRandomInteger(int min, int max) {
		return random.nextInt(max) + min;
	}

	public static long getRandomLong() {
		return random.nextLong();
	}

	public static float getRandomFloat() {
		return random.nextFloat();
	}

	public static String getRandomString() {
		return UUID.randomUUID().toString();
	}

	public static EntityType createEntityType(boolean saveInDatabase) {
		EntityType newEntityType = new EntityTypeImpl();

		newEntityType.setName("EntityType_" + getCurrentTimestamp());
		newEntityType.setParentId(-1); // directly under root node

		if (saveInDatabase) {
			PersistenceAdapterImpl.getInstance().saveArtifacts(newEntityType);
		}

		return newEntityType;
	}

	public static EntityType createEntityType(EntityType parent, boolean saveInDatabase) {
		EntityType newEntityType = createEntityType(false);

		newEntityType.setParentId(parent.getId());

		if (saveInDatabase) {
			PersistenceAdapterImpl.getInstance().saveArtifacts(newEntityType);
		}

		return newEntityType;
	}

	public static List<TypeAttribute> createEntityTypeAttributes(EntityType owner, boolean saveInDatabase) {
		return createTypeAttributes(owner.getId(), saveInDatabase);
	}

	public static Entity createEntity(EntityType type, boolean saveInDatabase) {
		Entity newEntity = new EntityImpl();

		newEntity.setName("Entity_" + getCurrentTimestamp());
		newEntity.setParentId(-1); // directly under root node
		newEntity.setTypeId(type.getId());
		newEntity.setStatus(EntityStatus.OK);

		if (saveInDatabase) {
			PersistenceAdapterImpl.getInstance().saveArtifacts(newEntity);
		}

		return newEntity;
	}

	public static Entity createEntity(EntityType type, Entity parent, boolean saveInDatabase) {
		Entity newEntity = createEntity(type, false);

		newEntity.setParentId(parent.getId());

		if (saveInDatabase) {
			PersistenceAdapterImpl.getInstance().saveArtifacts(newEntity);
		}

		return newEntity;
	}

	public static List<Attribute> createEntityAttributes(EntityType type, Entity owner, boolean saveInDatabase) {
		return createAttributes(type.getId(), owner.getId(), saveInDatabase);
	}

	public static EventType createEventType(boolean deletable, boolean saveInDatabase) {
		EventType newEventType = new EventTypeImpl();

		newEventType.setName("EventType_" + getCurrentTimestamp());
		newEventType.setShouldBeRegistered(false);
		newEventType.setDeletable(deletable);

		if (saveInDatabase) {
			PersistenceAdapterImpl.getInstance().saveArtifacts(newEventType);
		}

		return newEventType;
	}

	public static List<TypeAttribute> createEventTypeAttributes(EventType type, boolean saveInDatabase) {
		List<TypeAttribute> eventTypeAttributes = createTypeAttributes(type.getId(), saveInDatabase);

		type.setTimeStampAttributeId(eventTypeAttributes.get(0).getId());

		if (saveInDatabase) {
			PersistenceAdapterImpl.getInstance().saveArtifacts(type);
		}

		return eventTypeAttributes;
	}

	public static EventQuery createEventQuery(EventType type, boolean saveInDatabase) {
		EventQuery newQuery = new EventQueryImpl();
		newQuery.setTypeId(type.getId());
		newQuery.setQuery(String.format("SELECT * FROM %1$s", type.getName()));
		newQuery.setDescription("Description");
		newQuery.setUuid(getRandomString());

		if (saveInDatabase) {
			PersistenceAdapterImpl.getInstance().saveArtifacts(newQuery);
		}

		return newQuery;
	}

	public static List<EventQuery> createEventQueries(EventType type, boolean saveInDatabase) {
		List<EventQuery> queries = new ArrayList<>();

		for (int i = 0; i < getRandomInteger(2, 10); i++) {
			queries.add(createEventQuery(type, false));
		}

		if (saveInDatabase) {
			PersistenceAdapterImpl.getInstance().saveArtifacts(queries.toArray(new EventQuery[queries.size()]));
		}

		return queries;
	}

	public static EventQuery createBlockingEventQuery(EventType blockingEventType, boolean saveInDatabase, EventType blockedEventType) {
		EventQuery query = createEventQuery(blockingEventType, false);
		query.setQuery(String.format("this query blocks %1$s", blockedEventType.getName()));

		if (saveInDatabase) {
			PersistenceAdapterImpl.getInstance().saveArtifacts(query);
		}

		return query;
	}

	public static Event createEvent(EventType type, Entity entity, boolean saveInDatabase) {
		Event newEvent = new EventImpl();

		newEvent.setTypeId(type.getId());
		newEvent.setEntityId(entity.getId());

		if (saveInDatabase) {
			PersistenceAdapterImpl.getInstance().saveArtifacts(newEvent);
		}

		return newEvent;
	}

	public static List<Attribute> createEventAttributes(EventType type, Event owner, boolean saveInDatabase) {
		List<Attribute> eventAttributes = createAttributes(type.getId(), owner.getId(), saveInDatabase);

		for (Attribute eventAttribute : eventAttributes) {
			if (eventAttribute.getTypeAttributeId() == type.getTimeStampAttributeId()) {
				eventAttribute.setValue(getCurrentTimestamp());
				break;
			}
		}

		if (saveInDatabase) {
			PersistenceAdapterImpl.getInstance().saveArtifacts(eventAttributes.toArray(new Attribute[eventAttributes.size()]));
		}

		return eventAttributes;
	}

	public static EventEntityMapping createEventEntityMapping(EventType eventType, EntityType entityType, String targetStatus,
															  boolean saveInDatabase) {
		EventEntityMapping newMapping = new EventEntityMappingImpl();

		newMapping.setEventTypeId(eventType.getId());
		newMapping.setEntityTypeId(entityType.getId());
		newMapping.setTargetStatus(targetStatus);

		if (saveInDatabase) {
			PersistenceAdapterImpl.getInstance().saveArtifacts(newMapping);
		}

		return newMapping;
	}

	public static List<MappingCondition> createMappingConditions(EventEntityMapping mapping, boolean saveInDatabase,
																 Pair<Long, Long>... eventEntityTypeAttributeIds) {
		List<MappingCondition> mappingConditions = new ArrayList<>();

		for (Pair<Long, Long> eventEntityTypeAttributeId : eventEntityTypeAttributeIds) {
			MappingCondition newMappingCondition = new MappingConditionImpl();

			newMappingCondition.setEventTypeAttributeId(eventEntityTypeAttributeId.getKey());
			newMappingCondition.setEntityTypeAttributeId(eventEntityTypeAttributeId.getValue());
			newMappingCondition.setMappingId(mapping.getId());

			mappingConditions.add(newMappingCondition);
		}

		if (saveInDatabase) {
			PersistenceAdapterImpl.getInstance().saveArtifacts(mappingConditions.toArray(new MappingCondition[mappingConditions.size()]));
		}

		return mappingConditions;
	}

	public static List<MappingCondition> createMappingConditions(EventEntityMapping mapping,
																 List<TypeAttribute> eventTypeAttributes,
																 List<TypeAttribute> entityTypeAttributes,
																 boolean saveInDatabase) {

		return createMappingConditions(mapping, saveInDatabase, new Pair<>(eventTypeAttributes.get(0).getId(), entityTypeAttributes.get(0).getId()));

	}

	public static void assertWebSocketMessage(String message, PersistenceArtifactUpdateType updateReason, String artifactType) throws Exception {

		JsonArray jsonMessages = jsonParser.parse(message).getAsJsonArray();

		if (jsonMessages.size() != 1) {
			throw new Exception("too many messages included");
		}

		JsonObject jsonMessage = jsonMessages.get(0).getAsJsonObject();

		if (!jsonMessage.get("UpdateReason").getAsString().equalsIgnoreCase(updateReason.toString())) {
			throw new Exception("update reason did not match");
		}

		if (!jsonMessage.get("ArtifactType").getAsString().equalsIgnoreCase(artifactType)) {
			throw new Exception("artifact type did not match");
		}
	}

	private static List<TypeAttribute> createTypeAttributes(long typeId, boolean saveInDatabase) {
		List<TypeAttribute> typeAttributes = new ArrayList<>();

		for (int i = 0; i < getRandomInteger(2, 10); i++) {
			TypeAttribute newTypeAttribute = new TypeAttributeImpl();

			newTypeAttribute.setName("TypeAttribute_" + getRandomString());
			newTypeAttribute.setTypeId(typeId);

			typeAttributes.add(newTypeAttribute);
		}

		if (saveInDatabase) {
			PersistenceAdapterImpl.getInstance().saveArtifacts(typeAttributes.toArray(new TypeAttribute[typeAttributes.size()]));
		}

		return typeAttributes;
	}

	private static List<Attribute> createAttributes(long typeId, long ownerId, boolean saveInDatabase) {
		List<Attribute> attributes = new ArrayList<>();

		for (TypeAttribute typeAttribute : PersistenceAdapterImpl.getInstance().getTypeAttributes(typeId)) {
			Attribute newAttribute = new AttributeImpl();

			newAttribute.setOwnerId(ownerId);
			newAttribute.setTypeAttributeId(typeAttribute.getId());
			newAttribute.setValue(getRandomString());

			attributes.add(newAttribute);
		}

		if (saveInDatabase) {
			PersistenceAdapterImpl.getInstance().saveArtifacts(attributes.toArray(new Attribute[attributes.size()]));
		}

		return attributes;
	}
}
