package de.hpi.bpt.argos.persistence.model.event.type;

import de.hpi.bpt.argos.eventHandling.schema.EventTypeSchemaGenerator;
import de.hpi.bpt.argos.eventHandling.schema.EventTypeSchemaGeneratorImpl;
import de.hpi.bpt.argos.persistence.model.event.*;
import de.hpi.bpt.argos.persistence.model.event.attribute.EventAttribute;
import de.hpi.bpt.argos.persistence.model.event.attribute.EventAttributeImpl;
import de.hpi.bpt.argos.persistence.model.event.data.Event;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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
	protected String name = "";

	@OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, targetEntity = EventSubscriptionQueryImpl.class)
	protected EventSubscriptionQuery eventSubscriptionQuery;

	@ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, targetEntity = EventAttributeImpl.class)
	protected List<EventAttribute> attributes = new ArrayList<>();

	@ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, targetEntity = EventAttributeImpl.class)
	protected EventAttribute timestampAttribute;

	@ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, targetEntity = EventAttributeImpl.class)
	protected EventAttribute productIdentificationAttribute;

	@ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, targetEntity = EventAttributeImpl.class)
	protected EventAttribute productFamilyIdentificationAttribute;

	@OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, targetEntity = EventImpl.class)
	protected List<Event> events = new ArrayList<>();

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
	public List<EventAttribute> getAttributes() {
		return attributes;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setAttributes(List<EventAttribute> attributes) {
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
	public void setTimestampAttribute(EventAttribute eventAttribute) {
		this.timestampAttribute = eventAttribute;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventAttribute getProductIdentificationAttribute() {
		return productIdentificationAttribute;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setProductIdentificationAttribute(EventAttribute eventAttribute) {
		this.productIdentificationAttribute = eventAttribute;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventAttribute getProductFamilyIdentificationAttribute() {
		return productFamilyIdentificationAttribute;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setProductFamilyIdentificationAttribute(EventAttribute eventAttribute) {
		this.productFamilyIdentificationAttribute = eventAttribute;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Event> getEvents() {
		return events;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setEvents(List<Event> events) {
		this.events = events;
	}
}
