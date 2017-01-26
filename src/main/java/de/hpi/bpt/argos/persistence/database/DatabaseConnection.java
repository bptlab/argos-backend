package de.hpi.bpt.argos.persistence.database;

import de.hpi.bpt.argos.persistence.model.event.Event;
import de.hpi.bpt.argos.persistence.model.event.EventType;
import de.hpi.bpt.argos.persistence.model.product.ProductFamily;

import java.util.List;
import java.util.Map;

/**
 * This interface represents data connections which are used to communicate with the data base.
 */
public interface DatabaseConnection {

	/**
	 * This method sets up the data connection.
	 * @return - true if the connection was established
	 */
	boolean setup();

	/**
	 * This method makes the database call to retrieve the necessary data for the API that serves all product families.
	 * @return - a list of ProductFamily objects registered in the database
	 */
	List<ProductFamily> listAllProductFamilies();

	/**
	 * This method makes the database call to retrieve the necessary data for the API that serves all event types for
	 * a certain product
	 * @param productId - the product that we want the event types for
	 * @return - a map of event types that can occur for the product with the number of events that occurred for this
	 * event type
	 */
	Map<EventType, Integer> listAllEventTypesForProduct(Integer productId);

	/**
	 * This method makes the database call to retrieve the necessary data for the API that serves events for a
	 * certain product of a defined event type within a defined index range
	 * @param productId - the product to be searched identified by its id
	 * @param eventTypeId - the event type to be searched identified by its id
	 * @param indexFrom - the start of the index range that the events should come from
	 * @param indexTo - the end of the index range that the events should come from
	 * @return - a list of events that satisfies the parameters
	 */
	List<Event> listEventsForProductOfTypeInRange(Integer productId, Integer eventTypeId, Integer indexFrom, Integer
			indexTo);
}
