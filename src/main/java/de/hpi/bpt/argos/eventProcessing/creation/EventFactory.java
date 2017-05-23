package de.hpi.bpt.argos.eventProcessing.creation;

import de.hpi.bpt.argos.common.EventPlatformFeedback;
import de.hpi.bpt.argos.properties.PropertyEditorImpl;
import de.hpi.bpt.argos.storage.dataModel.event.type.EventType;
import de.hpi.bpt.argos.util.Pair;

/**
 * This interface offers method to create events, which can be send to the eventProcessingPlatform.
 */
@FunctionalInterface
public interface EventFactory {

	String EVENT_PROCESSING_PLATFORM_POST_EVENT_URI_PROPERTY_KEY = "eventProcessingPlatformPostEventUri";

	/**
	 * This method creates and sends an event to the eventProcessingPlatform.
	 * @param eventType - the eventType of the event to create
	 * @param timestampName - the name of the timestampAttribute
	 * @param attributes - a list of Name -> Value attributes for the event
	 * @return - the feedback of the eventProcessingPlatform
	 */
	EventPlatformFeedback postEvent(EventType eventType, String timestampName, Pair<String, Object>... attributes);

	/**
	 * This method reads the postEventUri property from the properties-file and returns it's value.
	 * @return - the postEventUri, specified in the properties-file
	 */
	static String getPostEventUri() {
		return PropertyEditorImpl.getInstance().getProperty(EVENT_PROCESSING_PLATFORM_POST_EVENT_URI_PROPERTY_KEY);
	}
}
