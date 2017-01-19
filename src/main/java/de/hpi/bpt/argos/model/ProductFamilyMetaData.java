package de.hpi.bpt.argos.model;

import de.hpi.bpt.argos.serialization.Serializable;

/**
 * This interface represents the metadata for a product family. It is serializable.
 */
public interface ProductFamilyMetaData extends Serializable {
	/**
	 * This method returns the label of the related product family.
	 * @return - the label of the related product family as a string
	 */
	String getLabel();

	/**
	 * This method sets the label of the related product family.
	 * @param label - the label of the related product family to be set
	 */
	void setLabel(String label);

	/**
	 * This method returns the brand of the related product family.
	 * @return - the brand of the related product family as a string
	 */
	String getBrand();

	/**
	 * This method sets the brand of the related product family.
	 * @param brand - the label of the related product family to be set
	 */
	void setBrand(String brand);

	/**
	 * This method returns the order number of the related product family.
	 * @return - the order number of the related product family
	 */
	int getOrderNumber();

	/**
	 * This method sets the order number of the related product family.
	 * @param orderNumber - the order number of the related product family to be set
	 */
	void setOrderNumber(int orderNumber);

	/**
	 * This method returns the status description of the related product family.
	 * @return - the status description of the related product family as a string
	 */
	String getStatusDescription();

	/**
	 * This method sets the status description of the related product family.
	 * @return - the status description of the related product family to be set
	 */
	void setStatusDescription(String statusDescription);
}
