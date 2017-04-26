package de.hpi.bpt.argos.eventProcessing;

import de.hpi.bpt.argos.storage.dataModel.attribute.Attribute;
import de.hpi.bpt.argos.storage.dataModel.attribute.type.TypeAttribute;
import de.hpi.bpt.argos.storage.dataModel.event.Event;
import de.hpi.bpt.argos.storage.dataModel.event.type.EventType;

import java.util.List;

/**
 * This interface represents observers, which want to be notified, whenever a new event was created.
 */
@FunctionalInterface
public interface EventCreationObserver {

	/**
	 * This method gets called, whenever a new event is created.
	 * @param eventType - the eventType of the new event
	 * @param eventTypeAttributes - a list of all typeAttributes of the eventType
	 * @param event - the new event itself
	 * @param eventAttributes - a list of all attributes of the new event
	 */
	void onEventCreated(EventType eventType, List<TypeAttribute> eventTypeAttributes, Event event, List<Attribute> eventAttributes);
}
