package de.hpi.bpt.argos.model.product;

import de.hpi.bpt.argos.serialization.Serializable;

import java.util.Set;

/**
 * This interface represents the product families. It is serializable.
 */
public interface ProductFamily extends Serializable {
	/**
	 * This method returns the unique identifier for this product family.
	 * @return - the unique identifier for this product family as integer
	 */
	int getId();

	/**
	 * This methods sets the unique identifier for this product family.
	 * @param id - the unique id to be set
	 */
	void setId(int id);

	/**
	 * This method returns the meta data for this product family.
	 * @return - the meta data for this product family as ProductFamilyMetaData
	 */
	ProductFamilyMetaData getMetaData();

	/**
	 * This method sets the meta data for this product family.
	 * @param metaData - the meta data
	 */
	void setMetaData(ProductFamilyMetaData metaData);

	/**
	 * This method returns a set of products within this product family.
	 * @return - a set of products within this product family
	 */
	Set<Product> getProducts();

	/**
	 * This method sets a set of products which belong to this product family.
	 * @param products - a set of products to be set
	 */
	void setProducts(Set<Product> products);
}
