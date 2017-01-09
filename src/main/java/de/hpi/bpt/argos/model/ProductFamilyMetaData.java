package de.hpi.bpt.argos.model;

import de.hpi.bpt.argos.serialization.Serializable;

public interface ProductFamilyMetaData extends Serializable{
	String getLabel();

	void setLabel(String label);

	String getBrand();

	void setBrand(String brand);

	int getOrderNumber();

	void setOrderNumber(int orderNumber);

	String getStatusDescription();

	void setStatusDescription(String statusDescription);
}
