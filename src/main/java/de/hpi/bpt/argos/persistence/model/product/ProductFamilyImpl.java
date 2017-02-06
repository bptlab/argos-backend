package de.hpi.bpt.argos.persistence.model.product;

import de.hpi.bpt.argos.persistence.database.PersistenceEntityImpl;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
@Entity
@Table(name = "ProductFamily")
public class ProductFamilyImpl extends PersistenceEntityImpl implements ProductFamily {

	@Column(name = "Name")
	protected String name = "";

	@Column(name = "Brand")
	protected String brand = "";

	@OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, targetEntity = ProductImpl.class)
	protected List<Product> products = new ArrayList<>();

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
	public String getBrand() {
		return brand;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setBrand(String brand) {
		this.brand = brand;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Product> getProducts() {
		return products;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setProducts(List<Product> products) {
		this.products = products;
	}

	@Override
	public void addProduct(Product product) {
		this.products.add(product);
	}
}
