package de.hpi.bpt.argos.persistence.model.event.data;

import de.hpi.bpt.argos.persistence.database.PersistenceEntityImpl;
import de.hpi.bpt.argos.persistence.model.event.attribute.EventAttribute;
import de.hpi.bpt.argos.persistence.model.event.attribute.EventAttributeImpl;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


/**
 * {@inheritDoc}
 * This is the implementation.
 */
@Entity
@Table(name = "EventData")
public class EventDataImpl extends PersistenceEntityImpl implements EventData {

	@ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, targetEntity = EventAttributeImpl.class)
	protected EventAttribute eventAttribute;

	@Column(name = "Value")
	protected String value = "";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventAttribute getEventAttribute() {
		return eventAttribute;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setEventAttribute(EventAttribute eventAttribute) {
		this.eventAttribute = eventAttribute;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getValue() {
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setValue(String value) {
		this.value = value;
	}
}
