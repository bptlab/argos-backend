package de.hpi.bpt.argos.persistence.model.event.statusUpdate;

import de.hpi.bpt.argos.persistence.model.event.type.EventType;

public interface StatusUpdateEventType extends EventType {

	/**
	 * This method returns the name of the timestamp attribute.
	 * @return - the name of the timestamp attribute
	 */
	static String getTimestampAttributeName() {
		return "timestamp";
	}

	/**
	 * This method return the name of the new status attribute.
	 * @return - the name of the new status attribute
	 */
	static String getNewStatusAttributeName() {
		return "newStatus";
	}

	/**
	 * This method return the name of the old status attribute.
	 * @return - the name of the old status attribute
	 */
	static String getOldStatusAttributeName() {
		return "oldStatus";
	}
}
