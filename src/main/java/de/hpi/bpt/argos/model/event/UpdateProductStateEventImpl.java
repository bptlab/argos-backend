package de.hpi.bpt.argos.model.event;

import de.hpi.bpt.argos.model.product.Product;
import de.hpi.bpt.argos.model.product.ProductImpl;
import de.hpi.bpt.argos.model.product.ProductState;

import javax.persistence.*;
import java.util.Date;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
@Entity
@Table(name = "UpdateProductStateEvent")
public class UpdateProductStateEventImpl implements UpdateProductStateEvent {

	@Id
	@GeneratedValue
	@Column(name = "Id")
	protected int id;

	@ManyToOne(fetch = FetchType.LAZY, targetEntity = ProductImpl.class)
	protected Product product;

	@Column(name = "State")
	protected ProductState state;

	@Column(name = "Timestamp")
	protected Date timestamp;

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
