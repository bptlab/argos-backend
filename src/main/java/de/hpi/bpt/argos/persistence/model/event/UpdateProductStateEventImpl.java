package de.hpi.bpt.argos.persistence.model.event;

import de.hpi.bpt.argos.persistence.database.PersistenceEntityImpl;
import de.hpi.bpt.argos.persistence.model.product.Product;
import de.hpi.bpt.argos.persistence.model.product.ProductImpl;
import de.hpi.bpt.argos.persistence.model.product.ProductState;


import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Date;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
@Entity
@Table(name = "UpdateProductStateEvent")
public class UpdateProductStateEventImpl extends PersistenceEntityImpl implements UpdateProductStateEvent {

	@ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, targetEntity = ProductImpl.class)
	protected Product product;

	@Column(name = "State")
	protected ProductState state = ProductState.RUNNING;

	@Column(name = "Timestamp")
	protected Date timestamp = new Date();

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
	public void setProduct(Product product) {
		this.product = product;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProductState getState() {
		return state;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setState(ProductState state) {
		this.state = state;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Date getTimestamp() {
		return timestamp;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
}
