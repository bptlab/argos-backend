package de.hpi.bpt.argos.persistence.model.event.type;

import de.hpi.bpt.argos.persistence.database.DatabaseConnection;
import de.hpi.bpt.argos.persistence.model.event.*;
import de.hpi.bpt.argos.persistence.model.event.attribute.EventAttribute;
import de.hpi.bpt.argos.persistence.model.event.attribute.EventAttributeImpl;
import de.hpi.bpt.argos.persistence.model.event.data.EventDataType;

import java.util.ArrayList;
import java.util.List;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class EventTypeFactoryImpl implements EventTypeFactory {

	protected DatabaseConnection databaseConnection;

	public EventTypeFactoryImpl(DatabaseConnection databaseConnection) {
		this.databaseConnection = databaseConnection;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDatabaseConnection(DatabaseConnection databaseConnection) {
		this.databaseConnection = databaseConnection;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createSimpleEventTypes() {
		List<EventType> existingEventTypes = databaseConnection.listEvenTypes();
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

		databaseConnection.saveEventTypes(newEventTypes);
	}

	protected EventAttribute createEventAttribute(String name, EventDataType type) {
		EventAttribute attribute = new EventAttributeImpl();
		attribute.setName(name);
		attribute.setType(type);

		return attribute;
	}

	protected EventType createSimpleEventType(String name) {
		EventType eventType = new EventTypeImpl();
		eventType.setName(name);

		EventSubscriptionQuery subscriptionQuery = new EventSubscriptionQueryImpl();
		subscriptionQuery.setQueryString(String.format("SELECT * FROM %1$s", name));

		eventType.setEventSubscriptionQuery(subscriptionQuery);

		return eventType;
	}

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
