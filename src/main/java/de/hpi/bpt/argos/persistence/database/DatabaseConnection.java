package de.hpi.bpt.argos.persistence.database;

import de.hpi.bpt.argos.persistence.model.event.EventType;
import de.hpi.bpt.argos.persistence.model.product.ProductFamily;

import java.util.List;

/**
 * This interface represents data connections which are used to communicate with the data base.
 */
public interface DatabaseConnection {

	/**
	 * This method sets up the data connection.
	 * @return - true if the connection was established
	 */
	boolean setup();

	List<ProductFamily> listAllProductFamilies();

	List<EventType> listAllEventTypesForProductFamily(String productFamilyId);
}
