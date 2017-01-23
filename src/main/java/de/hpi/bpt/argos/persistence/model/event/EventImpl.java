package de.hpi.bpt.argos.persistence.model.event;

import de.hpi.bpt.argos.persistence.model.product.Product;
import de.hpi.bpt.argos.persistence.model.product.ProductImpl;

import javax.persistence.*;

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

	@ManyToOne(fetch = FetchType.LAZY, targetEntity = ProductImpl.class)
	protected Product product;

	@ManyToOne(fetch = FetchType.LAZY, targetEntity = EventTypeImpl.class)
	protected EventType type;

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
	public EventType getType() {
		return type;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setEventType(EventType type) {
		this.type = type;
	}
}
