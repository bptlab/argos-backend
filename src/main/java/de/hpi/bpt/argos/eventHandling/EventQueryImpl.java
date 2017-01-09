package de.hpi.bpt.argos.eventHandling;


import com.google.gson.Gson;

public class EventQueryImpl implements EventQuery {
	protected static final Gson serializer = new Gson();
	protected static final String DEFAULT_NOTIFICATION_PATH = "http://localhost:8989/api/events/receiver";

	protected String queryString;
	protected String notificationPath;

	public EventQueryImpl(String queryString) {
		this(queryString, DEFAULT_NOTIFICATION_PATH);
	}

	public EventQueryImpl(String queryString, String notificationPath) {
		setQueryString(queryString);
		setNotificationPath(notificationPath);
	}

	@Override
	public String getQueryString() {
		return queryString;
	}

	@Override
	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	@Override
	public String getNotificationPath() {
		return notificationPath;
	}

	@Override
	public void setNotificationPath(String notificationPath) {
		this.notificationPath = notificationPath;
	}

	@Override
	public String toJson() {
		return serializer.toJson(this);
	}
}
