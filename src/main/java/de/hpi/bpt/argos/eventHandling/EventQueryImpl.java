package de.hpi.bpt.argos.eventHandling;


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
     * @param queryString - event query string to be set
     */
	public EventQueryImpl(String queryString) {
		this(queryString, DEFAULT_NOTIFICATION_PATH);
	}

    /**
     * This is a constructor for the EventQueryImpl object and sets the event query string, using the given
     * notification url.
     * @param queryString - event query string to be set
	 * @param notificationPath - notification path to be called from the event processing platform
     */
    public EventQueryImpl(String queryString, String notificationPath) {
		setQueryString(queryString);
		setNotificationPath(notificationPath);
	}

	/**
	 * This is a constructor for the EventQueryImpl object and sets the event query string, appending the event type id to the default notification
	 * url.
	 * @param queryString - event query string to be set
	 * @param eventTypeId - event type id to be appended to the default notification url
	 */
	public EventQueryImpl(String queryString, int eventTypeId) {
		this(queryString, String.format("%1$s/%2$s", DEFAULT_NOTIFICATION_PATH, eventTypeId));
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
