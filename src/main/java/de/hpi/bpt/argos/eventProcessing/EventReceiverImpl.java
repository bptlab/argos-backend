package de.hpi.bpt.argos.eventProcessing;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.hpi.bpt.argos.api.entity.EntityEndpoint;
import de.hpi.bpt.argos.common.ObservableImpl;
import de.hpi.bpt.argos.eventProcessing.mapping.EventEntityMappingStatus;
import de.hpi.bpt.argos.eventProcessing.mapping.EventEntityMappingStatusImpl;
import de.hpi.bpt.argos.storage.PersistenceAdapterImpl;
import de.hpi.bpt.argos.storage.dataModel.attribute.Attribute;
import de.hpi.bpt.argos.storage.dataModel.attribute.AttributeImpl;
import de.hpi.bpt.argos.storage.dataModel.attribute.type.TypeAttribute;
import de.hpi.bpt.argos.storage.dataModel.event.Event;
import de.hpi.bpt.argos.storage.dataModel.event.EventImpl;
import de.hpi.bpt.argos.storage.dataModel.event.type.EventType;
import de.hpi.bpt.argos.util.HttpStatusCodes;
import de.hpi.bpt.argos.util.RestEndpointUtilImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.HaltException;
import spark.Request;
import spark.Response;
import spark.Service;

import java.util.ArrayList;
import java.util.List;

import static spark.Spark.halt;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class EventReceiverImpl extends ObservableImpl<EventCreationObserver> implements EventReceiver {
	private static final Logger logger = LoggerFactory.getLogger(EventReceiverImpl.class);
	private static final JsonParser jsonParser = new JsonParser();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setup(Service sparkService) {
		sparkService.post(EventReceiver.getReceiveEventBaseUri(), this::receiveEvent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String receiveEvent(Request request, Response response) {
		RestEndpointUtilImpl.getInstance().logReceivedRequest(logger, request);

		try {
			long eventTypeId = RestEndpointUtilImpl.getInstance().validateLong(
					request.params(EventReceiver.getEventTypeIdParameter(false)),
					(Long input) -> input > 0);

			EventType eventType = PersistenceAdapterImpl.getInstance().getEventType(eventTypeId);

			if (eventType == null) {
				halt(HttpStatusCodes.NOT_FOUND, "event type id was not found");
			} else {
				createEvent(request.body(), eventType);
			}

		} catch (HaltException e) {
			RestEndpointUtilImpl.getInstance().logSendingResponse(logger, request, e.statusCode(), e.getMessage());
			throw e;
		}

		RestEndpointUtilImpl.getInstance().logSendingResponse(logger, request, HttpStatusCodes.SUCCESS, "");

		return "";
	}

	/**
	 * This method creates a new event from a given request body.
	 * @param requestBody - the request body to parse
	 * @param eventType - the eventType of the new event
	 */
	private void createEvent(String requestBody, EventType eventType) {
		List<TypeAttribute> eventTypeAttributes = PersistenceAdapterImpl.getInstance().getTypeAttributes(eventType.getId());

		JsonObject serializedEvent = jsonParser.parse(requestBody).getAsJsonObject();

		Event event = new EventImpl();
		event.setTypeId(eventType.getId());
		if (!PersistenceAdapterImpl.getInstance().saveArtifacts(event)) {
			halt(HttpStatusCodes.ERROR, "cannot create new event in database");
		}

		List<Attribute> eventAttributes = new ArrayList<>();

		for (TypeAttribute typeAttribute : eventTypeAttributes) {
			Attribute attribute = new AttributeImpl();

			attribute.setOwnerId(event.getId());
			attribute.setTypeAttributeId(typeAttribute.getId());
			attribute.setValue(serializedEvent.get(typeAttribute.getName()).getAsString());

			eventAttributes.add(attribute);
		}

		if (!PersistenceAdapterImpl.getInstance().saveArtifacts(eventAttributes.toArray(new Attribute[eventAttributes.size()]))) {
			PersistenceAdapterImpl.getInstance().deleteArtifacts(event);
			halt(HttpStatusCodes.ERROR, "cannot create event attributes in database");
		}

		EventEntityMappingStatus mappingStatus = new EventEntityMappingStatusImpl();

		notifyObservers((EventCreationObserver observer) ->
				observer.onEventCreated(mappingStatus, eventType, eventTypeAttributes, event, eventAttributes));

		if (!mappingStatus.isMapped() || event.getEntityId() == 0) {
			PersistenceAdapterImpl.getInstance().deleteArtifacts(eventAttributes.toArray(new Attribute[eventAttributes.size()]));
			PersistenceAdapterImpl.getInstance().deleteArtifacts(event);
			logger.info("cannot map event to entity");
			logger.trace(String.format("event body: '%1$s'", requestBody));
		} else {
			int numberOfEvents = PersistenceAdapterImpl.getInstance().getEventCountOfEntity(mappingStatus.getEventOwner().getId(), eventType.getId());

			// this event will now be stored with the corresponding owner id and therefore will be the next eventIndex in the list of all events
			PersistenceAdapterImpl.getInstance().createArtifact(event,
					EntityEndpoint.getEventsOfEntityUri(mappingStatus.getEventOwner().getId(),
							eventType.getId(),
							numberOfEvents + 1,
							numberOfEvents + 1));
		}
	}
}
