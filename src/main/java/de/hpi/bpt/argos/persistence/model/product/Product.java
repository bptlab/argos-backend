package de.hpi.bpt.argos.persistence.model.product;

import de.hpi.bpt.argos.persistence.database.PersistenceEntity;

import java.util.Date;
import java.util.Set;

/**
 * This interface represents the products. It extends persistence entity.
 */
public interface Product extends PersistenceEntity {

	/**
	 * This method returns the product family this product is in.
	 * @return - the product family this product is in
	 */
	ProductFamily getProductFamily();

	/**
	 * This method sets the product family this product is in.
	 * @param productFamily - the product family to be set
	 */
	void setProductFamily(ProductFamily productFamily);

	/**
	 * This method returns the production start date of this product.
	 * @return - the production start date of this product as a date object
	 */
	Date getProductionStart();

	/**
	 * This method sets the production start date of this product.
	 * @param productionStart - the production start date of this product to be set
	 */
	void setProductionStart(Date productionStart);

	/**
	 * This method returns the name of this product.
	 * @return - the name of this product to be set
	 */
	String getName();

	/**
	 * This method sets the name of this product.
	 * @param name - the name of this product to be set
	 */
	void setName(String name);

	/**
	 * This method returns the order number of this product.
	 * @return - the order number of this product as an integer
	 */
	long getOrderNumber();

	/**
	 * This method sets the order number of this product.
	 * @param orderNumber - the order number of this product to be set
	 */
	void setOrderNumber(long orderNumber);

	/**
	 * This method returns the number of devices that are installed from this product families.
	 * @return - the number of devices installed as an integer
	 */
	long getNumberOfDevices();

	/**
	 * This method increments the number of devices for this product.
	 * @param count - the count of how much new devices were found
	 */
	void incrementNumberOfDevices(long count);

	/**
	 * This method returns the number of events that occurred for this product.
	 * @return - the number of events occurred as an integer
	 */
	long getNumberOfEvents();

	/**
	 * This method returns the state of this product.
	 * @return - the product state as a ProductState enum
	 */
	ProductState getState();

	/**
	 * This method returns the status description of this product.
	 * @return - the status description of this product as a string
	 */
	String getStateDescription();

	/**
	 * This method returns all configurations of this product.
	 * @return - a set of all configurations for this product
	 */
	Set<ProductConfiguration> getProductConfigurations();

	/**
	 * This method sets all configurations of this product.
	 * @param productConfigurations - a set of product configurations to be set
	 */
	void setProductConfigurations(Set<ProductConfiguration> productConfigurations);

	/**
	 * This method adds a new product configuration to the configurations of this product.
	 * @param productConfiguration - the configuration to be added
	 */
	void addProductConfiguration(ProductConfiguration productConfiguration);

	/**
	 * This method returns the configuration with supports a given coding plug and a given coding plug software version.
	 * @param codingPlugId - the coding plug id of the configuration
	 * @param codingPlugSoftwareVersion - the supported coding plug software version
	 * @return - the configuration, or null if no configuration matches the parameters
	 */
	ProductConfiguration getProductConfiguration(int codingPlugId, float codingPlugSoftwareVersion);
}
