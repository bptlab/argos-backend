package de.hpi.bpt.argos.model.event;

import javax.persistence.MappedSuperclass;

/**
 * This interface represents event subscription queries.
 */
@MappedSuperclass
public interface EventSubscriptionQuery {

	/**
	 * This method return the unique identifier of this event subscription query.
	 * @return - the unique identifier of this event subscription query
	 */
	int getId();

	/**
	 * This method sets the unique identifier of this event subscription query.
	 * @param id - the unique identifier to be set
	 */
	void setId(int id);

	/**
	 * This method returns the unique identifier of the event source for this event subscription query.
	 * @return - the unique identifier if the event source for this event subscription query
	 */
	String getUuid();

	/**
	 * This method sets the unique identifier of the event source for this event subscription query.
	 * @param uuid - the unique identifier to be set
	 */
	void setUuid(String uuid);

	/**
	 * This method return the query string of this event subscription query.
	 * @return - the query string of this event subscription query
	 */
	String getQueryString();

	/**
	 * This method sets the query string of this event subscription query.
	 * @param queryString - the event string to be set
	 */
	void setQueryString(String queryString);
}
