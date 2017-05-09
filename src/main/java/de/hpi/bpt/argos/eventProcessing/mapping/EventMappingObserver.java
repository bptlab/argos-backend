package de.hpi.bpt.argos.eventProcessing.mapping;

/**
 * This interface represents observers, which get notified whenever a new event gets mapped to a specific entity.
 */
@FunctionalInterface
public interface EventMappingObserver {

	/**
	 * This method gets called, whenever a new event got mapped to a specific entity.
	 * @param processStatus - this holds the status of the mapping and status change process
	 */
	void onEventMapped(EventEntityMappingStatus processStatus);
}
