package de.hpi.bpt.argos.storage;

import de.hpi.bpt.argos.storage.dataModel.attribute.Attribute;
import de.hpi.bpt.argos.storage.dataModel.attribute.type.TypeAttribute;
import de.hpi.bpt.argos.storage.dataModel.entity.Entity;
import de.hpi.bpt.argos.storage.dataModel.entity.type.EntityType;
import de.hpi.bpt.argos.storage.dataModel.event.Event;
import de.hpi.bpt.argos.storage.dataModel.event.query.EventQuery;
import de.hpi.bpt.argos.storage.dataModel.event.type.EventType;
import de.hpi.bpt.argos.storage.dataModel.mapping.EventEntityMapping;

import java.util.List;

/**
 * This interface offers methods to retrieve and store artifacts
 */
public interface PersistenceAdapter {

	/**
	 * This method tries to establish a connection to the database.
	 * @return - true, if the connection was established
	 */
	boolean establishConnection();

	/**
	 * This method returns a list of all attributes, which belong to a specific owner. (e.g. Entity, Event)
	 * @param ownerId - the unique identifier of the attribute owner
	 * @return - a list of all attributes, which belong to a specific owner
	 */
	List<Attribute> getAttributes(long ownerId);

	/**
	 * This method returns a list of all typeAttributes, which belong to a specific type. (e.g. EntityType, EventType)
	 * @param typeId - the unique identifier of the typeAttribute owner
	 * @return - a list of all typeAttributes, which belong to a specific type
	 */
	List<TypeAttribute> getTypeAttributes(long typeId);

	/**
	 * This method returns a specific entity, identified by it's id.
	 * @param id - the unique identifier for the entity
	 * @return - the entity or null
	 */
	Entity getEntity(long id);

	/**
	 * This method returns a list of all entities, which are children of a specific other entity and are from a specific entityType.
	 * @param parentId - the unique identifier of the parent entity
	 * @param entityTypeId - the unique identifier of the entityType
	 * @return - a list of all entities, which are children of a specific other entity and are from a specific entityType
	 */
	List<Entity> getEntities(long parentId, long entityTypeId);

	/**
	 * This method returns a list of all entityTypes.
	 * @return - a list of all entityTypes
	 */
	List<EntityType> getEntityTypes();

	/**
	 * This method returns a list of events, which belong to a specific entity and are from a specific eventType.
	 * @param entityOwnerId - the unique identifier of the entity the events belong to
	 * @param eventTypeId - the unique identifier of the eventType
	 * @param listStartIndex - the startIndex for the events
	 * @param listEndIndex - the endIndex for the events
	 * @return - a list of events, which belong to a specific entity and are from a specific eventType
	 */
	List<Event> getEvents(long entityOwnerId, long eventTypeId, int listStartIndex, int listEndIndex);

	/**
	 * This method returns a specific eventType, identified by it's id.
	 * @param id - the unique identifier of the eventType
	 * @return - the eventType or null
	 */
	EventType getEventType(long id);

	/**
	 * This method returns a list of all eventTypes.
	 * @return - a list of all eventTypes
	 */
	List<EventType> getEventTypes();

	/**
	 * This method returns a list of all eventQueries, which belong to a specific eventType.
	 * @param eventTypeId - the unique identifier of the eventType
	 * @return - a list of all eventQueries, which belong to a specific eventType
	 */
	List<EventQuery> getEventQueries(long eventTypeId);

	/**
	 * This method returns a list of all eventEntityMappings, which belong to a specific entityType.
	 * @param entityTypeId - the unique identifier of the entityType
	 * @return - a list of all eventEntityMappings, which belong to a specific entityType
	 */
	List<EventEntityMapping> getEventEntityMappingsForEntityType(long entityTypeId);

	/**
	 * This method returns a list of all eventEntityMappings, which belong to a specific eventType.
	 * @param eventTypeId - the unique identifier of the eventType
	 * @return - a list of all eventEntityMappings, which belong to a specific eventType
	 */
	List<EventEntityMapping> getEventEntityMappingsForEventType(long eventTypeId);
}
