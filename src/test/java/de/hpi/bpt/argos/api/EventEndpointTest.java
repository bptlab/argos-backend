package de.hpi.bpt.argos.api;

import com.google.gson.JsonObject;
import de.hpi.bpt.argos.api.event.EventEndpoint;
import de.hpi.bpt.argos.api.response.ResponseFactory;
import de.hpi.bpt.argos.core.ArgosTestUtil;
import de.hpi.bpt.argos.persistence.model.event.Event;
import de.hpi.bpt.argos.persistence.model.event.data.EventData;
import de.hpi.bpt.argos.persistence.model.event.type.EventType;
import de.hpi.bpt.argos.persistence.model.product.Product;
import de.hpi.bpt.argos.persistence.model.product.ProductFamily;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EventEndpointTest extends CustomerEndpointParentClass {

    protected static Event testEvent;

    @BeforeClass
	public static void createTestEvent() {
		EventType eventType = ArgosTestUtil.createEventType();
		ProductFamily productFamily = ArgosTestUtil.createProductFamily();
		Product product = ArgosTestUtil.createProduct(productFamily);
		testEvent = ArgosTestUtil.createEvent(eventType, product);
	}

    @Test
    public void testGetEvent() {
    	request = requestFactory.createGetRequest(TEST_HOST,
				getEvent(testEvent.getId()),
				TEST_ACCEPT_TYPE_JSON);
    	assertEquals(ResponseFactory.HTTP_SUCCESS_CODE, request.getResponseCode());

    	JsonObject jsonEvent = jsonParser.parse(request.getResponse()).getAsJsonObject();
    	assertEquals(testEvent.getId(), jsonEvent.get("id").getAsLong());

    	for (EventData data : testEvent.getEventData()) {
    		assertEquals(data.getValue(), jsonEvent.get(data.getEventAttribute().getName()).getAsString());
		}
    }

    @Test
	public void testGetEvent_InvalidId_NotFound() {
		request = requestFactory.createRequest(TEST_HOST,
				getEvent(testEvent.getId() - 1),
				TEST_REQUEST_METHOD,
				TEST_CONTENT_TYPE,
				TEST_ACCEPT_TYPE_PLAIN);
		assertEquals(ResponseFactory.HTTP_NOT_FOUND_CODE, request.getResponseCode());
	}

    private String getEvent(Object eventId) {
        return EventEndpoint.getEventBaseUri().replaceAll(EventEndpoint.getEventIdParameter(true), eventId.toString());
    }
}