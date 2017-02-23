package de.hpi.bpt.argos.api;

import com.google.gson.JsonObject;
import de.hpi.bpt.argos.api.eventQueries.EventQueryEndpoint;
import de.hpi.bpt.argos.api.eventTypes.EventTypeEndpoint;
import de.hpi.bpt.argos.api.response.ResponseFactory;
import de.hpi.bpt.argos.core.ArgosTestParent;
import de.hpi.bpt.argos.core.ArgosTestUtil;
import de.hpi.bpt.argos.persistence.model.event.type.EventType;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EventQueryEndpointTest extends EndpointParentClass {
	protected static EventType testEventType;

	@BeforeClass
	public static void createTestEventType() {
		testEventType = ArgosTestUtil.createEventType();
	}

	@Test
	public void testUpdateEventQuery() {
		request = requestFactory.createPostRequest(TEST_HOST,
				updateEventQuery(testEventType.getId()),
				TEST_CONTENT_TYPE,
				TEST_ACCEPT_TYPE_PLAIN);

		String newQueryString = "SELECT * FROM " + testEventType.getName();
		JsonObject jsonBody = new JsonObject();
		jsonBody.addProperty("eventQuery", newQueryString);

		request.setContent(serializer.toJson(jsonBody));
		assertEquals(ResponseFactory.getHttpSuccessCode(), request.getResponseCode());

		EventType updatedEventType = ArgosTestParent.argos.getPersistenceEntityManager().getEventType(testEventType.getId());
		assertEquals(updatedEventType.getEventQuery().getQueryString(), newQueryString);
	}

	@Test
	public void testUpdateEventQuery_InvalidId_NotFound() {
		request = requestFactory.createPostRequest(TEST_HOST,
				updateEventQuery(testEventType.getId() - 1),
				TEST_CONTENT_TYPE,
				TEST_ACCEPT_TYPE_PLAIN);

		String newQueryString = "SELECT * FROM " + testEventType.getName();
		JsonObject jsonBody = new JsonObject();
		jsonBody.addProperty("eventQuery", newQueryString);

		request.setContent(serializer.toJson(jsonBody));
		assertEquals(ResponseFactory.getHttpNotFoundCode(), request.getResponseCode());
	}

	@Test
	public void testUpdateEventQuery_InvalidQuery_Error() {
		request = requestFactory.createPostRequest(TEST_HOST,
				updateEventQuery(testEventType.getId()),
				TEST_CONTENT_TYPE,
				TEST_ACCEPT_TYPE_PLAIN);

		String newQueryString = "";
		JsonObject jsonBody = new JsonObject();
		jsonBody.addProperty("eventQuery", newQueryString);

		request.setContent(serializer.toJson(jsonBody));
		assertEquals(ResponseFactory.getHttpErrorCode(), request.getResponseCode());
	}

	@Test
	public void testUpdateEventQuery_InvalidEventType_Forbidden() {

		EventType invalidEventType = ArgosTestUtil.createEventType();
		invalidEventType.setEditable(false);
		ArgosTestParent.argos.getPersistenceEntityManager().updateEntity(invalidEventType);

		request = requestFactory.createPostRequest(TEST_HOST,
				updateEventQuery(invalidEventType.getId()),
				TEST_CONTENT_TYPE,
				TEST_ACCEPT_TYPE_PLAIN);

		String newQueryString = "SELECT * FROM " + invalidEventType.getName();
		JsonObject jsonBody = new JsonObject();
		jsonBody.addProperty("eventQuery", newQueryString);

		request.setContent(serializer.toJson(jsonBody));
		assertEquals(ResponseFactory.getHttpForbiddenCode(), request.getResponseCode());
	}



	private String updateEventQuery(Object eventTypeId) {
		return EventQueryEndpoint.getUpdateEventQueryBaseUri().replaceAll(
				EventTypeEndpoint.getEventTypeIdParameter(true),
				eventTypeId.toString());
	}
}
