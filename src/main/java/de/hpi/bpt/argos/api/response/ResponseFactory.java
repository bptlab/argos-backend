package de.hpi.bpt.argos.api.response;

import de.hpi.bpt.argos.persistence.database.PersistenceEntityManager;

/**
 * This interface represents factories which produce rest responses.
 */
public interface ResponseFactory {

	/**
	 * This method sets up this response factory.
	 * @param entityManager - the entity manager to get persistence entities from
	 */
	void setup(PersistenceEntityManager entityManager);

	/**
	 * This method returns a json representation of all product families.
	 * @return - a json representation of all product families
	 */
	String getAllProductFamilies();

	/**
	 * This method returns a json representation of one specific product family.
	 * @param productFamilyId - the product family id
	 * @return - a json representation of the specific product family
	 */
	String getProductFamily(long productFamilyId);

	/**
	 * This method returns a json representation of one specific product.
	 * @param productId - the product id
	 * @return - a json representation of the specific product
	 */
	String getProduct(long productId);

	/**
	 * This method returns a json representation of all event types for one specific product id.
	 * @param productId - the specific product identifier
	 * @return - a json representation of all event types
	 */
	String getAllEventTypes(long productId);

	/**
	 * This method returns a json representation of the specified event type.
	 * @param eventTypeId - the id of the event type
	 * @return - a json representation of the event type
	 */
	String getEventType(long eventTypeId);

	/**
	 * This method returns a json representation of all event types.
	 * @return - a json representation of all event types
	 */
	String getAllEventTypes();

	/**
	 * This method returns a json representation of all events for one specific product with a specific event type within a certain range.
	 * @param productId - the product identifier
	 * @param eventTypeId - the event type identifier
	 * @param eventIndexFrom - the start index for the events
	 * @param eventIndexTo - the end index of the events
	 * @return - a json representation of the requested events
	 */
	String getEventsForProduct(long productId, long eventTypeId, int eventIndexFrom, int eventIndexTo);

	/**
	 * This method returns a json representation of an event defined by the event id.
	 * @param eventId - the event identifier
	 * @return - a json representation of the requested event
	 */
	String getEvent(long eventId);

	/**
	 * This method returns a default response to generic requests.
	 * @return - a simple response to a request
	 */
	default String finishRequest() {
		return "request finished";
	}

	/**
	 * This method returns the http page not found code.
	 * @return - the http page not found code
	 */
	static int getHttpNotFoundCode() {
		return 404;
	}
}
