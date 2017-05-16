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
import de.hpi.bpt.argos.storage.util.DataFile;

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
	 * This method saves a new event in the database and notifies connected web socket clients.
	 * @param event - the created event
	 * @param eventOwner - the entity, which owns the event
	 * @param fetchUri - the uri, from where the new event can be fetched
	 * @return - true, if the event was stored in the database
	 */
	boolean createEvent(Event event, Entity eventOwner, String fetchUri);

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
	 * This method returns the typeAttribute, identified by its id.
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
	 * This method returns all entities in the database.
	 * @return - a list of all entities in the database
	 */
	List<Entity> getEntities();

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
	 * This method returns a specific entityType, or null.
	 * @param id - the unique identifier of the requested entityType
	 * @return - the requested entityType, or null
	 */
	EntityType getEntityType(long id);

	/**
	 * This method returns a list of all entityTypes.
	 * @return - a list of all entityTypes
	 */
	List<EntityType> getEntityTypes();

	/**
	 * This method returns a list of events, which belong to at least one of the given entities.
	 * @param eventTypeId - the eventType id of the events
	 * @param listStartIndex - the start index for the event limitation
	 * @param listEndIndex - the end index for the event limitation
	 * @param entityIds - a list of event owner ids
	 * @return - a list of events
	 */
	List<Event> getEvents(long eventTypeId, int listStartIndex, int listEndIndex, Long... entityIds);

	/**
	 * This method returns a list of events, which belong to a specific entity.
	 * @param entityOwnerId - the unique identifier of the entity the events belong to
	 * @return - a list of events, which belong to a specific entity
	 */
	List<Event> getEvents(long entityOwnerId);

	/**
	 * This method returns a list of all eventTypes for a list of entities.
	 * @param entityIds - a list of unique identifiers of entities
	 * @return - a list of all eventTypes, where events occurred for at least one of the given entities
	 */
	List<EventType> getEventTypes(Long... entityIds);

	/**
	 * This method returns the number of events for a specific entity and a specific eventType.
	 * @param entityId - the unique identifier of the entity
	 * @param eventTypeId - the unique identifier of the eventType
	 * @return - the number of events for a specific entity and a specific eventType
	 */
	int getEventCountOfEntity(long entityId, long eventTypeId);

	/**
	 * This method returns a list of events, which belong to a specific eventType.
	 * @param eventTypeId - the unique identifier of the eventType
	 * @return - a list of events, which belong to a specific eventType
	 */
	List<Event> getEventsOfEventType(long eventTypeId);

	/**
	 * This method returns the number of events, for the given eventType.
	 * @param eventTypeId - the unique identifier of the eventType
	 * @return - number of events, for the given eventType
	 */
	int getEventCountOfEventType(long eventTypeId);

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
	 * This method returns a eventQuery with given id.
	 * @param eventQueryId - the unique identifier of the eventQuery
	 * @return - the eventQuery with matching id
	 */
	EventQuery getEventQuery(long eventQueryId);

	/**
	 * This method returns the mapping with given id.
	 * @param mappingId - the unique identifier of the mapping
	 * @return - the mapping with matching id
	 */
	EventEntityMapping getEventEntityMapping(long mappingId);
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

	/**
	 * This method returns a dataFile for the given path, or null if no dataFile exists in the database.
	 * @param path - the path of the requested dataFile
	 * @return - the requested dataFile or null, if no such file exists
	 */
	DataFile getDataFile(String path);
}
