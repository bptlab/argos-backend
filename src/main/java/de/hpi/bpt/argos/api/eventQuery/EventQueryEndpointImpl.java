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

    private static final String JSON_EVENT_QUERY_ATTRIBUTE = "EventQuery";

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

        try {
            JsonObject jsonBody = jsonParser.parse(request.body()).getAsJsonObject();
            JsonObject jsonEventQuery = jsonBody.get(JSON_EVENT_QUERY_ATTRIBUTE).getAsJsonObject();

            if (jsonEventQuery == null) {
                halt(HttpStatusCodes.BAD_REQUEST, "no event query given in body");
            }

            EventQuery eventQuery = null;
            try {
                eventQuery = new EventQueryImpl();
                eventQuery.setTypeId(jsonEventQuery.get("EventTypeId").getAsLong());
                eventQuery.setDescription(jsonEventQuery.get("Description").getAsString());
                eventQuery.setQuery(jsonEventQuery.get("Query").getAsString());
            } catch (Exception e) {
                halt(HttpStatusCodes.BAD_REQUEST, "failed to parse event query");
            }

            EventPlatformFeedback feedback = EventProcessingPlatformUpdaterImpl.getInstance().registerEventQuery(eventQuery.getTypeId(), eventQuery);

            if (!feedback.isSuccessful()) {
                halt(HttpStatusCodes.ERROR, String.format("cannot register event type: %1$s", feedback.getResponseText()));
            }

            PersistenceAdapterImpl.getInstance().createArtifact(eventQuery, EventTypeEndpoint.getEventTypeQueriesUri(eventQuery.getId()));

        } catch (HaltException halt) {
            logger.info(String.format("cannot create event query: %1$s -> %2$s", halt.statusCode(), halt.body()));
            throw halt;
        } catch (Exception e) {
            LoggerUtilImpl.getInstance().error(logger, "cannot parse request body to event query", e);
            halt(HttpStatusCodes.ERROR, e.getMessage());
        }
        endpointUtil.logSendingResponse(logger, request, response.status(), response.body());
        return "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String deleteEventQuery(Request request, Response response) {
        endpointUtil.logReceivedRequest(logger, request);

        long eventQueryId = getEventQueryId(request);
        EventQuery eventQuery = PersistenceAdapterImpl.getInstance().getEventQuery(eventQueryId);
        if (eventQuery == null) {
            halt(HttpStatusCodes.ERROR, "Given query was not found");
        }

        EventPlatformFeedback feedback = EventProcessingPlatformUpdaterImpl.getInstance().deleteEventQuery(eventQuery);
        if (!feedback.isSuccessful()) {
            halt(HttpStatusCodes.ERROR, String.format("cannot unregister event query: %1$s", feedback.getResponseText()));
        }

        if (!PersistenceAdapterImpl.getInstance().deleteArtifact(eventQuery,
                EventTypeEndpoint.getEventTypeQueriesUri(eventQuery.getId()))) {
            halt(HttpStatusCodes.ERROR, "Could not delete query");
        }

        endpointUtil.logSendingResponse(logger, request, response.status(), response.body());
        return "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String editEventQuery(Request request, Response response) {
        endpointUtil.logReceivedRequest(logger, request);

        JsonObject jsonBody = new JsonObject();
        try {
            jsonBody = jsonParser.parse(request.body()).getAsJsonObject();
        } catch (JsonSyntaxException e) {
            LoggerUtilImpl.getInstance().error(logger, "cannot parse request body to event query", e);
            halt(HttpStatusCodes.BAD_REQUEST, "not a valid json");
        }

        JsonObject jsonEventQuery = jsonBody.get(JSON_EVENT_QUERY_ATTRIBUTE).getAsJsonObject();
        if (jsonEventQuery == null) {
            halt(HttpStatusCodes.BAD_REQUEST, "no event query given in body");
        }

        EventQuery newEventQuery = getEventQueryFromJson(jsonEventQuery);
        if (newEventQuery == null) {
            halt(HttpStatusCodes.BAD_REQUEST, "failed to parse event query");
        }

        long oldEventQueryId = getEventQueryId(request);
        EventQuery oldEventQuery = PersistenceAdapterImpl.getInstance().getEventQuery(oldEventQueryId);
        if (oldEventQuery == null) {
            halt(HttpStatusCodes.ERROR, "Given query was not found");
        }

        EventPlatformFeedback deleteFeedback = EventProcessingPlatformUpdaterImpl.getInstance().deleteEventQuery(oldEventQuery);
        if (!deleteFeedback.isSuccessful()) {
            halt(HttpStatusCodes.ERROR, "Given query could not be unregistered");
        }

        EventPlatformFeedback createFeedback = EventProcessingPlatformUpdaterImpl.getInstance().registerEventQuery(newEventQuery.getTypeId(), newEventQuery);
        if (!createFeedback.isSuccessful()) {
            halt(HttpStatusCodes.ERROR, "Given query could not be newly registered");
        }

        if (!PersistenceAdapterImpl.getInstance().updateArtifact(newEventQuery,EventTypeEndpoint.getEventTypeQueriesUri(newEventQuery.getId()))) {
            halt(HttpStatusCodes.ERROR, "Failed to save new query into database");
        }

        endpointUtil.logSendingResponse(logger, request, response.status(), response.body());
        return "";
    }

    /**
     * This method returns an event query derived from a json
     * @param jsonEventQuery json describing the event query
     * @return event query from json
     */
    private EventQuery getEventQueryFromJson(JsonObject jsonEventQuery) {
        EventQuery newEventQuery;
        try {
            newEventQuery = new EventQueryImpl();
            newEventQuery.setTypeId(jsonEventQuery.get("EventTypeId").getAsLong());
            newEventQuery.setDescription(jsonEventQuery.get("Description").getAsString());
            newEventQuery.setQuery(jsonEventQuery.get("Query").getAsString());
        } catch (Exception e) {
            LoggerUtilImpl.getInstance().error(logger, "cannot parse request body to event query", e);
            return null;
        }
        return newEventQuery;
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
