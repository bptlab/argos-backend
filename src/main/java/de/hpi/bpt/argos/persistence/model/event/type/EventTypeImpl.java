package de.hpi.bpt.argos.persistence.model.event.type;

import de.hpi.bpt.argos.eventHandling.schema.EventTypeSchemaGenerator;
import de.hpi.bpt.argos.eventHandling.schema.EventTypeSchemaGeneratorImpl;
import de.hpi.bpt.argos.persistence.database.PersistenceEntityImpl;
import de.hpi.bpt.argos.persistence.model.event.EventQuery;
import de.hpi.bpt.argos.persistence.model.event.EventQueryImpl;
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

	@OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, targetEntity = EventQueryImpl.class)
	protected EventQuery eventQuery = new EventQueryImpl();

	@ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, targetEntity = EventAttributeImpl.class)
	@Fetch(value = FetchMode.SUBSELECT)
	protected List<EventAttribute> attributes = new ArrayList<>();

	@ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, targetEntity = EventAttributeImpl.class)
	protected EventAttribute timestampAttribute;

	@Column(name = "Editable")
	protected boolean editable = true;

	@Column(name = "Deletable")
	protected boolean deletable = true;

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
	public EventQuery getEventQuery() {
		return eventQuery;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setEventQuery(EventQuery eventQuery) {
		this.eventQuery = eventQuery;
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
	public EventAttribute getAttribute(String attributeName) {
		for (EventAttribute attribute : attributes) {
			if (attribute.getName().equals(attributeName)) {
				return attribute;
			}
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEditable() {
		return editable;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDeletable() {
		return deletable;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDeletable(boolean deletable) {
		this.deletable = deletable;
	}
}
