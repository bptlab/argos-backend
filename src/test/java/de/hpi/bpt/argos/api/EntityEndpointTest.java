package de.hpi.bpt.argos.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.hpi.bpt.argos.api.entity.EntityEndpoint;
import de.hpi.bpt.argos.common.RestRequest;
import de.hpi.bpt.argos.common.RestRequestFactoryImpl;
import de.hpi.bpt.argos.core.ArgosTestParent;
import de.hpi.bpt.argos.storage.PersistenceAdapterImpl;
import de.hpi.bpt.argos.storage.dataModel.attribute.Attribute;
import de.hpi.bpt.argos.storage.dataModel.attribute.type.TypeAttribute;
import de.hpi.bpt.argos.storage.dataModel.entity.Entity;
import de.hpi.bpt.argos.storage.dataModel.entity.VirtualRoot;
import de.hpi.bpt.argos.storage.dataModel.entity.type.EntityType;
import de.hpi.bpt.argos.storage.dataModel.event.Event;
import de.hpi.bpt.argos.storage.dataModel.event.type.EventType;
import de.hpi.bpt.argos.testUtil.ArgosTestUtil;
import de.hpi.bpt.argos.util.HttpStatusCodes;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

public class EntityEndpointTest extends ArgosTestParent {

	private static EntityType testEntityType;
	private static List<TypeAttribute> testEntityTypeAttributes;
	private static Entity testEntity;
	private static List<Attribute> testEntityAttributes;
	private static EventType testEventType;
	private static List<TypeAttribute> testEventTypeAttributes;
	private static Event testEvent;
	private static List<Attribute> testEventAttributes;

	@BeforeClass
	public static void initialize() {
		testEntityType = ArgosTestUtil.createEntityType(true);
		testEntityTypeAttributes = ArgosTestUtil.createEntityTypeAttributes(testEntityType, true);
		testEntity = ArgosTestUtil.createEntity(testEntityType, true);
		testEntityAttributes = ArgosTestUtil.createEntityAttributes(testEntityType, testEntity, true);
		testEventType = ArgosTestUtil.createEventType(false, true);
		testEventTypeAttributes = ArgosTestUtil.createEventTypeAttributes(testEventType, true);
		testEvent = ArgosTestUtil.createEvent(testEventType, testEntity, true);
		testEventAttributes = ArgosTestUtil.createEventAttributes(testEventType, testEvent, true);
	}

	@Test
	public void testGetEntity() {
		RestRequest request = RestRequestFactoryImpl.getInstance().createGetRequest(ARGOS_REST_HOST, getEntityUri(testEntity.getId()));

		assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());

		JsonObject entity = jsonParser.parse(request.getResponse()).getAsJsonObject();
		JsonArray entityAttributes = entity.get("Attributes").getAsJsonArray();

		assertEquals(testEntity.getId(), entity.get("Id").getAsLong());
		assertEquals(testEntity.getTypeId(), entity.get("TypeId").getAsLong());
		assertEquals(testEntity.getParentId(), entity.get("ParentId").getAsLong());
		assertEquals(testEntity.getName(), entity.get("Name").getAsString());
		assertEquals(testEntity.getStatus(), entity.get("Status").getAsString());
		assertFalse(entity.get("HasChildren").getAsBoolean());
		assertEquals(testEntityAttributes.size(), entityAttributes.size());
	}

	@Test
	public void testGetEntity_VirtualRoot_Success() {
		RestRequest request = RestRequestFactoryImpl.getInstance().createGetRequest(ARGOS_REST_HOST, getEntityUri(VirtualRoot.getInstance().getId()));

		assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());

		JsonObject entity = jsonParser.parse(request.getResponse()).getAsJsonObject();
		JsonArray entityAttributes = entity.get("Attributes").getAsJsonArray();

		assertEquals(VirtualRoot.getInstance().getId(), entity.get("Id").getAsLong());
		assertEquals(VirtualRoot.getInstance().getTypeId(), entity.get("TypeId").getAsLong());
		assertEquals(VirtualRoot.getInstance().getParentId(), entity.get("ParentId").getAsLong());
		assertEquals(VirtualRoot.getInstance().getName(), entity.get("Name").getAsString());
		assertEquals(VirtualRoot.getInstance().getStatus(), entity.get("Status").getAsString());
		assertTrue(entity.get("HasChildren").getAsBoolean());
		assertEquals(0, entityAttributes.size());
	}

	@Test
	public void testGetEntity_InvalidId_NotFound() {
		RestRequest request = RestRequestFactoryImpl.getInstance().createGetRequest(ARGOS_REST_HOST, getEntityUri(testEntity.getId() - 1));

		assertEquals(HttpStatusCodes.NOT_FOUND, request.getResponseCode());
	}

	@Test
	public void testGetChildEntities() {
		List<Object> attributeNames = getEntityAttributeNamesToInclude(true);

		RestRequest request = RestRequestFactoryImpl.getInstance()
				.createGetRequest(ARGOS_REST_HOST, getChildEntitiesUri(testEntity.getParentId(), testEntity.getTypeId(), attributeNames));

		assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());

		JsonArray childEntities = jsonParser.parse(request.getResponse()).getAsJsonArray();
		assertEquals(1, childEntities.size());

		JsonObject entity = childEntities.get(0).getAsJsonObject();
		JsonArray entityAttributes = entity.get("Attributes").getAsJsonArray();

		assertEquals(testEntity.getId(), entity.get("Id").getAsLong());
		assertEquals(testEntity.getTypeId(), entity.get("TypeId").getAsLong());
		assertEquals(testEntity.getParentId(), entity.get("ParentId").getAsLong());
		assertEquals(testEntity.getName(), entity.get("Name").getAsString());
		assertEquals(testEntity.getStatus(), entity.get("Status").getAsString());
		assertFalse(entity.get("HasChildren").getAsBoolean());
		assertEquals(attributeNames.size(), entityAttributes.size());
	}

	@Test
	public void testGetChildEntities_InvalidParentId_Success() {
		List<Object> attributeNames = getEntityAttributeNamesToInclude(true);

		RestRequest request = RestRequestFactoryImpl.getInstance()
				.createGetRequest(ARGOS_REST_HOST, getChildEntitiesUri(testEntity.getParentId() - 1, testEntity.getTypeId(), attributeNames));

		assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());

		JsonArray childEntities = jsonParser.parse(request.getResponse()).getAsJsonArray();
		assertEquals(0, childEntities.size());
	}

	@Test
	public void testGetChildEntities_InvalidEntityTypeId_BadRequest() {
		List<Object> attributeNames = getEntityAttributeNamesToInclude(true);

		RestRequest request = RestRequestFactoryImpl.getInstance()
				.createGetRequest(ARGOS_REST_HOST, getChildEntitiesUri(testEntity.getParentId(), testEntity.getTypeId() + 1, attributeNames));

		assertEquals(HttpStatusCodes.BAD_REQUEST, request.getResponseCode());
	}

	@Test
	public void testGetChildEntities_InvalidAttributeName_BadRequest() {
		List<Object> attributeNames = getEntityAttributeNamesToInclude(false);

		RestRequest request = RestRequestFactoryImpl.getInstance()
				.createGetRequest(ARGOS_REST_HOST, getChildEntitiesUri(testEntity.getParentId(), testEntity.getTypeId(), attributeNames));

		assertEquals(HttpStatusCodes.BAD_REQUEST, request.getResponseCode());
	}

	@Test
	public void testGetEventTypesOfEntity() {
		RestRequest request = RestRequestFactoryImpl.getInstance()
				.createGetRequest(ARGOS_REST_HOST, getEventTypesOfEntityUri(testEntity.getId(), false));

		assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());

		JsonArray eventTypes = jsonParser.parse(request.getResponse()).getAsJsonArray();
		assertEventTypes(eventTypes, testEventType);
	}

	@Test
	public void testGetEventTypesOfEntity_ChildEventTypes_Success() {
		EventType newEventType = ArgosTestUtil.createEventType(false, true);
		List<TypeAttribute> newEventTypeAttributes = ArgosTestUtil.createEventTypeAttributes(newEventType, true);
		Entity newEntity = ArgosTestUtil.createEntity(testEntityType, testEntity, true);
		Event newEvent = ArgosTestUtil.createEvent(newEventType, newEntity, true);

		RestRequest request = RestRequestFactoryImpl.getInstance()
				.createGetRequest(ARGOS_REST_HOST, getEventTypesOfEntityUri(testEntity.getId(), true));

		assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());

		// delete those artifacts, to not disturb other tests
		PersistenceAdapterImpl.getInstance().deleteArtifacts(newEventType, newEntity, newEvent);
		PersistenceAdapterImpl.getInstance().deleteArtifacts(newEventTypeAttributes.toArray(new TypeAttribute[newEventTypeAttributes.size()]));

		JsonArray eventTypes = jsonParser.parse(request.getResponse()).getAsJsonArray();
		assertEventTypes(eventTypes, testEventType, newEventType);
	}

	@Test
	public void testGetEventTypesOfEntity_DoNotIncludeChildEventTypes_Success() {
		EventType newEventType = ArgosTestUtil.createEventType(false, true);
		List<TypeAttribute> newEventTypeAttributes = ArgosTestUtil.createEventTypeAttributes(newEventType, true);
		Entity newEntity = ArgosTestUtil.createEntity(testEntityType, testEntity, true);
		Event newEvent = ArgosTestUtil.createEvent(newEventType, newEntity, true);

		RestRequest request = RestRequestFactoryImpl.getInstance()
				.createGetRequest(ARGOS_REST_HOST, getEventTypesOfEntityUri(testEntity.getId(), false));

		assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());

		// delete those artifacts, to not disturb other tests
		PersistenceAdapterImpl.getInstance().deleteArtifacts(newEventType, newEntity, newEvent);
		PersistenceAdapterImpl.getInstance().deleteArtifacts(newEventTypeAttributes.toArray(new TypeAttribute[newEventTypeAttributes.size()]));

		JsonArray eventTypes = jsonParser.parse(request.getResponse()).getAsJsonArray();
		assertEventTypes(eventTypes, testEventType);
	}

	@Test
	public void testGetEventTypesOfEntity_InvalidId_NotFound() {
		RestRequest request = RestRequestFactoryImpl.getInstance()
				.createGetRequest(ARGOS_REST_HOST, getEventTypesOfEntityUri(testEntity.getId() + 1, true));

		assertEquals(HttpStatusCodes.NOT_FOUND, request.getResponseCode());
	}

	@Test
	public void testGetEventsOfEntity() {
		RestRequest request = RestRequestFactoryImpl.getInstance()
				.createGetRequest(ARGOS_REST_HOST,
						getEventsOfEntityUri(testEntity.getId(), testEventType.getId(), false, 0, 999));

		assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());

		JsonArray events = jsonParser.parse(request.getResponse()).getAsJsonArray();
		assertEvents(events, testEventAttributes);
	}

	@Test
	public void testGetEventsOfEntity_SwappedIndices_Success() {
		RestRequest request = RestRequestFactoryImpl.getInstance()
				.createGetRequest(ARGOS_REST_HOST,
						getEventsOfEntityUri(testEntity.getId(), testEventType.getId(), false, 999, 0));

		assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());

		JsonArray events = jsonParser.parse(request.getResponse()).getAsJsonArray();
		assertEvents(events, testEventAttributes);
	}

	@Test
	public void testGetEventsOfEntity_ChildEvents_Success() {
		Entity newEntity = ArgosTestUtil.createEntity(testEntityType, testEntity, true);
		Event newEvent = ArgosTestUtil.createEvent(testEventType, newEntity, true);
		List<Attribute> newEventAttributes = ArgosTestUtil.createEventAttributes(testEventType, newEvent, true);

		RestRequest request = RestRequestFactoryImpl.getInstance()
				.createGetRequest(ARGOS_REST_HOST,
						getEventsOfEntityUri(testEntity.getId(), testEventType.getId(), true, 0, 999));

		assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());

		// delete these again to not disturb other tests
		PersistenceAdapterImpl.getInstance().deleteArtifacts(newEntity, newEvent);
		PersistenceAdapterImpl.getInstance().deleteArtifacts(newEventAttributes.toArray(new Attribute[newEventAttributes.size()]));

		JsonArray events = jsonParser.parse(request.getResponse()).getAsJsonArray();
		assertEvents(events, testEventAttributes, newEventAttributes);
	}

	@Test
	public void testGetEventsOfEntity_DoNotIncludeChildEvents_Success() {
		Entity newEntity = ArgosTestUtil.createEntity(testEntityType, testEntity, true);
		Event newEvent = ArgosTestUtil.createEvent(testEventType, newEntity, true);
		List<Attribute> newEventAttributes = ArgosTestUtil.createEventAttributes(testEventType, newEvent, true);

		RestRequest request = RestRequestFactoryImpl.getInstance()
				.createGetRequest(ARGOS_REST_HOST,
						getEventsOfEntityUri(testEntity.getId(), testEventType.getId(), false, 0, 999));

		assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());

		// delete these again to not disturb other tests
		PersistenceAdapterImpl.getInstance().deleteArtifacts(newEntity, newEvent);
		PersistenceAdapterImpl.getInstance().deleteArtifacts(newEventAttributes.toArray(new Attribute[newEventAttributes.size()]));

		JsonArray events = jsonParser.parse(request.getResponse()).getAsJsonArray();
		assertEvents(events, testEventAttributes);
	}

	@Test
	public void testGetEventsOfEntity_EmptyList_Success() {
		RestRequest request = RestRequestFactoryImpl.getInstance()
				.createGetRequest(ARGOS_REST_HOST,
						getEventsOfEntityUri(testEntity.getId(), testEventType.getId(), true, 0, 0));

		assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());

		JsonArray events = jsonParser.parse(request.getResponse()).getAsJsonArray();
		assertEquals(0, events.size());

	}

	@Test
	public void testGetEventsOfEntity_InvalidEntityId_NotFound() {
		RestRequest request = RestRequestFactoryImpl.getInstance()
				.createGetRequest(ARGOS_REST_HOST,
						getEventsOfEntityUri(testEntity.getId() + 1, testEventType.getId(), true, 0, 999));

		assertEquals(HttpStatusCodes.NOT_FOUND, request.getResponseCode());
	}

	@Test
	public void testGetEventsOfEntity_InvalidEventTypeId_Success() {
		RestRequest request = RestRequestFactoryImpl.getInstance()
				.createGetRequest(ARGOS_REST_HOST,
						getEventsOfEntityUri(testEntity.getId(), testEventType.getId() + 1, true, 0, 999));

		assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());

		JsonArray events = jsonParser.parse(request.getResponse()).getAsJsonArray();
		assertEquals(0, events.size());
	}

	@Test
	public void testGetEventsOfEntity_InvalidStartIndex_BadRequest() {
		RestRequest request = RestRequestFactoryImpl.getInstance()
				.createGetRequest(ARGOS_REST_HOST,
						getEventsOfEntityUri(testEntity.getId(), testEventType.getId(), true, -1, 999));

		assertEquals(HttpStatusCodes.BAD_REQUEST, request.getResponseCode());
	}

	@Test
	public void testGetEventsOfEntity_InvalidEndIndex_BadRequest() {
		RestRequest request = RestRequestFactoryImpl.getInstance()
				.createGetRequest(ARGOS_REST_HOST,
						getEventsOfEntityUri(testEntity.getId(), testEventType.getId(), true, 0, -1));

		assertEquals(HttpStatusCodes.BAD_REQUEST, request.getResponseCode());
	}

	private List<Object> getEntityAttributeNamesToInclude(boolean valid) {
		List<Object> attributeNames = new ArrayList<>();
		String prefix = "";

		if (!valid) {
			prefix = "invalid_";
		}

		for (int i = 0; i < 2; i++) {
			attributeNames.add(prefix + testEntityTypeAttributes.get(i).getName());
		}

		return attributeNames;
	}

	private long getTypeAttributeId(String typeAttributeName) {
		for (TypeAttribute typeAttribute : testEventTypeAttributes) {
			if (typeAttribute.getName().equals(typeAttributeName)) {
				return typeAttribute.getId();
			}
		}

		return 0;
	}

	private String getAttributeValue(long typeAttributeId, List<Attribute> attributes) {
		for (Attribute attribute : attributes) {
			if (attribute.getTypeAttributeId() == typeAttributeId) {
				return attribute.getValue();
			}
		}

		return "";
	}

	private void assertEventAttributes(JsonArray jsonEventAttributes, List<Attribute> eventAttributes) {
		for (JsonElement element : jsonEventAttributes) {
			JsonObject attribute = element.getAsJsonObject();

			long typeAttributeId = getTypeAttributeId(attribute.get("Name").getAsString());
			String attributeValue = getAttributeValue(typeAttributeId, eventAttributes);

			assertEquals(attributeValue, attribute.get("Value").getAsString());
		}
	}

	private void assertEventType(EventType expected, JsonObject actual) {
		assertNotNull(actual);
		assertEquals(expected.getId(), actual.get("Id").getAsLong());
		assertEquals(expected.getName(), actual.get("Name").getAsString());
		assertEquals(1, actual.get("NumberOfEvents").getAsLong());
		assertEquals(expected.getTimeStampAttributeId(), actual.get("TimestampAttributeId").getAsLong());
	}

	private void assertEventTypes(JsonArray jsonEventTypes, EventType... eventTypes) {
		assertNotNull(jsonEventTypes);
		assertEquals(eventTypes.length, jsonEventTypes.size());

		for (JsonElement element : jsonEventTypes) {
			JsonObject jsonEventType = element.getAsJsonObject();

			boolean eventTypeFound = false;
			for (EventType eventType : eventTypes) {
				if (jsonEventType.get("Id").getAsLong() == eventType.getId()) {
					eventTypeFound = true;
					assertEventType(eventType, jsonEventType);
				}
			}

			assertTrue(eventTypeFound);
		}
	}

	private void assertEvent(List<Attribute> expected, JsonObject actual) {
		assertNotNull(actual);
		JsonArray actualAttributes = actual.get("Attributes").getAsJsonArray();
		assertEquals(expected.size(), actualAttributes.size());
		assertEventAttributes(actualAttributes, expected);
	}

	private void assertEvents(JsonArray jsonEvents, List<Attribute>... eventAttributes) {
		assertNotNull(jsonEvents);
		assertEquals(eventAttributes.length, jsonEvents.size());

		for (int i = 0; i < eventAttributes.length; i++) {
			assertEvent(eventAttributes[i], jsonEvents.get(i).getAsJsonObject());
		}
	}

	private String getEntityUri(Object entityId) {
		return EntityEndpoint.getEntityBaseUri().replaceAll(EntityEndpoint.getEntityIdParameter(true), entityId.toString());
	}

	private String getChildEntitiesUri(Object parentEntityId, Object entityTypeId, List<Object> attributesToInclude) {
		StringBuilder attributeNames = new StringBuilder();

		for (int i = 0; i < attributesToInclude.size(); i++) {
			if (i > 0) {
				attributeNames.append("+");
			}
			attributeNames.append(attributesToInclude.get(i).toString());
		}

		return EntityEndpoint.getChildEntitiesBaseUri()
				.replaceAll(EntityEndpoint.getEntityIdParameter(true), parentEntityId.toString())
				.replaceAll(EntityEndpoint.getTypeIdParameter(true), entityTypeId.toString())
				.replaceAll(EntityEndpoint.getAttributeNamesParameter(true), attributeNames.toString());
	}

	private String getEventTypesOfEntityUri(Object entityId, Object includeChildEvents) {
		return EntityEndpoint.getEventTypesOfEntityBaseUri()
				.replaceAll(EntityEndpoint.getEntityIdParameter(true), entityId.toString())
				.replaceAll(EntityEndpoint.getIncludeChildEventsParameter(true), includeChildEvents.toString());
	}

	private String getEventsOfEntityUri(Object entityId, Object eventTypeId, Object includeChildEvents, Object startIndex, Object endIndex) {
		return EntityEndpoint.getEventsOfEntityBaseUri()
				.replaceAll(EntityEndpoint.getEntityIdParameter(true), entityId.toString())
				.replaceAll(EntityEndpoint.getTypeIdParameter(true), eventTypeId.toString())
				.replaceAll(EntityEndpoint.getIncludeChildEventsParameter(true), includeChildEvents.toString())
				.replaceAll(EntityEndpoint.getIndexFromParameter(true), startIndex.toString())
				.replaceAll(EntityEndpoint.getIndexToParameter(true), endIndex.toString());
	}
}
