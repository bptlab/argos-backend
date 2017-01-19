package de.hpi.bpt.argos.model.product;

import de.hpi.bpt.argos.serialization.Serializable;

/**
 * This interface represents meta data for product families. It is serializable.
 */
public interface ProductFamilyMetaData extends Serializable {

	/**
	 * This method returns the name of the related product family.
	 * @return - the name of the related product family as string
	 */
	String getName();

	/**
	 * This method sets the name of the related product family.
	 * @param name - the name to be set
	 */
	void setName(String name);

	/**
	 * This method returns the brand of the related product family.
	 * @return - the name of the related product family
	 */
	String getBrand();

	/**
	 * This method sets the brand of the related product family.
	 * @param brand - the brand of the related product family
	 */
	void setBrand(String brand);
}
