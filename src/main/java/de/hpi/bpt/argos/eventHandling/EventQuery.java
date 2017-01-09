package de.hpi.bpt.argos.eventHandling;

import de.hpi.bpt.argos.serialization.Serializable;

public interface EventQuery extends Serializable {
	String getQueryString();

	void setQueryString(String queryString);

	String getNotificationPath();

	void setNotificationPath(String notificationPath);
}
