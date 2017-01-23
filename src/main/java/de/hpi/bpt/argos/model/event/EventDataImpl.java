package de.hpi.bpt.argos.model.event;

import org.hibernate.annotations.NaturalId;

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

	@ManyToOne(targetEntity = EventAttributeImpl.class)
	protected EventAttribute eventAttribute;

	@ManyToOne(targetEntity = EventImpl.class)
	protected Event event;

	@Column(name = "Value")
	protected String value;

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
	public Event getEvent() {
		return event;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setEvent(Event event) {
		this.event = event;
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
