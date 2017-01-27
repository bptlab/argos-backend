package de.hpi.bpt.argos.eventHandling;

import de.hpi.bpt.argos.common.RestEndpointImpl;
import de.hpi.bpt.argos.common.validation.RestInputValidationService;
import de.hpi.bpt.argos.common.validation.RestInputValidationServiceImpl;
import de.hpi.bpt.argos.persistence.database.DatabaseConnection;
import de.hpi.bpt.argos.persistence.model.event.*;
import de.hpi.bpt.argos.persistence.model.event.data.Event;
import de.hpi.bpt.argos.persistence.model.event.type.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class EventReceiverImpl extends RestEndpointImpl implements EventReceiver {
	private static final Logger logger = LoggerFactory.getLogger(EventReceiverImpl.class);

	protected static final RestInputValidationService inputValidation = new RestInputValidationServiceImpl();
	protected static final String POST_EVENT = "/api/events/receiver/:eventTypeId";

	protected static EventFactory eventFactory;
	protected DatabaseConnection databaseConnection;

	/**
	 * This is a constructor for creating a new event receiver instance.
	 */
	public EventReceiverImpl(DatabaseConnection databaseConnection) {
		this.databaseConnection = databaseConnection;
		eventFactory = new EventFactoryImpl(databaseConnection);
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public void setup(Service sparkService) {
		sparkService.post(POST_EVENT, this::receiveEvent);
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public String receiveEvent(Request request, Response response) {
		int eventTypeId = inputValidation.validateInteger(request.params("eventTypeId"), (Integer input) -> { return input >= 0; });

		EventType eventType = databaseConnection.getEventType(eventTypeId);
		Event event = eventFactory.getEvent(eventType, request.body());

		List<Event> events = new ArrayList<>();
		events.add(event);

		databaseConnection.saveEvents(events);
		logInfoForReceivedEvent(request);
		return finishRequest();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDatabaseConnection(DatabaseConnection databaseConnection) {
		this.databaseConnection = databaseConnection;
	}

	/**
     * This message logs a given head on info level.
     * @param head - string to be logged
     */
    protected void logInfo(String head) {
		logger.info(head);
	}

    /**
     * This method logs an info for every received event.
     * @param request - request received from event processing platform
     */
	protected void logInfoForReceivedEvent(Request request) {
		logInfo("received event: (body) " + request.body() + "	(sender) " + request.ip());
	}
}
