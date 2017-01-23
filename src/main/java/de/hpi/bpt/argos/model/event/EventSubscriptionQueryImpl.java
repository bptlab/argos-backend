package de.hpi.bpt.argos.model.event;

import org.hibernate.annotations.NaturalId;

import javax.persistence.*;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
@Entity
@Table(name = "EventSubscriptionQuery")
public class EventSubscriptionQueryImpl implements EventSubscriptionQuery {

	@Id
	@GeneratedValue
	@Column(name = "Id")
	protected int id;

	@NaturalId
	@Column(name = "Uuid")
	protected String uuid;

	@Column(name = "QueryString")
	protected String queryString;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getId() {
		return id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setId(int id) {
		this.id = id;
	}

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
