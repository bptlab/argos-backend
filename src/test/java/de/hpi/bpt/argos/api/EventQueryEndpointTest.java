package de.hpi.bpt.argos.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.hpi.bpt.argos.api.eventQueries.EventQueryEndpoint;
import de.hpi.bpt.argos.api.eventTypes.EventTypeEndpoint;
import de.hpi.bpt.argos.common.RestRequest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EventQueryEndpointTest extends EndpointParentClass {
	protected static final Gson serializer = new Gson();

	protected RestRequest request;

	@Test
	public void testUpdateEventQuery() {
		// enable test mode to mock unicorn
		argos.setTestMode(true);

		request = requestFactory.createPostRequest(TEST_HOST, updateEventQuery(1), TEST_CONTENT_TYPE, TEST_ACCEPT_TYPE);

		JsonObject jsonBody = new JsonObject();
		jsonBody.addProperty("eventQuery", "");

		request.setContent(serializer.toJson(jsonBody));

		assertEquals(HTTP_INVALID_REQUEST_RESPONSE_CODE ,request.getResponseCode());
		argos.setTestMode(false);
	}

	private String updateEventQuery(Object eventTypeId) {
		return EventQueryEndpoint.getUpdateEventQueryBaseUri().replaceAll(
				EventTypeEndpoint.getEventTypeIdParameter(true),
				eventTypeId.toString());
	}
}
