package de.hpi.bpt.argos.event_handling;


import com.google.gson.Gson;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class EventQueryImpl implements EventQuery {
	protected static final Gson serializer = new Gson();
	protected static final String DEFAULT_NOTIFICATION_PATH = "http://localhost:8989/api/events/receiver";

	protected String queryString;
	protected String notificationPath;

    /**
     * This is a constructor for the EventQueryImpl object and sets the event query string, using the default
     * notification url.
     * @param queryString
     */
	public EventQueryImpl(String queryString) {
		this(queryString, DEFAULT_NOTIFICATION_PATH);
	}

    /**
     * This is a constructor for the EventQueryImpl object and sets the event query string, using the given
     * notification url.
     * @param queryString
     */
    public EventQueryImpl(String queryString, String notificationPath) {
		setQueryString(queryString);
		setNotificationPath(notificationPath);
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public String getQueryString() {
		return queryString;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public String getNotificationPath() {
		return notificationPath;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public void setNotificationPath(String notificationPath) {
		this.notificationPath = notificationPath;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public String toJson() {
		return serializer.toJson(this);
	}
}
