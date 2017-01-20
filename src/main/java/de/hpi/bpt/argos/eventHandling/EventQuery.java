package de.hpi.bpt.argos.eventHandling;

import de.hpi.bpt.argos.serialization.Serializable;

/**
 * This interface represents a event query saved on the event processing platform.
 */
public interface EventQuery extends Serializable {
    /**
     * This method is a getter for the event query string.
     * @return - returns the event query as a string
     */
	String getQueryString();

    /**
     * This method is a setter for the event query string.
     * @param queryString - event query to be set.
     */
	void setQueryString(String queryString);

    /**
     * This method is a getter for the url on which the event processing platform notifies about new events.
     * @return - returns a notification url as a string
     */
    //TODO: refactor to notification URL rather than notification path
	String getNotificationPath();

    /**
     * This method is a setter for the url on which the event processing platform notifies about new events.
     * @param notificationPath - the notification url to be set
     */
	void setNotificationPath(String notificationPath);
}
