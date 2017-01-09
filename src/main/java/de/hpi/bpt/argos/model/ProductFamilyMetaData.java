package de.hpi.bpt.argos.model;

public interface ProductFamilyMetaData {
	String getLabel();

	void setLabel(String label);

	String getBrand();

	void setBrand(String brand);

	int getOrderNumber();

	void setOrderNumber(int orderNumber);

	String getStatusDescription();

	void setStatusDescription(String statusDescription);
}
