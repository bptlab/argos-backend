package de.hpi.bpt.argos.model.product;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
@Entity
@Table(name = "ProductFamily")
public class ProductFamilyImpl implements ProductFamily {

	@Id
	@GeneratedValue
	@Column(name = "Id")
	protected int id;

	@Column(name = "Name")
	protected String name;

	@Column(name = "Brand")
	protected String brand;

	@OneToMany(mappedBy = "Id", fetch = FetchType.LAZY)
	protected Set<Product> products = new HashSet<>();

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
	public Set<Product> getProducts() {
		return products;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setProducts(Set<Product> products) {
		this.products = products;
	}
}
