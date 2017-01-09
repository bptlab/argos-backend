package de.hpi.bpt.argos.model;

public class ProductFamilyMetaDataImpl implements ProductFamilyMetaData {
	protected String label;
	protected String brand;
	protected int orderNumber;
	protected String statusDescription;

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String getBrand() {
		return brand;
	}

	@Override
	public void setBrand(String brand) {
		this.brand = brand;
	}

	@Override
	public int getOrderNumber() {
		return orderNumber;
	}

	@Override
	public void setOrderNumber(int orderNumber) {
		this.orderNumber = orderNumber;
	}

	@Override
	public String getStatusDescription() {
		return statusDescription;
	}

	@Override
	public void setStatusDescription(String statusDescription) {
		this.statusDescription = statusDescription;
	}
}
