package de.hpi.bpt.argos.model.product;

import com.google.gson.Gson;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class ProductFamilyMetaDataImpl implements ProductFamilyMetaData {
	protected static final Gson serializer = new Gson();

	protected String name;
	protected String brand;

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
	public String toJson() {
		return serializer.toJson(this);
	}
}
