package de.hpi.bpt.argos.eventProcessing.mapping;

import de.hpi.bpt.argos.common.Observable;
import de.hpi.bpt.argos.eventProcessing.EventCreationObserver;
import de.hpi.bpt.argos.eventProcessing.EventReceiver;

/**
 * This interface represents the mapper, which finds the corresponding entity for a given event.
 */
public interface EventEntityMapper extends EventCreationObserver, Observable<EventMappingObserver> {

	/**
	 * This method sets up the mapper.
	 * @param eventReceiver - the eventReceiver, which notifies this class on event creation
	 */
	void setup(EventReceiver eventReceiver);
}
