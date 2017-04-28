package de.hpi.bpt.argos.storage;

import de.hpi.bpt.argos.common.Observable;
import de.hpi.bpt.argos.storage.dataModel.PersistenceArtifact;
import de.hpi.bpt.argos.storage.dataModel.attribute.Attribute;
import de.hpi.bpt.argos.storage.dataModel.attribute.type.TypeAttribute;
import de.hpi.bpt.argos.storage.dataModel.entity.Entity;
import de.hpi.bpt.argos.storage.dataModel.entity.type.EntityType;
import de.hpi.bpt.argos.storage.dataModel.event.Event;
import de.hpi.bpt.argos.storage.dataModel.event.query.EventQuery;
import de.hpi.bpt.argos.storage.dataModel.event.type.EventType;
import de.hpi.bpt.argos.storage.dataModel.mapping.EventEntityMapping;
import de.hpi.bpt.argos.storage.dataModel.mapping.MappingCondition;

import java.util.List;

/**
 * This interface offers methods to retrieve and store artifacts.
 */
public interface PersistenceAdapter extends Observable<PersistenceArtifactUpdateObserver> {

	/**
	 * This method tries to establish a connection to the database.
	 * @return - true, if the connection was established
	 */
	boolean establishConnection();

	/**
	 * This method saves/updates a list of persistenceArtifacts. Connected web socket clients will *not* be notified.
	 * @param artifacts - the artifacts to save/update
	 * @return - true, if all artifacts were saved/updated
	 */
	boolean saveArtifacts(PersistenceArtifact... artifacts);

	/**
	 * This method deletes a list of persistenceArtifacts. Connected web socket clients will *not* be notified.
	 * @param artifacts - the artifacts to delete
	 * @return - true, if all artifacts were deleted
	 */
	boolean deleteArtifacts(PersistenceArtifact... artifacts);

	/**
	 * This method saves a new artifact in the database and notifies connected web socket clients.
	 * @param artifact - the artifact to save
	 * @param fetchUri - the uri, where connected web socket clients can fetch the new artifact from
	 * @return - true, if the artifact was stored in the database
	 */
	boolean createArtifact(PersistenceArtifact artifact, String fetchUri);

	/**
	 * This method updates an existing artifact in the database and notifies connected web socket clients.
	 * @param artifact - the artifact to update
	 * @param fetchUri - the uri, where connected web socket clients can fetch the updated artifact from
	 * @return - true, if the artifact was updated in the database
	 */
	boolean updateArtifact(PersistenceArtifact artifact, String fetchUri);

	/**
	 * This method deletes an existing artifact in the database and notifies connected web socket clients.
	 * @param artifact - the artifact to delete
	 * @param fetchUri - the fetch uri, where the artifact was fetched from
	 * @return - true, if the artifact was deleted
	 */
	boolean deleteArtifact(PersistenceArtifact artifact, String fetchUri);

	/**
	 * This method returns a list of all attributes, which belong to a specific owner. (e.g. Entity, Event)
	 * @param ownerId - the unique identifier of the attribute owner
	 * @return - a list of all attributes, which belong to a specific owner
	 */
	List<Attribute> getAttributes(long ownerId);

	/**
	 * This method returns the typeAttribute, identified by it's id.
	 * @param id - the unique identifier of the typeAttribute
	 * @return - the typeAttribute or null
	 */
	TypeAttribute getTypeAttribute(long id);

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
	 * This method returns an entity, whose id is returned for a specific sqlQuery.
	 * @param sqlQuery - the sqlQuery to execute to get the entity id
	 * @return - the entity, whose id is returned by the given query or null, of there was no perfect match
	 */
	Entity getMappingEntity(String sqlQuery);

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
	 * This method returns the number of events for a specific entity and a specific eventType.
	 * @param entityId - the unique identifier of the entity
	 * @param eventTypeId - the unique identifier of the eventType
	 * @return - the number of events for a specific entity and a specific eventType
	 */
	int getEventCountOfEntity(long entityId, long eventTypeId);

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

	/**
	 * This method returns a list of all mappingConditions, which belong to a specific eventEntityMapping.
	 * @param eventEntityMappingId - the unique identifier of the eventEntityMapping
	 * @return - a list of all mappingConditions, which belong to a specific eventEntityMapping
	 */
	List<MappingCondition> getMappingConditions(long eventEntityMappingId);
}
