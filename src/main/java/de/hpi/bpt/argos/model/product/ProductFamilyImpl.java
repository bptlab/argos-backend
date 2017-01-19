package de.hpi.bpt.argos.model.product;

import com.google.gson.Gson;

import java.util.HashSet;
import java.util.Set;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class ProductFamilyImpl implements ProductFamily {
	protected static final Gson serializer = new Gson();

	protected int id;
	protected ProductFamilyMetaData metaData;
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
	public ProductFamilyMetaData getMetaData() {
		return metaData;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMetaData(ProductFamilyMetaData metaData) {
		this.metaData = metaData;
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toJson() {
		return serializer.toJson(this);
	}
}
