package de.hpi.bpt.argos.api.eventQuery;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import de.hpi.bpt.argos.api.eventType.EventTypeEndpoint;
import de.hpi.bpt.argos.api.eventType.EventTypeEndpointImpl;
import de.hpi.bpt.argos.common.EventPlatformFeedback;
import de.hpi.bpt.argos.common.EventProcessingPlatformUpdaterImpl;
import de.hpi.bpt.argos.storage.PersistenceAdapterImpl;
import de.hpi.bpt.argos.storage.dataModel.event.query.EventQuery;
import de.hpi.bpt.argos.storage.dataModel.event.query.EventQueryImpl;
import de.hpi.bpt.argos.util.HttpStatusCodes;
import de.hpi.bpt.argos.util.LoggerUtilImpl;
import de.hpi.bpt.argos.util.RestEndpointUtil;
import de.hpi.bpt.argos.util.RestEndpointUtilImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.HaltException;
import spark.Request;
import spark.Response;
import spark.Service;

import static spark.Spark.halt;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class EventQueryEndpointImpl  implements EventQueryEndpoint {


    private static final Logger logger = LoggerFactory.getLogger(EventTypeEndpointImpl.class);
    private static final RestEndpointUtil endpointUtil = RestEndpointUtilImpl.getInstance();
    private static final JsonParser jsonParser = new JsonParser();

    private static final String JSON_PARSE_ERROR_MESSAGE = "cannot parse request body to event query";

    /**
     * {@inheritDoc}
     */
    @Override
    public void setup(Service sparkService) {
        sparkService.post(EventQueryEndpoint.getCreateEventQueryBaseUri(), this::createEventQuery);
        sparkService.delete(EventQueryEndpoint.getDeleteEventQueryBaseUri(), this::deleteEventQuery);
        sparkService.put(EventQueryEndpoint.getEditEventQueryBaseUri(), this::editEventQuery);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String createEventQuery(Request request, Response response) {
        endpointUtil.logReceivedRequest(logger, request);

        response.body("");

        try {
            JsonObject jsonEventQuery = jsonParser.parse(request.body()).getAsJsonObject();

            if (jsonEventQuery == null) {
                halt(HttpStatusCodes.BAD_REQUEST, "no event query given in body");
            }

            EventQuery eventQuery = getEventQueryFromJson(jsonEventQuery, true);
            if (eventQuery == null) {
                halt(HttpStatusCodes.BAD_REQUEST, "cannot parse event query");
            }

            checkEventTypeExists(eventQuery);

            EventPlatformFeedback feedback = EventProcessingPlatformUpdaterImpl.getInstance().registerEventQuery(eventQuery.getTypeId(), eventQuery);

            if (!feedback.isSuccessful()) {
                halt(HttpStatusCodes.ERROR, String.format("cannot register event type: %1$s", feedback.getResponseText()));
            }

            PersistenceAdapterImpl.getInstance().createArtifact(eventQuery, EventTypeEndpoint.getEventTypeQueriesUri(eventQuery.getId()));

        } catch (HaltException halt) {
            LoggerUtilImpl.getInstance().error(logger,
                    String.format("cannot create event query: %1$s -> %2$s", halt.statusCode(), halt.body()), halt);
            throw halt;
        } catch (Exception e) {
            LoggerUtilImpl.getInstance().error(logger, JSON_PARSE_ERROR_MESSAGE, e);
            halt(HttpStatusCodes.ERROR, e.getMessage());
        }
        endpointUtil.logSendingResponse(logger, request, response.status(), response.body());
        return response.body();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String deleteEventQuery(Request request, Response response) {
        endpointUtil.logReceivedRequest(logger, request);

		response.body("");

        long eventQueryId = getEventQueryId(request);
        EventQuery eventQuery = PersistenceAdapterImpl.getInstance().getEventQuery(eventQueryId);
        if (eventQuery == null) {
            halt(HttpStatusCodes.BAD_REQUEST, "given query was not found");
        }

        EventPlatformFeedback feedback = EventProcessingPlatformUpdaterImpl.getInstance().deleteEventQuery(eventQuery);
        if (!feedback.isSuccessful()) {
            halt(HttpStatusCodes.ERROR, String.format("cannot unregister event query: %1$s", feedback.getResponseText()));
        }

        if (!PersistenceAdapterImpl.getInstance().deleteArtifact(eventQuery,
                EventTypeEndpoint.getEventTypeQueriesUri(eventQuery.getId()))) {
            halt(HttpStatusCodes.ERROR, "could not delete query");
        }

        endpointUtil.logSendingResponse(logger, request, response.status(), response.body());
        return response.body();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String editEventQuery(Request request, Response response) {
        endpointUtil.logReceivedRequest(logger, request);

		response.body("");

        JsonObject jsonEventQuery = new JsonObject();
        try {
            jsonEventQuery = jsonParser.parse(request.body()).getAsJsonObject();
        } catch (JsonSyntaxException e) {
            LoggerUtilImpl.getInstance().error(logger, JSON_PARSE_ERROR_MESSAGE, e);
            halt(HttpStatusCodes.BAD_REQUEST, "not a valid json");
        }

        if (jsonEventQuery == null) {
            halt(HttpStatusCodes.BAD_REQUEST, "no event query given in body");
        }

        EventQuery newEventQuery = getEventQueryFromJson(jsonEventQuery, false);
        if (newEventQuery == null) {
            halt(HttpStatusCodes.BAD_REQUEST, "failed to parse event query");
        }

        long oldEventQueryId = getEventQueryId(request);
        EventQuery oldEventQuery = PersistenceAdapterImpl.getInstance().getEventQuery(oldEventQueryId);
        if (oldEventQuery == null) {
            halt(HttpStatusCodes.BAD_REQUEST, "given query was not found");
        }

        EventPlatformFeedback deleteFeedback = EventProcessingPlatformUpdaterImpl.getInstance().deleteEventQuery(oldEventQuery);
        if (!deleteFeedback.isSuccessful()) {
            halt(HttpStatusCodes.ERROR, "given query could not be unregistered");
        }

        oldEventQuery.setDescription(newEventQuery.getDescription());
        oldEventQuery.setQuery(newEventQuery.getQuery());

        EventPlatformFeedback createFeedback = EventProcessingPlatformUpdaterImpl.getInstance()
                .registerEventQuery(oldEventQuery.getTypeId(), oldEventQuery);
        if (!createFeedback.isSuccessful()) {
            halt(HttpStatusCodes.ERROR, "given query could not be newly registered");
        }

        if (!PersistenceAdapterImpl.getInstance().updateArtifact(oldEventQuery, EventTypeEndpoint.getEventTypeQueriesUri(oldEventQuery.getId()))) {
            halt(HttpStatusCodes.ERROR, "failed to save new query into database");
        }

        endpointUtil.logSendingResponse(logger, request, response.status(), response.body());
        return response.body();
    }

    /**
     * This method returns an event query derived from a json.
     * @param jsonEventQuery json describing the event query
	 * @param includeEventTypeId true, if the eventTypeId attribute is included in the json
     * @return event query from json
     */
    private EventQuery getEventQueryFromJson(JsonObject jsonEventQuery, boolean includeEventTypeId) {
        EventQuery newEventQuery;
        try {
            newEventQuery = new EventQueryImpl();

            if (includeEventTypeId) {
				newEventQuery.setTypeId(jsonEventQuery.get("EventTypeId").getAsLong());
			}

            newEventQuery.setDescription(jsonEventQuery.get("Description").getAsString());
            newEventQuery.setQuery(jsonEventQuery.get("Query").getAsString());
        } catch (Exception e) {
            LoggerUtilImpl.getInstance().error(logger, JSON_PARSE_ERROR_MESSAGE, e);
            return null;
        }
        return newEventQuery;
    }

	/**
	 * This method checks whether the eventType for a given eventQuery exists.
	 * @param eventQuery - the query to check
	 */
	private void checkEventTypeExists(EventQuery eventQuery) {
		if (PersistenceAdapterImpl.getInstance().getEventType(eventQuery.getTypeId()) == null) {
			halt(HttpStatusCodes.BAD_REQUEST, "event type id invalid");
		}
	}

    /**
     * This method returns the query id given in request.
     * @param request the request with event query id
     * @return id of the event query in request
     */
    private long getEventQueryId(Request request) {
        return endpointUtil.validateLong(
                request.params(EventQueryEndpoint.getEventQueryIdParameter(false)),
                (Long input) -> input > 0);
    }
}
