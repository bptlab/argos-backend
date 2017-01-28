package de.hpi.bpt.argos.persistence.model.product;

import de.hpi.bpt.argos.persistence.database.DatabaseConnection;

/**
 * This interface represents factories which create products or retrieve them from the database if already existing.
 */
public interface ProductFactory {

	/**
	 * This method sets the database connection for this product factory.
	 * @param databaseConnection - the database connection to be set
	 */
	void setDatabaseConnection(DatabaseConnection databaseConnection);

	/**
	 * This method returns a product and creates it if it does not exist in the database.
	 * @param productFamilyIdentifier - the identifier for the product family
	 * @param productIdentifier - the identifier for the product
	 * @return - a product
	 */
	Product getProduct(String productFamilyIdentifier, int productIdentifier);
}
