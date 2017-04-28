package de.hpi.bpt.argos.eventProcessing.status;

import de.hpi.bpt.argos.common.Observable;
import de.hpi.bpt.argos.eventProcessing.mapping.EventMappingObserver;

/**
 * This interface represents observers, who listen to eventEntityMappings and calculate a new entity status accordingly.
 */
public interface EntityStatusCalculator extends EventMappingObserver {

	/**
	 * This method sets up the statusCalculator by subscribing to the eventEntityMapper.
	 * @param eventEntityMapper - the eventEntityMapper to subscribe to
	 */
	void setup(Observable<EventMappingObserver> eventEntityMapper);
}
