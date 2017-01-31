package de.hpi.bpt.argos.persistence.database;

import de.hpi.bpt.argos.persistence.model.event.data.Event;
import de.hpi.bpt.argos.persistence.model.event.type.EventType;
import de.hpi.bpt.argos.persistence.model.product.Product;
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
	Map<EventType, Integer> listAllEventTypesForProduct(int productId);

	/**
	 * This method makes the database call to retrieve the necessary data for the API that serves events for a
	 * certain product of a defined event type within a defined index range
	 * @param productId - the product to be searched identified by its id
	 * @param eventTypeId - the event type to be searched identified by its id
	 * @param indexFrom - the start of the index range that the events should come from
	 * @param indexTo - the end of the index range that the events should come from
	 * @return - a list of events that satisfies the parameters
	 */
	List<Event> listEventsForProductOfTypeInRange(int productId, int eventTypeId, int indexFrom, int
			indexTo);

	/**
	 * This method makes the database call to retrieve the requested event type.
	 * @param eventTypeId - the event type id
	 * @return - the requested event type
	 */
	EventType getEventType(int eventTypeId);

	/**
	 * This method makes the database call to retrieve the requested product.
	 * @param productOrderNumber - the product order number to search
	 * @return - the product
	 */
	Product getProduct(int productOrderNumber);

	/**
	 * This method makes the database call to retrieve the requested product family.
	 * @param productFamilyName - the productFamilyName of the product family
	 * @return - the product family
	 */
	ProductFamily getProductFamily(String productFamilyName);

	/**
	 * This method makes the database call to save the given product families.
	 * @param productFamilies - the list of product families
	 */
	void saveProductFamilies(List<ProductFamily> productFamilies);

	/**
	 * This method makes the database call to retrieve all event types.
	 * @return - a list of all event types
	 */
	List<EventType> listEvenTypes();

	/**
	 * This method makes the database call to save all given event types.
	 * @param eventTypes - a list of event types to save
	 */
	void saveEventTypes(List<EventType> eventTypes);

	/**
	 * This method makes the database call to save all given events.
	 * @param events - a list of events to save
	 */
	void saveEvents(List<Event> events);

	/**
	 * This method makes the database call to save all given products.
	 * @param products - a list of products to save
	 */
	void saveProducts(List<Product> products);
}
