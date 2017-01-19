package de.hpi.bpt.argos.model;

import com.google.gson.Gson;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class ProductFamilyMetaDataImpl implements ProductFamilyMetaData {
	protected static final Gson serializer = new Gson();

	protected String label;
	protected String brand;
	protected int orderNumber;
	protected String statusDescription;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLabel() {
		return label;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLabel(String label) {
		this.label = label;
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
	public int getOrderNumber() {
		return orderNumber;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setOrderNumber(int orderNumber) {
		this.orderNumber = orderNumber;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getStatusDescription() {
		return statusDescription;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setStatusDescription(String statusDescription) {
		this.statusDescription = statusDescription;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toJson() {
		return serializer.toJson(this);
	}
}
