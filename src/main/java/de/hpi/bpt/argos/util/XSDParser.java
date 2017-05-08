package de.hpi.bpt.argos.util;

import de.hpi.bpt.argos.storage.dataModel.attribute.type.TypeAttribute;
import de.hpi.bpt.argos.storage.dataModel.event.type.EventType;

import java.util.List;

/**
 * This interface offers methods to parse eventTypes to a xsd format.
 */
@FunctionalInterface
public interface XSDParser {

	/**
	 * This method returns a string representation of the specific event type.
	 * @param eventType - the event type
	 * @param typeAttributes - a list of all typeAttributes of the given eventType
	 * @return - a string representation of the event type
	 */
	String getEventTypeSchema(EventType eventType, List<TypeAttribute> typeAttributes);
}
