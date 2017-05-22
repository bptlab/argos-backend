package de.hpi.bpt.argos.eventProcessing.status;

import de.hpi.bpt.argos.eventProcessing.EventReceiver;
import de.hpi.bpt.argos.eventProcessing.mapping.EventMappingObserver;

/**
 * This interface represents observers, who listen to eventEntityMappings and calculate a new entity status accordingly.
 */
public interface EntityStatusCalculator extends EventMappingObserver {

	/**
	 * This method sets up the statusCalculator by subscribing to the eventEntityMapper.
	 * @param eventReceiver - the eventReceiver, which notifies about event mappings
	 */
	void setup(EventReceiver eventReceiver);
}
