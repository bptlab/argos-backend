package de.hpi.bpt.argos.persistence.model.event.type;

import de.hpi.bpt.argos.eventHandling.schema.EventTypeSchemaGenerator;
import de.hpi.bpt.argos.eventHandling.schema.EventTypeSchemaGeneratorImpl;
import de.hpi.bpt.argos.persistence.database.PersistenceEntityImpl;
import de.hpi.bpt.argos.persistence.model.event.Event;
import de.hpi.bpt.argos.persistence.model.event.EventImpl;
import de.hpi.bpt.argos.persistence.model.event.EventSubscriptionQuery;
import de.hpi.bpt.argos.persistence.model.event.EventSubscriptionQueryImpl;
import de.hpi.bpt.argos.persistence.model.event.attribute.EventAttribute;
import de.hpi.bpt.argos.persistence.model.event.attribute.EventAttributeImpl;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
@Entity
@Table(name = "EventType")
public class EventTypeImpl extends PersistenceEntityImpl implements EventType {

	protected static final EventTypeSchemaGenerator schemaGenerator = new EventTypeSchemaGeneratorImpl();

	@Column(name = "Name")
	protected String name = "";

	@OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, targetEntity = EventSubscriptionQueryImpl.class)
	protected EventSubscriptionQuery eventSubscriptionQuery;

	@ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, targetEntity = EventAttributeImpl.class)
	@Fetch(value = FetchMode.SUBSELECT)
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
