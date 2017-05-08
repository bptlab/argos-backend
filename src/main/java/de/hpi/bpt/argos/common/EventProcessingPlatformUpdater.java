package de.hpi.bpt.argos.common;

import de.hpi.bpt.argos.properties.PropertyEditorImpl;
import de.hpi.bpt.argos.storage.dataModel.event.query.EventQuery;
import de.hpi.bpt.argos.storage.dataModel.event.type.EventType;

/**
 * This class defines the interface between our backend and the eventProcessingPlatform for any message we want to send.
 */
public interface EventProcessingPlatformUpdater {

	String EVENT_PROCESSING_PLATFORM_HOST_PROPERTY_KEY = "eventProcessingPlatformHost";
	String EVENT_PROCESSING_PLATFORM_REGISTER_EVENT_TYPE_URI_PROPERTY_KEY = "eventProcessingPlatformRegisterEventTypeUri";
	String EVENT_PROCESSING_PLATFORM_REGISTER_EVENT_QUERY_URI_PROPERTY_KEY = "eventProcessingPlatformRegisterEventQueryUri";
	String EVENT_PROCESSING_PLATFORM_DELETE_EVENT_TYPE_URI_PROPERTY_KEY = "eventProcessingPlatformDeleteEventTypeUri";
	String EVENT_PROCESSING_PLATFORM_DELETE_EVENT_QUERY_URI_PROPERTY_KEY = "eventProcessingPlatformDeleteEventQueryUri";

	/**
	 * This method sets up the eventProcessingPlatform by registering all eventTypes and eventQueries.
	 */
	void setup();

	/**
	 * This method registers a given eventType in the eventProcessingPlatform.
	 * @param eventType - the eventType, which should be registered
	 * @return - the feedback of the eventProcessingPlatform
	 */
	EventPlatformFeedback registerEventType(EventType eventType);

	/**
	 * This method deletes a given eventType in the eventProcessingPlatform.
	 * @param eventType - the eventType to be deleted
	 * @return - the feedback of the eventProcessingPlatform
	 */
	EventPlatformFeedback deleteEventType(EventType eventType);

	/**
	 * This method registers a given eventQuery in the eventProcessingPlatform. If the query is registered already, it will be re-registered.
	 * @param eventTypeId - the unique identifier of the eventType, which contains the eventQuery
	 * @param eventQuery - the eventQuery to be registered
	 * @return - the feedback of the eventProcessingPlatform
	 */
	EventPlatformFeedback registerEventQuery(long eventTypeId, EventQuery eventQuery);

	/**
	 * This method deletes a given eventQuery in the eventProcessingPlatform.
	 * @param eventQuery - the eventQuery to be deleted
	 * @return - the feedback of the eventProcessingPlatform
	 */
	EventPlatformFeedback deleteEventQuery(EventQuery eventQuery);

	/**
	 * This method reads the host property from the properties-file and returns it's value.
	 * @return - the host, specified in the properties-file
	 */
	static String getHost() {
		return PropertyEditorImpl.getInstance().getProperty(EVENT_PROCESSING_PLATFORM_HOST_PROPERTY_KEY);
	}

	/**
	 * This method reads the registerEventTypeUri property from the properties-file and returns it's value.
	 * @return - the registerEventTypeUri, specified in the properties-file
	 */
	static String getRegisterEventTypeUri() {
		return PropertyEditorImpl.getInstance().getProperty(EVENT_PROCESSING_PLATFORM_REGISTER_EVENT_TYPE_URI_PROPERTY_KEY);
	}

	/**
	 * This method reads the registerEventQueryUri property from the properties-file and returns it's value.
	 * @return - the registerEventQueryUri, specified in the properties-file
	 */
	static String getRegisterEventQueryUri() {
		return PropertyEditorImpl.getInstance().getProperty(EVENT_PROCESSING_PLATFORM_REGISTER_EVENT_QUERY_URI_PROPERTY_KEY);
	}

	/**
	 * This method reads the deleteEventTypeUri property from the properties-file and returns it's value.
	 * @param eventTypeName - the name of the eventType to be placed in the uri
	 * @return - the deleteEventTypeUri, specified in the properties-file
	 */
	static String getDeleteEventTypeUri(String eventTypeName) {
		return PropertyEditorImpl.getInstance().getProperty(EVENT_PROCESSING_PLATFORM_DELETE_EVENT_TYPE_URI_PROPERTY_KEY)
				.replaceAll(":1", eventTypeName);
	}

	/**
	 * This method reads the deleteEventQueryUri property from the properties-file and returns it's value.
	 * @param eventQueryUuid - the uuid to be placed in the uri
	 * @return - the deleteEventQueryUri, specified in the properties-file
	 */
	static String getDeleteEventQueryUri(String eventQueryUuid) {
		return PropertyEditorImpl.getInstance().getProperty(EVENT_PROCESSING_PLATFORM_DELETE_EVENT_QUERY_URI_PROPERTY_KEY)
				.replaceAll(":1", eventQueryUuid);
	}
}
