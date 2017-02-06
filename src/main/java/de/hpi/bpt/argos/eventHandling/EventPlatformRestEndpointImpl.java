package de.hpi.bpt.argos.eventHandling;

import de.hpi.bpt.argos.api.response.ResponseFactory;
import de.hpi.bpt.argos.api.response.ResponseFactoryImpl;
import de.hpi.bpt.argos.persistence.database.PersistenceEntityManager;
import de.hpi.bpt.argos.persistence.model.event.EventSubscriptionQuery;
import de.hpi.bpt.argos.persistence.model.event.EventSubscriptionQueryImpl;
import de.hpi.bpt.argos.persistence.model.event.attribute.EventAttribute;
import de.hpi.bpt.argos.persistence.model.event.attribute.EventAttributeImpl;
import de.hpi.bpt.argos.persistence.model.event.data.EventDataType;
import de.hpi.bpt.argos.persistence.model.event.type.EventType;
import de.hpi.bpt.argos.persistence.model.event.type.EventTypeImpl;
import spark.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class EventPlatformRestEndpointImpl implements EventPlatformRestEndpoint {

	protected PersistenceEntityManager entityManager;

	protected EventSubscriber eventSubscriber;
	protected ResponseFactory responseFactory;
	protected EventReceiver eventReceiver;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setup(PersistenceEntityManager entityManager, Service sparkService) {
		this.entityManager = entityManager;
		responseFactory = new ResponseFactoryImpl();
		responseFactory.setup(entityManager);

		createSimpleEventTypes();

		eventSubscriber = new EventSubscriberImpl();
		eventSubscriber.setup(entityManager);
		eventSubscriber.setupEventPlatform();

		eventReceiver = new EventReceiverImpl();
		eventReceiver.setup(responseFactory, entityManager, sparkService);
	}

	/**
	 * This method creates all simple event types.
	 */
	protected void createSimpleEventTypes() {
		List<EventType> existingEventTypes = entityManager.getEventTypes();
		List<EventType> newEventTypes = new ArrayList<>();

		newEventTypes.add(createFeedbackDataEventType());

		for(EventType existingType : existingEventTypes) {

			for(int i = 0; i < newEventTypes.size(); i++) {
				if (existingType.getName().equals(newEventTypes.get(i).getName())) {
					newEventTypes.remove(i);
					break;
				}
			}
		}

		for (EventType newEventType : newEventTypes) {
			entityManager.updateEntity(newEventType);
		}
	}

	/**
	 * This method creates a new event attribute.
	 * @param name - the name of the attribute
	 * @param type - the type of the attribute
	 * @return - the new event attribute
	 */
	protected EventAttribute createEventAttribute(String name, EventDataType type) {
		EventAttribute attribute = new EventAttributeImpl();
		attribute.setName(name);
		attribute.setType(type);

		return attribute;
	}

	/**
	 * This method creates a new simple event type.
	 * @param name - the name of the event type
	 * @return - the new event type
	 */
	protected EventType createSimpleEventType(String name) {
		EventType eventType = new EventTypeImpl();
		eventType.setName(name);

		EventSubscriptionQuery subscriptionQuery = new EventSubscriptionQueryImpl();
		subscriptionQuery.setQueryString(String.format("SELECT * FROM %1$s", name));

		eventType.setEventSubscriptionQuery(subscriptionQuery);

		return eventType;
	}

	/**
	 * This method creates the "FeedbackData" event type.
	 * @return - the new "FeedbackData" event type
	 */
	protected EventType createFeedbackDataEventType() {
		EventType eventType = createSimpleEventType("FeedbackData");

		List<EventAttribute> attributes = new ArrayList<>();

		EventAttribute timestampAttribute = createEventAttribute("dateOfServiceIntervention", EventDataType.DATE);
		EventAttribute productIdentificationAttribute = createEventAttribute("orderNumber", EventDataType.INTEGER);
		EventAttribute productFamilyIdentificationAttribute = createEventAttribute("productFamilyId", EventDataType.STRING);

		attributes.add(timestampAttribute);
		attributes.add(createEventAttribute("dateOfInstallation", EventDataType.DATE));
		attributes.add(createEventAttribute("dateOfProduction", EventDataType.DATE));
		attributes.add(createEventAttribute("factoryId", EventDataType.INTEGER));
		attributes.add(createEventAttribute("counter", EventDataType.INTEGER));
		attributes.add(createEventAttribute("softwareVersion", EventDataType.FLOAT));
		attributes.add(createEventAttribute("feedbackOfInstaller", EventDataType.STRING));
		attributes.add(createEventAttribute("objectId", EventDataType.INTEGER));
		attributes.add(createEventAttribute("locationOfDeviceId", EventDataType.INTEGER));
		attributes.add(productFamilyIdentificationAttribute);

		attributes.add(createEventAttribute("errorId", EventDataType.STRING));
		attributes.add(createEventAttribute("errorFailureTreeId", EventDataType.INTEGER));
		attributes.add(createEventAttribute("errorDescription", EventDataType.STRING));

		attributes.add(productIdentificationAttribute);
		attributes.add(createEventAttribute("productName", EventDataType.STRING));

		attributes.add(createEventAttribute("replacementPartId", EventDataType.INTEGER));
		attributes.add(createEventAttribute("replacementPartName", EventDataType.STRING));

		attributes.add(createEventAttribute("causeId", EventDataType.INTEGER));
		attributes.add(createEventAttribute("causeDescription", EventDataType.STRING));

		attributes.add(createEventAttribute("codingPlugId", EventDataType.INTEGER));
		attributes.add(createEventAttribute("codingPlugBusId", EventDataType.INTEGER));
		attributes.add(createEventAttribute("codingPlugSoftwareVersion", EventDataType.FLOAT));

		eventType.setAttributes(attributes);
		eventType.setTimestampAttribute(timestampAttribute);
		eventType.setProductIdentificationAttribute(productIdentificationAttribute);
		eventType.setProductFamilyIdentificationAttribute(productFamilyIdentificationAttribute);

		return eventType;
	}
}
