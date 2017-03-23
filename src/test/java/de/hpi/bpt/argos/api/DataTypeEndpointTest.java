package de.hpi.bpt.argos.api;

import com.google.gson.JsonArray;
import de.hpi.bpt.argos.api.dataType.DataTypeEndpoint;
import de.hpi.bpt.argos.api.response.ResponseFactory;
import de.hpi.bpt.argos.persistence.model.event.data.EventDataType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DataTypeEndpointTest extends CustomerEndpointParentClass {

	@Test
	public void testGetEventTypes() {
		request = requestFactory.createGetRequest(TEST_HOST,
				DataTypeEndpoint.getDataTypesBaseUri(),
				TEST_ACCEPT_TYPE_JSON);

		assertEquals(ResponseFactory.HTTP_SUCCESS_CODE, request.getResponseCode());

		JsonArray types = jsonParser.parse(request.getResponse()).getAsJsonArray();
		List<EventDataType> eventDataTypesLeft = new ArrayList<>(Arrays.asList(EventDataType.values()));

		for (int i = 0; i < types.size(); i++) {
			for (EventDataType dataType : EventDataType.values()) {
				if (types.get(i).getAsString().equalsIgnoreCase(dataType.toString())) {
					eventDataTypesLeft.remove(dataType);
					break;
				}
			}
		}

		assertEquals(true, eventDataTypesLeft.isEmpty());
	}
}
