package de.hpi.bpt.argos.api.response;

import de.hpi.bpt.argos.persistence.database.DatabaseConnection;

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

	/**
	 * This method returns a json representation of all events for one specific product with a specific event type within a certain range.
	 * @param productId - the product identifier
	 * @param eventTypeId - the event type identifier
	 * @param eventIndexFrom - the start index for the events
	 * @param eventIndexTo - the end index of the events
	 * @return - a json representation of the requested events
	 */
	String getEventsForProduct(int productId, int eventTypeId, int eventIndexFrom, int eventIndexTo);

	/**
	 * This method returns a json representation of an event defined by the event id.
	 * @param eventId - the event identifier
	 * @return - a json representation of the requested event
	 */
	String getSingleEvent(int eventId);
}
