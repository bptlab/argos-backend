package de.hpi.bpt.argos.persistence.model.event;

import de.hpi.bpt.argos.persistence.database.PersistenceEntity;
import de.hpi.bpt.argos.persistence.model.event.data.EventData;
import de.hpi.bpt.argos.persistence.model.event.type.EventType;
import de.hpi.bpt.argos.persistence.model.product.ProductConfiguration;

import java.util.List;

/**
 * This interface represents the events. It extends persistence entity.
 */
public interface Event extends PersistenceEntity {

	/**
	 * This method return the related product configuration.
	 * @return - the related product configuration
	 */
	ProductConfiguration getProductConfiguration();

	/**
	 * This method sets related product configuration.
	 * @param productConfiguration - the related product configuration to be set
	 */
	void setProductConfiguration(ProductConfiguration productConfiguration);

	/**
	 * This method returns the event eventType of this event.
	 * @return - the event eventType of this event as an EventyType object
	 */
	EventType getEventType();

	/**
	 * This method sets the event eventType of this event.
	 * @param type - the event eventType
	 */
	void setEventType(EventType type);

	/**
	 * This method returns the event data for this event.
	 * @return - a list of event data
	 */
	List<EventData> getEventData();

	/**
	 * This method sets the event data of this event.
	 * @param eventData - the event data to be set
	 */
	void setEventData(List<EventData> eventData);
}
