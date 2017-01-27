package de.hpi.bpt.argos.persistence.model.event.data;

import de.hpi.bpt.argos.persistence.model.event.attribute.EventAttribute;
import de.hpi.bpt.argos.persistence.model.event.attribute.EventAttributeImpl;

import javax.persistence.*;

/**
 * {@inheritDoc}
 * This is the implementation
 */
@Entity
@Table(name = "EventData")
public class EventDataImpl implements EventData {

	@Id
	@GeneratedValue
	@Column(name = "Id")
	protected int id;

	@ManyToOne(cascade = {CascadeType.ALL}, targetEntity = EventAttributeImpl.class)
	protected EventAttribute eventAttribute;

	@Column(name = "Value")
	protected String value = "";

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
