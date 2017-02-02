package de.hpi.bpt.argos.persistence.model.product;

import de.hpi.bpt.argos.persistence.database.PersistenceEntity;

import java.util.List;

/**
 * This interface represents the product families. It extends persistence entity.
 */
public interface ProductFamily extends PersistenceEntity {

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
	List<Product> getProducts();

	/**
	 * This method sets the set of products in this product family.
	 * @param products - a set of products to be set
	 */
	void setProducts(List<Product> products);

	/**
	 * This method adds a product to the product family.
	 * @param product - the product to be added
	 */
	void addProduct(Product product);
}
