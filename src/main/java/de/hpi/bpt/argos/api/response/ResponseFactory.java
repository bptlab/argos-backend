package de.hpi.bpt.argos.api.response;

import de.hpi.bpt.argos.persistence.database.DatabaseConnection;
import de.hpi.bpt.argos.persistence.model.product.ProductFamily;

/**
 * This interface represents factories which produce rest responses.
 */
public interface ResponseFactory {

	/**
	 * This method sets the database connection for this response factory.
	 * @param databaseConnection - the database connection to be set
	 */
	void setDatabaseConnection(DatabaseConnection databaseConnection);

	/**
	 * This method returns a json representation of all product families.
	 * @return - a json representation of all product families
	 */
	String getAllProductFamilies();

	/**
	 * This method returns a json representation of all event types for one specific product id.
	 * @param productId - the specific product identifier
	 * @return - a json representation of all event types
	 */
	String getAllEventTypes(int productId);
}
