package de.hpi.bpt.argos.persistence.model.event;

import de.hpi.bpt.argos.eventHandling.schema.EventTypeSchemaGenerator;
import de.hpi.bpt.argos.eventHandling.schema.EventTypeSchemaGeneratorImpl;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
@Entity
@Table(name = "EventType")
public class EventTypeImpl implements EventType {

	protected static final EventTypeSchemaGenerator schemaGenerator = new EventTypeSchemaGeneratorImpl();

	@Id
	@GeneratedValue
	@Column(name = "Id")
	protected  int id;

	@Column(name = "Name")
	protected String name;

	@OneToOne(fetch = FetchType.EAGER, targetEntity = EventSubscriptionQueryImpl.class)
	protected EventSubscriptionQuery eventSubscriptionQuery;

	@ManyToMany(fetch = FetchType.EAGER, targetEntity = EventAttributeImpl.class)
	protected Set<EventAttribute> attributes = new HashSet<>();

	@ManyToOne(fetch = FetchType.EAGER, targetEntity = EventAttributeImpl.class)
	protected EventAttribute timestampAttribute;

	@OneToMany(fetch = FetchType.LAZY, targetEntity = EventImpl.class)
	protected Set<Event> events = new HashSet<>();

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
	public String getSchema() {
		return schemaGenerator.getEventTypeSchema(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventSubscriptionQuery getEventSubscriptionQuery() {
		return eventSubscriptionQuery;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setEventSubscriptionQuery(EventSubscriptionQuery eventSubscriptionQuery) {
		this.eventSubscriptionQuery = eventSubscriptionQuery;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<EventAttribute> getAttributes() {
		return attributes;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setAttributes(Set<EventAttribute> attributes) {
		this.attributes = attributes;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventAttribute getTimestampAttribute() {
		return timestampAttribute;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTimetampAttribute(EventAttribute eventAttribute) {
		this.timestampAttribute = eventAttribute;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<Event> getEvents() {
		return events;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setEvents(Set<Event> events) {
		this.events = events;
	}
}
