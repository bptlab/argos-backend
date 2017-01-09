package de.hpi.bpt.argos.model;

import java.util.Date;

public interface ProductFamily {
	int getNumberOfDevices();

	void setNumberOfDevices(int numberOfDevices);

	int getNumberOfEvents();

	void setNumberOfEvents(int numberOfEvents);

	Date getProductionStart();

	void setProductionStart(Date productionStart);

	ProductFamilyState getState();

	void setState(ProductFamilyState productFamilyState);

	String getName();

	void setName(String name);

	int getId();

	void setId(int id);

	ProductFamilyMetaData getMetaData();

	void setMetaData(ProductFamilyMetaData productFamilyMetaData);
}
