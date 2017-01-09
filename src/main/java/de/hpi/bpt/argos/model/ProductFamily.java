package de.hpi.bpt.argos.model;

import de.hpi.bpt.argos.serialization.Serializable;

import java.util.Date;

public interface ProductFamily extends Serializable {
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

	void setExampleData();
}
