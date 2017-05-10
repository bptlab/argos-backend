package de.hpi.bpt.argos.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.hpi.bpt.argos.api.entity.EntityEndpoint;
import de.hpi.bpt.argos.common.RestRequest;
import de.hpi.bpt.argos.common.RestRequestFactoryImpl;
import de.hpi.bpt.argos.core.ArgosTestParent;
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
				.createGetRequest(ARGOS_REST_HOST, getEventTypesOfEntityUri(testEntity.getId()));

		assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());

		JsonArray eventTypes = jsonParser.parse(request.getResponse()).getAsJsonArray();
		assertEquals(1, eventTypes.size());

		JsonObject eventType = eventTypes.get(0).getAsJsonObject();
		assertEquals(testEventType.getId(), eventType.get("Id").getAsLong());
		assertEquals(testEventType.getName(), eventType.get("Name").getAsString());
		assertEquals(1, eventType.get("NumberOfEvents").getAsLong());
		assertEquals(testEventType.getTimeStampAttributeId(), eventType.get("TimestampAttributeId").getAsLong());
	}

	@Test
	public void testGetEventTypesOfEntity_InvalidId_Success() {
		RestRequest request = RestRequestFactoryImpl.getInstance()
				.createGetRequest(ARGOS_REST_HOST, getEventTypesOfEntityUri(testEntity.getId() + 1));

		assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());

		JsonArray eventTypes = jsonParser.parse(request.getResponse()).getAsJsonArray();
		assertEquals(0, eventTypes.size());
	}

	@Test
	public void testGetEventsOfEntity() {
		RestRequest request = RestRequestFactoryImpl.getInstance()
				.createGetRequest(ARGOS_REST_HOST, getEventsOfEntityUri(testEntity.getId(), testEventType.getId(), 0, 999));

		assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());

		JsonArray events = jsonParser.parse(request.getResponse()).getAsJsonArray();
		assertEquals(1, events.size());

		JsonObject event = events.get(0).getAsJsonObject();
		JsonArray eventAttributes = event.get("Attributes").getAsJsonArray();
		assertEquals(testEventAttributes.size(), eventAttributes.size());
	}

	@Test
	public void testGetEventsOfEntity_SwappedIndices_Success() {
		RestRequest request = RestRequestFactoryImpl.getInstance()
				.createGetRequest(ARGOS_REST_HOST, getEventsOfEntityUri(testEntity.getId(), testEventType.getId(), 999, 0));

		assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());

		JsonArray events = jsonParser.parse(request.getResponse()).getAsJsonArray();
		assertEquals(1, events.size());

		JsonObject event = events.get(0).getAsJsonObject();
		JsonArray eventAttributes = event.get("Attributes").getAsJsonArray();
		assertEquals(testEventAttributes.size(), eventAttributes.size());
	}

	@Test
	public void testGetEventsOfEntity_InvalidEntityId_Success() {
		RestRequest request = RestRequestFactoryImpl.getInstance()
				.createGetRequest(ARGOS_REST_HOST, getEventsOfEntityUri(testEntity.getId() + 1, testEventType.getId(), 0, 999));

		assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());

		JsonArray events = jsonParser.parse(request.getResponse()).getAsJsonArray();
		assertEquals(0, events.size());
	}

	@Test
	public void testGetEventsOfEntity_InvalidEventTypeId_Success() {
		RestRequest request = RestRequestFactoryImpl.getInstance()
				.createGetRequest(ARGOS_REST_HOST, getEventsOfEntityUri(testEntity.getId(), testEventType.getId() + 1, 0, 999));

		assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());

		JsonArray events = jsonParser.parse(request.getResponse()).getAsJsonArray();
		assertEquals(0, events.size());
	}

	@Test
	public void testGetEventsOfEntity_InvalidStartIndex_BadRequest() {
		RestRequest request = RestRequestFactoryImpl.getInstance()
				.createGetRequest(ARGOS_REST_HOST, getEventsOfEntityUri(testEntity.getId(), testEventType.getId(), -1, 999));

		assertEquals(HttpStatusCodes.BAD_REQUEST, request.getResponseCode());
	}

	@Test
	public void testGetEventsOfEntity_InvalidEndIndex_BadRequest() {
		RestRequest request = RestRequestFactoryImpl.getInstance()
				.createGetRequest(ARGOS_REST_HOST, getEventsOfEntityUri(testEntity.getId(), testEventType.getId(), 0, -1));

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

	private String getEventTypesOfEntityUri(Object entityId) {
		return EntityEndpoint.getEventTypesOfEntityBaseUri()
				.replaceAll(EntityEndpoint.getEntityIdParameter(true), entityId.toString());
	}

	private String getEventsOfEntityUri(Object entityId, Object eventTypeId, Object startIndex, Object endIndex) {
		return EntityEndpoint.getEventsOfEntityBaseUri()
				.replaceAll(EntityEndpoint.getEntityIdParameter(true), entityId.toString())
				.replaceAll(EntityEndpoint.getTypeIdParameter(true), eventTypeId.toString())
				.replaceAll(EntityEndpoint.getIndexFromParameter(true), startIndex.toString())
				.replaceAll(EntityEndpoint.getIndexToParameter(true), endIndex.toString());
	}
}
