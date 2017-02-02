package de.hpi.bpt.argos.persistence.model.event;

import de.hpi.bpt.argos.persistence.database.PersistenceEntity;

/**
 * This interface represents event subscription queries. It extends persistence entity.
 */
public interface EventSubscriptionQuery extends PersistenceEntity {

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
