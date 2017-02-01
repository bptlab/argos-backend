package de.hpi.bpt.argos.persistence.model.event;

import de.hpi.bpt.argos.persistence.database.PersistenceEntityImpl;

import javax.persistence.*;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
@Entity
@Table(name = "EventSubscriptionQuery")
public class EventSubscriptionQueryImpl extends PersistenceEntityImpl implements EventSubscriptionQuery {

	@Column(name = "Uuid")
	protected String uuid = "";

	@Column(name = "QueryString")
	protected String queryString = "";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getUuid() {
		return uuid;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setUuid(String uuid) {
		this.uuid = uuid;
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
}
