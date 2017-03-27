package de.hpi.bpt.argos.persistence.model.event;

import de.hpi.bpt.argos.persistence.database.PersistenceEntityImpl;
import de.hpi.bpt.argos.persistence.model.event.data.EventData;
import de.hpi.bpt.argos.persistence.model.event.data.EventDataImpl;
import de.hpi.bpt.argos.persistence.model.event.type.EventType;
import de.hpi.bpt.argos.persistence.model.event.type.EventTypeImpl;
import de.hpi.bpt.argos.persistence.model.product.ProductConfiguration;
import de.hpi.bpt.argos.persistence.model.product.ProductConfigurationImpl;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
@Entity
@Table(name = "Event")
public class EventImpl extends PersistenceEntityImpl implements Event {

	@ManyToOne(cascade = {CascadeType.ALL}, targetEntity = ProductConfigurationImpl.class)
	@JoinColumn(name = "product_configuration_Id")
	protected ProductConfiguration productConfiguration;

	@ManyToOne(cascade = {CascadeType.ALL}, targetEntity = EventTypeImpl.class)
	protected EventType eventType;

	@OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, targetEntity = EventDataImpl.class)
	protected List<EventData> eventData = new ArrayList<>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProductConfiguration getProductConfiguration() {
		return productConfiguration;
	}

	/**
	 * {@inheritDoc}
	 * @param productConfiguration
	 */
	@Override
	public void setProductConfiguration(ProductConfiguration productConfiguration) {
		this.productConfiguration = productConfiguration;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventType getEventType() {
		return eventType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<EventData> getEventData() {
		return eventData;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setEventData(List<EventData> eventData) {
		this.eventData = eventData;
	}
}
