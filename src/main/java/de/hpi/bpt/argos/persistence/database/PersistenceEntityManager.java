package de.hpi.bpt.argos.persistence.database;

import com.google.gson.JsonObject;
import de.hpi.bpt.argos.persistence.model.event.Event;
import de.hpi.bpt.argos.persistence.model.event.type.EventType;
import de.hpi.bpt.argos.persistence.model.product.Product;
import de.hpi.bpt.argos.persistence.model.product.ProductFamily;
import de.hpi.bpt.argos.persistence.model.product.ProductState;

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
	 * @return - true, if the entity was updated
	 */
	boolean updateEntity(PersistenceEntity entity);

	/**
	 * This method stores a modified entity in the database. Use this method to also notify clients about the changes.
	 * @param entity - the modified entity
	 * @param fetchUri - the uri where to fetch this updated entity
	 * @return - true, if the entity was updated
	 */
	boolean updateEntity(PersistenceEntity entity, String fetchUri);

	/**
	 * This method deletes an entity. This method also notifies clients about the changes.
	 * @param entity - the entity to delete
	 * @return - true, if the entity was deleted
	 */
	boolean deleteEntity(PersistenceEntity entity);

	/**
	 * This method returns a newly created event from its json representation.
	 * @param eventTypeId - the event type id
	 * @param requestBody - the json representation of the event
	 * @return - the new event
	 */
	Event createEvent(long eventTypeId, String requestBody);

	/**
	 * This method returns a newly created status update event from its json representation.
	 * @param productId - the product id
	 * @param newProductState - the updated product state
	 * @param requestBody - the json representation of the event
	 * @return - the new event
	 */
	Event createStatusUpdateEvent(long productId, ProductState newProductState, String requestBody);

	/**
	 * This method returns a newly created, simple event type from its json representation.
	 * @param jsonEventType - the json representation of the event type
	 * @return - the simple event type
	 */
	EventType createSimpleEventType(JsonObject jsonEventType);

	/**
	 * This method returns a newly created event type from its json representation. Notice: This event type has no valid EventQuery and
	 * thus, clients will not be updated.
	 * @param jsonEventType - the json representation of the event type
	 * @return - the event type
	 */
	EventType createEventType(JsonObject jsonEventType);

	/**
	 * This method updates an existing event type.
	 * @param jsonEventType - the new json representation of the event type
	 * @param eventTypeId - the event type id
	 * @return - the updated event type
	 */
	EventType updateEventType(JsonObject jsonEventType, long eventTypeId);

	/**
	 * This method returns a product or creates it, if it does not exist in the database.
	 * @param productFamily - the family of the product
	 * @param externalProductId - the unique product order number
	 * @return - the product
	 */
	Product getProduct(ProductFamily productFamily, long externalProductId);

	/**
	 * This method returns a product and creates it, if it does not exist in the database.
	 * @param productFamilyName - the identifier for the product family
	 * @param externalProductId - the identifier for the product
	 * @return - the product product
	 */
	Product getProduct(String productFamilyName, long externalProductId);

	/**
	 * This method returns a product family and create it, if it doe not exist in the database.
	 * @param productFamilyName - the unique product family name
	 * @return - the product family
	 */
	@Override
	ProductFamily getProductFamily(String productFamilyName);
}
