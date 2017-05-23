package de.hpi.bpt.argos.eventProcessing.creation;

import de.hpi.bpt.argos.common.EventPlatformFeedback;
import de.hpi.bpt.argos.common.EventPlatformFeedbackImpl;
import de.hpi.bpt.argos.common.EventProcessingPlatformUpdater;
import de.hpi.bpt.argos.common.RestRequest;
import de.hpi.bpt.argos.common.RestRequestFactoryImpl;
import de.hpi.bpt.argos.storage.dataModel.event.type.EventType;
import de.hpi.bpt.argos.util.Pair;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public final class EventFactoryImpl implements EventFactory {

	private static EventFactory instance;

	private SimpleDateFormat dateFormat;

	/**
	 * This constructor hides the default public one to implement the singleton pattern.
	 */
	private EventFactoryImpl() {
		dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	/**
	 * This method returns the singleton instance of this class.
	 * @return - the singleton instance of this class
	 */
	public static EventFactory getInstance() {
		if (instance == null) {
			instance = new EventFactoryImpl();
		}

		return instance;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventPlatformFeedback postEvent(EventType eventType, String timestampName, Pair<String, Object>... attributes) {
		String eventString = attachProperty("", timestampName, dateFormat.format(new Date()));

		for (Pair<String, Object> attribute : attributes) {
			eventString = attachProperty(eventString, attribute.getKey(), String.valueOf(attribute.getValue()));
		}

		eventString = finishEvent(eventString, eventType.getName());

		RestRequest postRequest = RestRequestFactoryImpl.getInstance()
				.createPostRequest(EventProcessingPlatformUpdater.getHost(), EventFactory.getPostEventUri());

		postRequest.setContent(eventString);

		return new EventPlatformFeedbackImpl(postRequest);
	}

	/**
	 * This method finishes a started event.
	 * @param event - the event (which should contain all attributes)
	 * @param eventTypeName - the name of the eventType
	 * @return - the finished event string
	 */
	private String finishEvent(String event, String eventTypeName) {
		return String.format(
				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"
				+ "<cpoi xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
				+ "xsi:noNamespaceSchemaLocation=\"%1$s.xsd\">"
				+ "%2$s"
				+ "</cpoi>",
				eventTypeName,
				event);
	}

	/**
	 * This method attaches a property to a given event string.
	 * @param event - the event string
	 * @param propertyName - the name of the property to attach
	 * @param propertyValue - the value of the property to attach
	 * @return - the new event string
	 */
	private String attachProperty(String event, String propertyName, String propertyValue) {
		return String.format(
				"%1$s"
				+ "<%2$s>%3$s</%2$s>",
				event,
				propertyName,
				propertyValue);
	}
}
