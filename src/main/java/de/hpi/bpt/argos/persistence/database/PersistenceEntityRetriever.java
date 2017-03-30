package de.hpi.bpt.argos.persistence.database;

import de.hpi.bpt.argos.persistence.model.event.Event;
import de.hpi.bpt.argos.persistence.model.event.type.EventType;
import de.hpi.bpt.argos.persistence.model.parsing.DataFile;
import de.hpi.bpt.argos.persistence.model.product.Product;
import de.hpi.bpt.argos.persistence.model.product.ProductConfiguration;
import de.hpi.bpt.argos.persistence.model.product.ProductFamily;

import java.util.List;
import java.util.Map;

/**
 * This interface represents object which are able to retrieve entities from the database.
 */
public interface PersistenceEntityRetriever {

	/**
	 * This method sets up the data connection.
	 * @return - true if the connection was established
	 */
	boolean setup();

	/**
	 * This method makes the database call to retrieve the necessary data for the API that serves all product families.
	 * @return - a list of ProductFamily objects registered in the database
	 */
	List<ProductFamily> getProductFamilies();

	/**
	 * This method makes the database call to retrieve one specific product family.
	 * @param productFamilyId - the id of the requested product family
	 * @return - the specified product family
	 */
	ProductFamily getProductFamily(long productFamilyId);

	/**
	 * This method makes the database call to retrieve one specific product.
	 * @param productId - the id of the requested product
	 * @return - the specified product
	 */
	Product getProduct(long productId);

	/**
	 * This method makes the database call to retrieve one specific product configuration.
	 * @param productConfigurationId - the id of the requested product configuration
	 * @return - the specified product configuration
	 */
	ProductConfiguration getProductConfiguration(long productConfigurationId);

	/**
	 * This method makes the database call to retrieve the necessary data for the API that serves all event types for a certain product.
	 * @param productId - the product that we want the event types for
	 * @return - a map of event types that can occur for the product with the number of events that occurred for this
	 * event type
	 */
	Map<EventType, Integer> getEventTypesForProduct(long productId);

	/**
	 * This method makes the database call to retrieve the necessary data for the API that serves all event types for a certain product configuration.
	 * @param productConfigurationId - the product configuration that we want the event types for
	 * @return - a map of event types that can occur for the product configuration with the number of events that occurred for this
	 * event type
	 */
	Map<EventType, Integer> getEventTypesForProductConfiguration(long productConfigurationId);

	/**
	 * This method makes the database call to retrieve the necessary data for the API that serves events for a
	 * certain product of a defined event type within a defined index range.
	 * @param productId - the product to be searched identified by its id
	 * @param eventTypeId - the event type to be searched identified by its id
	 * @param indexFrom - the start of the index range that the events should come from
	 * @param indexTo - the end of the index range that the events should come from
	 * @return - a list of events that satisfies the parameters
	 */
	List<Event> getEventsForProduct(long productId, long eventTypeId, int indexFrom, int indexTo);

	/**
	 * This method makes the database call to retrieve the necessary data for the API that serves events for a
	 * certain product configuration of a defined event type within a defined index range.
	 * @param productConfigurationId - the product configuration to be searched identified by its id
	 * @param eventTypeId - the event type to be searched identified by its id
	 * @param indexFrom - the start of the index range that the events should come from
	 * @param indexTo - the end of the index range that the events should come from
	 * @return - a list of events that satisfies the parameters
	 */
	List<Event> getEventsForProductConfiguration(long productConfigurationId, long eventTypeId, int indexFrom, int indexTo);

	/**
	 * This method makes the database call to retrieve the requested event type.
	 * @param eventTypeId - the event type id
	 * @return - the requested event type
	 */
	EventType getEventType(long eventTypeId);

	/**
	 * This method makes the database call to retrieve the requested event type.
	 * @param eventTypeName - the name of the event type
	 * @return - the requested event type or null
	 */
	EventType getEventType(String eventTypeName);

	/**
	 * This method makes the database call to retrieve the requested product.
	 * @param externalProductId - the external product id
	 * @return - the product
	 */
	Product getProductByExternalId(long externalProductId);

	/**
	 * This method makes the database call to retrieve the requested product family.
	 * @param productFamilyName - the productFamilyName of the product family
	 * @return - the product family
	 */
	ProductFamily getProductFamily(String productFamilyName);

	/**
	 * This method makes the database call to retrieve all event types.
	 * @return - a list of all event types
	 */
	List<EventType> getEventTypes();

	/**
	 * This method makes the database call to retrieve a single event.
	 * @param eventId - the id of the event to retrieve
	 * @return - the requested event
	 */
	Event getEvent(long eventId);

	/**
	 * This method makes the database call to retrieve a single data file.
	 * @param path - the of the file to look for
	 * @return - the requested data file
	 */
	DataFile getDataFile(String path);
}
