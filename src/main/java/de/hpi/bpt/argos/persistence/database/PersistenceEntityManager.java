package de.hpi.bpt.argos.persistence.database;

import de.hpi.bpt.argos.persistence.model.event.Event;
import de.hpi.bpt.argos.persistence.model.event.type.EventType;
import de.hpi.bpt.argos.persistence.model.product.Product;
import de.hpi.bpt.argos.persistence.model.product.ProductFamily;

import java.util.List;

/**
 * This interface represents a factory which is able to create new persistence entities.
 */
public interface PersistenceEntityManager extends PersistenceEntityRetriever {

	/**
	 * This method adds a new event receiver for entity update events.
	 * @param eventReceiver - the event receiver
	 */
	void subscribe(PersistenceEntityManagerEventReceiver eventReceiver);

	/**
	 * This method removes an event receiver.
	 * @param eventReceiver - the event receiver
	 */
	void unsubscribe(PersistenceEntityManagerEventReceiver eventReceiver);

	/**
	 * This method stores a modified entity in the database. Only use this method if client should not be notified about changes.
	 * @param entity - the modified entity
	 */
	void updateEntity(PersistenceEntity entity);

	/**
	 * This method stores a modified entity in the database. Use this method to also notify clients about the changes.
	 * @param entity - the modified entity
	 * @param fetchUri - the uri where to fetch this updated entity
	 */
	void updateEntity(PersistenceEntity entity, String fetchUri);

	/**
	 * This method returns a newly created event from its json representation.
	 * @param eventType - the type of the event
	 * @param jsonEvent - the json representation of the event
	 * @return - the event
	 */
	Event createEvent(EventType eventType, String jsonEvent);

	/**
	 * This method returns a product or creates it, if it does not exist in the database.
	 * @param productFamily - the family of the product
	 * @param productOrderNumber - the unique product order number
	 * @return - the product
	 */
	Product getProduct(ProductFamily productFamily, int productOrderNumber);

	/**
	 * This method returns a product and creates it, if it does not exist in the database.
	 * @param productFamilyName - the identifier for the product family
	 * @param productIdentifier - the identifier for the product
	 * @return - a product
	 */
	Product getProduct(String productFamilyName, int productIdentifier);

	/**
	 * This method returns a product family and create it, if it doe not exist in the database.
	 * @param productFamilyName - the unique product family name
	 * @return - the product family
	 */
	@Override
	ProductFamily getProductFamily(String productFamilyName);
}
