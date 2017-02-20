package de.hpi.bpt.argos.persistence.model.event;

import de.hpi.bpt.argos.persistence.database.PersistenceEntity;

/**
 * This interface represents event queries. It extends persistence entity.
 */
public interface EventQuery extends PersistenceEntity {

	/**
	 * This method returns the unique identifier of the event source for this event query.
	 * @return - the unique identifier if the event source for this event query
	 */
	String getUuid();

	/**
	 * This method sets the unique identifier of the event source for this event query.
	 * @param uuid - the unique identifier to be set
	 */
	void setUuid(String uuid);

	/**
	 * This method return the query string of this event query.
	 * @return - the query string of this event query
	 */
	String getQueryString();

	/**
	 * This method sets the query string of this event query.
	 * @param queryString - the event string to be set
	 */
	void setQueryString(String queryString);
}
