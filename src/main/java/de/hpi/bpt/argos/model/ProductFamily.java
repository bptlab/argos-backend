package de.hpi.bpt.argos.model;

import de.hpi.bpt.argos.serialization.Serializable;

import java.util.Date;

/**
 * This interface represents the product families. It is serializable.
 */
public interface ProductFamily extends Serializable {
	/**
	 * This method returns the number of devices of this particular product family.
	 * @return - the number of devices of this product family as an integer
	 */
	int getNumberOfDevices();

	/**
	 * This method sets the number of devices of this particular product family.
	 * @param numberOfDevices - the number of devices to be set
	 */
	void setNumberOfDevices(int numberOfDevices);

	/**
	 * This method returns the number of events that occurred and included this product family.
	 * @return - the number of events that occurred and included this product family as an integer
	 */
	int getNumberOfEvents();

	/**
	 * This method sets the number of events that occurred and included this product family.
	 * @param numberOfEvents - the number of events that occurred and included this product family to be set
	 */
	void setNumberOfEvents(int numberOfEvents);

	/**
	 * This method returns the production start date of this product family.
	 * @return - the production start date of this product family as a date object
	 */
	Date getProductionStart();

	/**
	 * This method sets the production start date of this product family.
	 * @param productionStart - the production start date of this product family to be set as a date object
	 */
	void setProductionStart(Date productionStart);

	/**
	 * This method returns the state of this product family.
	 * @return - the product family state as a ProductFamilyState enum
	 */
	ProductFamilyState getState();

	/**
	 * This method sets the state of this product family.
	 * @return - the product family state as a ProductFamilyState enum
	 */
	void setState(ProductFamilyState productFamilyState);

	/**
	 * This method returns the name of the product family.
	 * @return - the name of the product family as a string to be set
	 */
	String getName();

	/**
	 * This method sets the name of the product family.
	 * @param name - the name of the product family to be set
	 */
	void setName(String name);

	/**
	 * This method returns the id of this product family.
	 * @return - the id of this product family as an integer
	 */
	int getId();

	/**
	 * This method returns the id of this product family.
	 * @param id - the id of this product family to be set
	 */
	void setId(int id);

	/**
	 * This method returns the metadata of this product family
	 * @return - the metadata of this product family as a ProductFamilyMetadata object
	 */
	ProductFamilyMetaData getMetaData();

	/**
	 * This method sets the metadata of this product family.
	 * @param productFamilyMetaData - the product family metadata to be set as a ProductFamilyMetadata object
	 */
	void setMetaData(ProductFamilyMetaData productFamilyMetaData);

	/**
	 * This method fills this product family with some default values.
	 */
	void setExampleData();
}
