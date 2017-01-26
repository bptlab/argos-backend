package de.hpi.bpt.argos.persistence.model.event;

import de.hpi.bpt.argos.persistence.model.product.Product;
import de.hpi.bpt.argos.persistence.model.product.ProductImpl;

import javax.persistence.*;
import java.util.Map;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
@Entity
@Table(name = "Event")
public class EventImpl implements Event {

	@Id
	@GeneratedValue
	@Column(name = "Id")
	protected int id;

	@ManyToOne(targetEntity = ProductImpl.class)
	@JoinColumn(name = "product_Id")
	protected Product product;

	@ManyToOne(targetEntity = EventTypeImpl.class)
	protected EventType eventType;
	
	protected Map<EventAttribute, EventData> eventData;

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
	public Product getProduct() {
		return product;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setProductId(Product product) {
		this.product = product;
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
	public Map<EventAttribute, EventData> getEventData() {
		return eventData;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setEventData(Map<EventAttribute, EventData> eventData) {
		this.eventData = eventData;
	}
}
