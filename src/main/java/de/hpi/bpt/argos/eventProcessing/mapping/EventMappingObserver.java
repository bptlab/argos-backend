package de.hpi.bpt.argos.eventProcessing.mapping;

import de.hpi.bpt.argos.storage.dataModel.entity.Entity;
import de.hpi.bpt.argos.storage.dataModel.event.Event;

/**
 * This interface represents observers, which get notified whenever a new event gets mapped to a specific entity.
 */
public interface EventMappingObserver {

	/**
	 * This method gets called, whenever a new event got mapped to a specific entity.
	 * @param event - the new event, which just got mapped
	 * @param entity - the entity to which the new event belongs
	 */
	void onEventMapped(Event event, Entity entity);
}
