package de.hpi.bpt.argos.eventProcessing;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.hpi.bpt.argos.common.RestRequest;
import de.hpi.bpt.argos.common.RestRequestFactoryImpl;
import de.hpi.bpt.argos.core.ArgosTestParent;
import de.hpi.bpt.argos.storage.PersistenceAdapterImpl;
import de.hpi.bpt.argos.storage.dataModel.attribute.Attribute;
import de.hpi.bpt.argos.storage.dataModel.attribute.type.TypeAttribute;
import de.hpi.bpt.argos.storage.dataModel.entity.Entity;
import de.hpi.bpt.argos.storage.dataModel.entity.type.EntityType;
import de.hpi.bpt.argos.storage.dataModel.event.Event;
import de.hpi.bpt.argos.storage.dataModel.event.type.EventType;
import de.hpi.bpt.argos.storage.dataModel.mapping.EventEntityMapping;
import de.hpi.bpt.argos.storage.dataModel.mapping.MappingCondition;
import de.hpi.bpt.argos.util.ArgosTestUtil;
import de.hpi.bpt.argos.util.HttpStatusCodes;
import javafx.util.Pair;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class EventReceiverTest extends ArgosTestParent {
	private static final Gson serializer = new Gson();


	private static EntityType testEntityType;
	private static List<TypeAttribute> testEntityTypeAttributes;
	private static Entity testEntity;
	private static List<Attribute> testEntityAttributes;
	private static EventType testEventType;
	private static List<TypeAttribute> testEventTypeAttributes;
	private static EventEntityMapping testMapping;
	private static List<MappingCondition> testMappingConditions;

	@BeforeClass
	public static void initialize() {
		testEntityType = ArgosTestUtil.createEntityType(true);
		testEntityTypeAttributes = ArgosTestUtil.createEntityTypeAttributes(testEntityType, true);
		testEntity = ArgosTestUtil.createEntity(testEntityType, true);
		testEntityAttributes = ArgosTestUtil.createEntityAttributes(testEntityType, testEntity, true);
		testEventType = ArgosTestUtil.createEventType(false, true);
		testEventTypeAttributes = ArgosTestUtil.createEventTypeAttributes(testEventType, true);
		testMapping = ArgosTestUtil.createEventEntityMapping(testEventType, testEntityType, "", true);

		testMappingConditions = ArgosTestUtil.createMappingConditions(testMapping, true,
				new Pair<>(testEventTypeAttributes.get(0).getId(), testEntityTypeAttributes.get(1).getId()));
	}

	@Test
	public void testReceiveEvent() {
		RestRequest request = RestRequestFactoryImpl.getInstance()
				.createPostRequest(ARGOS_HOST, getReceiveEventUri(testEventType.getId()));

		JsonObject event = new JsonObject();

		for (TypeAttribute eventTypeAttribute : testEventTypeAttributes) {
			if (testEventType.getTimeStampAttributeId() == eventTypeAttribute.getId()) {
				event.addProperty(eventTypeAttribute.getName(), ArgosTestUtil.getCurrentTimestamp());
				continue;
			}

			event.addProperty(eventTypeAttribute.getName(), ArgosTestUtil.getRandomString());
		}

		setValidMapping(event);
		request.setContent(serializer.toJson(event));

		assertEquals(HttpStatusCodes.SUCCESS, request.getResponseCode());

		List<Event> events = PersistenceAdapterImpl.getInstance().getEventsOfEventType(testEventType.getId());

		assertEquals(1, events.size());
	}

	@Test
	public void testReceiveEvent_InvalidEventTypeId_NotFound() {
		RestRequest request = RestRequestFactoryImpl.getInstance()
				.createPostRequest(ARGOS_HOST, getReceiveEventUri(testEventType.getId() - 1));

		JsonObject event = new JsonObject();

		for (TypeAttribute eventTypeAttribute : testEventTypeAttributes) {
			if (testEventType.getTimeStampAttributeId() == eventTypeAttribute.getId()) {
				event.addProperty(eventTypeAttribute.getName(), ArgosTestUtil.getCurrentTimestamp());
				continue;
			}

			event.addProperty(eventTypeAttribute.getName(), ArgosTestUtil.getRandomString());
		}

		setValidMapping(event);
		request.setContent(serializer.toJson(event));

		assertEquals(HttpStatusCodes.NOT_FOUND, request.getResponseCode());
	}

	@Test
	public void testReceiveEvent_InvalidMapping_BadRequest() {
		RestRequest request = RestRequestFactoryImpl.getInstance()
				.createPostRequest(ARGOS_HOST, getReceiveEventUri(testEventType.getId()));

		JsonObject event = new JsonObject();

		for (TypeAttribute eventTypeAttribute : testEventTypeAttributes) {
			if (testEventType.getTimeStampAttributeId() == eventTypeAttribute.getId()) {
				event.addProperty(eventTypeAttribute.getName(), ArgosTestUtil.getCurrentTimestamp());
				continue;
			}

			event.addProperty(eventTypeAttribute.getName(), ArgosTestUtil.getRandomString());
		}

		setInvalidMapping(event);
		request.setContent(serializer.toJson(event));

		assertEquals(HttpStatusCodes.BAD_REQUEST, request.getResponseCode());
	}

	private void setValidMapping(JsonObject event) {
		for (MappingCondition mappingCondition : testMappingConditions) {
			event.addProperty(
					getTypeAttributeName(testEventTypeAttributes, mappingCondition.getEventTypeAttributeId()),
					getAttributeValue(testEntityAttributes, mappingCondition.getEntityTypeAttributeId()));
		}
	}

	private void setInvalidMapping(JsonObject event) {
		for (MappingCondition mappingCondition : testMappingConditions) {
			event.addProperty(
					getTypeAttributeName(testEventTypeAttributes, mappingCondition.getEventTypeAttributeId()),
					getAttributeValue(testEntityAttributes, mappingCondition.getEntityTypeAttributeId()) + "_invalid");
		}
	}

	private String getTypeAttributeName(List<TypeAttribute> typeAttributes, long typeAttributeId) {
		for (TypeAttribute typeAttribute : typeAttributes) {
			if (typeAttribute.getId() == typeAttributeId) {
				return typeAttribute.getName();
			}
		}

		return "";
	}

	private String getAttributeValue(List<Attribute> attributes, long typeAttributeId) {
		for (Attribute attribute : attributes) {
			if (attribute.getTypeAttributeId() == typeAttributeId) {
				return attribute.getValue();
			}
		}

		return "";
	}

	private String getReceiveEventUri(Object eventTypeId) {
		return EventReceiver.getReceiveEventBaseUri().replaceAll(EventReceiver.getEventTypeIdParameter(true), eventTypeId.toString());
	}
}
