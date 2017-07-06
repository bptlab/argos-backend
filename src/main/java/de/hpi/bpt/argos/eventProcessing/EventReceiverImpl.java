package de.hpi.bpt.argos.eventProcessing;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.hpi.bpt.argos.api.entity.EntityEndpoint;
import de.hpi.bpt.argos.common.Observable;
import de.hpi.bpt.argos.common.ObservableImpl;
import de.hpi.bpt.argos.eventProcessing.mapping.EventEntityMappingStatus;
import de.hpi.bpt.argos.eventProcessing.mapping.EventEntityMappingStatusImpl;
import de.hpi.bpt.argos.eventProcessing.mapping.EventMappingObserver;
import de.hpi.bpt.argos.storage.PersistenceAdapterImpl;
import de.hpi.bpt.argos.storage.dataModel.attribute.Attribute;
import de.hpi.bpt.argos.storage.dataModel.attribute.AttributeImpl;
import de.hpi.bpt.argos.storage.dataModel.attribute.type.TypeAttribute;
import de.hpi.bpt.argos.storage.dataModel.event.Event;
import de.hpi.bpt.argos.storage.dataModel.event.EventImpl;
import de.hpi.bpt.argos.storage.dataModel.event.type.EventType;
import de.hpi.bpt.argos.util.HttpStatusCodes;
import de.hpi.bpt.argos.util.Pair;
import de.hpi.bpt.argos.util.PairImpl;
import de.hpi.bpt.argos.util.RestEndpointUtil;
import de.hpi.bpt.argos.util.RestEndpointUtilImpl;
import de.hpi.bpt.argos.util.performance.WatchImpl;
import de.hpi.bpt.argos.util.threading.BackgroundWorkerPool;
import de.hpi.bpt.argos.util.threading.BackgroundWorkerPoolImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static spark.Spark.halt;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class EventReceiverImpl implements EventReceiver {
	private static final Logger logger = LoggerFactory.getLogger(EventReceiverImpl.class);
	private static final RestEndpointUtil endpointUtil = RestEndpointUtilImpl.getInstance();
	private static final JsonParser jsonParser = new JsonParser();

	private ObservableImpl<EventCreationObserver> eventCreationObservable;
	private ObservableImpl<EventMappingObserver> eventMappingObservable;
	private BackgroundWorkerPool<Pair<EventType, String>> eventProcessingPool;

	/**
	 * This constructor initializes all members with their default values.
	 */
	public EventReceiverImpl() {
		eventCreationObservable = new ObservableImpl<>(ObservableImpl.ObserverCallOrder.LAST_IN_FIRST_OUT);
		eventMappingObservable = new ObservableImpl<>(ObservableImpl.ObserverCallOrder.LAST_IN_FIRST_OUT);
		eventProcessingPool = BackgroundWorkerPoolImpl.Create(EventReceiver.getEventProcessingThreads(),
				(Pair<EventType, String> eventData)
						-> WatchImpl.measure("process event", () -> createEvent(eventData.getValue(), eventData.getKey())));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setup(Service sparkService) {
		sparkService.post(EventReceiver.getReceiveEventBaseUri(),
				(Request request, Response response) ->
						endpointUtil.executeRequest(logger, request, response, this::receiveEvent));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String receiveEvent(Request request, Response response) {
		long eventTypeId = RestEndpointUtilImpl.getInstance().validateLong(
				request.params(EventReceiver.getEventTypeIdParameter(false)),
				(Long input) -> input > 0);

		EventType eventType = PersistenceAdapterImpl.getInstance().getEventType(eventTypeId);

		if (eventType == null) {
			halt(HttpStatusCodes.NOT_FOUND, "event type id was not found");
		} else {
			eventProcessingPool.addWorkload(new PairImpl<>(eventType, request.body()));
		}

		return "";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Observable<EventCreationObserver> getEventCreationObservable() {
		return eventCreationObservable;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Observable<EventMappingObserver> getEventMappingObservable() {
		return eventMappingObservable;
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

			if (!serializedEvent.has(typeAttribute.getName())) {
				if (eventType.getTimeStampAttributeId() == typeAttribute.getId()) {
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
					attribute.setValue(dateFormat.format(new Date()));
				} else {
					attribute.setValue("");
				}
			} else {
				attribute.setValue(serializedEvent.get(typeAttribute.getName()).getAsString());
			}

			eventAttributes.add(attribute);
		}

		if (!PersistenceAdapterImpl.getInstance().saveArtifacts(eventAttributes.toArray(new Attribute[eventAttributes.size()]))) {
			PersistenceAdapterImpl.getInstance().deleteArtifacts(event);
			halt(HttpStatusCodes.ERROR, "cannot create event attributes in database");
		}

		EventEntityMappingStatus eventCreationStatus = new EventEntityMappingStatusImpl(event);

		eventCreationObservable.notifyObservers((EventCreationObserver observer) ->
				observer.onEventCreated(eventCreationStatus, eventType, eventTypeAttributes, event, eventAttributes));

		if (eventCreationStatus.isMapped()) {
			eventMappingObservable.notifyObservers((EventMappingObserver observer) -> observer.onEventMapped(eventCreationStatus));
		}

		if (!eventCreationStatus.isMapped() || event.getEntityId() == 0) {
			PersistenceAdapterImpl.getInstance().deleteArtifacts(eventAttributes.toArray(new Attribute[eventAttributes.size()]));
			PersistenceAdapterImpl.getInstance().deleteArtifacts(event);
			logger.info("cannot map event to entity");
			logger.trace(String.format("event body: '%1$s'", requestBody));
			halt(HttpStatusCodes.BAD_REQUEST, "cannot map event to entity");
		} else {
			int numberOfEvents = PersistenceAdapterImpl.getInstance().getEventCountOfEntity(eventCreationStatus.getEventOwner().getId(), eventType.getId());

			if (eventCreationStatus.getStatusUpdateStatus().isStatusUpdated()) {
				eventCreationStatus.getEventOwner()
						.setStatus(eventCreationStatus.getStatusUpdateStatus().getNewStatus(), eventCreationStatus.getEvent());
				PersistenceAdapterImpl.getInstance()
						.updateArtifact(eventCreationStatus.getEventOwner(), EntityEndpoint.getEntityUri(eventCreationStatus.getEventOwner().getId()));
			}

			// this event will now be stored with the corresponding owner id and therefore will be the next eventIndex in the list of all events
			String fetchUri = EntityEndpoint
					.getEventsOfEntityUri(
							eventCreationStatus.getEventOwner().getId(),
							eventType.getId(),
							false,
							numberOfEvents,
							numberOfEvents + 1);

			PersistenceAdapterImpl.getInstance().createEvent(event, eventCreationStatus.getEventOwner(), fetchUri);
		}
	}
}
