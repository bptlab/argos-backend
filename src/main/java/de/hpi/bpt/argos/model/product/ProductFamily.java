package de.hpi.bpt.argos.model.product;

import java.util.Set;

/**
 * This interface represents the product families.
 */
public interface ProductFamily {
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
	 * This method returns the name of this product family.
	 * @return - the name of this product family as string
	 */
	String getName();

	/**
	 * This method sets the name of this product family.
	 * @param name - the name to be set
	 */
	void setName(String name);

	/**
	 * This method returns the brand of this product family.
	 * @return - the name of this product family
	 */
	String getBrand();

	/**
	 * This method sets the brand of this product family.
	 * @param brand - the brand of this product family
	 */
	void setBrand(String brand);

	/**
	 * This method return a set of products in this product family.
	 * @return - a set of products
	 */
	Set<Product> getProducts();

	/**
	 * This method sets the set of products in this product family.
	 * @param products - a set of products to be set
	 */
	void setProducts(Set<Product> products);
}
