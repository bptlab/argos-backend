package de.hpi.bpt.argos.persistence.model.event;


import javax.persistence.*;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
@Entity
@Table(name = "EventAttribute")
public class EventAttributeImpl implements EventAttribute {

	@Id
	@GeneratedValue
	@Column(name = "Id")
	protected int id;

	@Column(name = "Name")
	protected String name;

	@Column(name = "Type")
	protected EventDataType type;

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
	public String getName() {
		return name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventDataType getType() {
		return type;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setType(EventDataType type) {
		this.type = type;
	}
}
