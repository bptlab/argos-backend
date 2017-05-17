package de.hpi.bpt.argos.storage;

import de.hpi.bpt.argos.common.ObservableImpl;
import de.hpi.bpt.argos.storage.dataModel.PersistenceArtifact;
import de.hpi.bpt.argos.storage.dataModel.attribute.Attribute;
import de.hpi.bpt.argos.storage.dataModel.attribute.type.TypeAttribute;
import de.hpi.bpt.argos.storage.dataModel.entity.Entity;
import de.hpi.bpt.argos.storage.dataModel.entity.type.EntityType;
import de.hpi.bpt.argos.storage.dataModel.event.Event;
import de.hpi.bpt.argos.storage.dataModel.event.EventImpl;
import de.hpi.bpt.argos.storage.dataModel.event.query.EventQuery;
import de.hpi.bpt.argos.storage.dataModel.event.type.EventType;
import de.hpi.bpt.argos.storage.dataModel.event.type.EventTypeImpl;
import de.hpi.bpt.argos.storage.dataModel.mapping.EventEntityMapping;
import de.hpi.bpt.argos.storage.dataModel.mapping.MappingCondition;
import de.hpi.bpt.argos.storage.util.DataFile;
import de.hpi.bpt.argos.util.LoggerUtilImpl;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public final class PersistenceAdapterImpl extends ObservableImpl<PersistenceArtifactUpdateObserver> implements PersistenceAdapter {
	private static final Logger logger = LoggerFactory.getLogger(PersistenceAdapterImpl.class);

	private static PersistenceAdapter instance;
	private DatabaseAccess databaseAccess;

	/**
	 * This constructor hides the default public one to implement the singleton pattern.
	 */
	private PersistenceAdapterImpl() {
		databaseAccess = new DatabaseAccessImpl();
	}

	/**
	 * This method returns the singleton instance of this class.
	 * @return - the singleton instance of this class
	 */
	public static PersistenceAdapter getInstance() {
		if (instance == null) {
			instance = new PersistenceAdapterImpl();
		}

		return instance;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean establishConnection() {
		return databaseAccess.establishConnection();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean saveArtifacts(PersistenceArtifact... artifacts) {
		return databaseAccess.saveArtifacts(artifacts);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean deleteArtifacts(PersistenceArtifact... artifacts) {
		return databaseAccess.deleteArtifacts(artifacts);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean createArtifact(PersistenceArtifact artifact, String fetchUri) {
		if (!saveArtifacts(artifact)) {
			return false;
		}

		notifyObservers((PersistenceArtifactUpdateObserver observer) ->
				observer.onArtifactUpdated(PersistenceArtifactUpdateType.CREATE, artifact, fetchUri));

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean createEvent(Event event, Entity eventOwner, String fetchUri) {
		if (!saveArtifacts(event)) {
			return false;
		}

		notifyObservers((PersistenceArtifactUpdateObserver observer) ->
				observer.onEventCreation(eventOwner, event, fetchUri));

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean updateArtifact(PersistenceArtifact artifact, String fetchUri) {
		if (!saveArtifacts(artifact)) {
			return false;
		}

		notifyObservers((PersistenceArtifactUpdateObserver observer) ->
				observer.onArtifactUpdated(PersistenceArtifactUpdateType.MODIFY, artifact, fetchUri));

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean deleteArtifact(PersistenceArtifact artifact, String fetchUri) {
		if (!deleteArtifacts(artifact)) {
			return false;
		}

		notifyObservers((PersistenceArtifactUpdateObserver observer) ->
				observer.onArtifactUpdated(PersistenceArtifactUpdateType.DELETE, artifact, fetchUri));

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Attribute> getAttributes(long ownerId) {
		Session session = databaseAccess.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();

		Query<Attribute> query = session.createQuery("FROM AttributeImpl attribute WHERE attribute.ownerId = :ownerId",
				Attribute.class)
				.setParameter("ownerId", ownerId);

		return databaseAccess.getArtifacts(session, query, transaction, query::list, new ArrayList<>());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TypeAttribute getTypeAttribute(long id) {
		Session session = databaseAccess.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();

		Query<TypeAttribute> query = session.createQuery("FROM TypeAttributeImpl typeAttribute WHERE typeAttribute.id = :id",
				TypeAttribute.class)
				.setParameter("id", id);

		return databaseAccess.getArtifacts(session, query, transaction, query::getSingleResult, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<TypeAttribute> getTypeAttributes(long typeId) {
		Session session = databaseAccess.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();

		Query<TypeAttribute> query = session.createQuery("FROM TypeAttributeImpl typeAttribute WHERE typeAttribute.typeId = :typeId",
				TypeAttribute.class)
				.setParameter("typeId", typeId);

		return databaseAccess.getArtifacts(session, query, transaction, query::list, new ArrayList<>());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Entity> getEntities() {
		Session session = databaseAccess.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();

		Query<Entity> query = session.createQuery("FROM EntityImpl entity",
				Entity.class);

		return databaseAccess.getArtifacts(session, query, transaction, query::getResultList, new ArrayList<>());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getEntitiesCount() {
		Session session = databaseAccess.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();

		Table entityTable = Entity.class.getAnnotation(Table.class);
		String eventTableName = "Entity";

		if (entityTable != null) {
			eventTableName = entityTable.name();
		}

		String sqlQuery = String.format("SELECT count(*) FROM %1$s", eventTableName);
		Query query = session.createNativeQuery(sqlQuery);

		String stringResult = databaseAccess.getArtifacts(session, query, transaction, query::getSingleResult, 0).toString();
		return Integer.parseInt(stringResult);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Entity getEntity(long id) {
		Session session = databaseAccess.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();

		Query<Entity> query = session.createQuery("FROM EntityImpl entity WHERE entity.id = :id",
				Entity.class)
				.setParameter("id", id);

		return databaseAccess.getArtifacts(session, query, transaction, query::getSingleResult, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Entity getMappingEntity(String sqlQuery) {
		if (sqlQuery == null || sqlQuery.length() == 0) {
			return null;
		}

		Session session = databaseAccess.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();

		Query query = session.createNativeQuery(sqlQuery);

		List<?> entityIds = databaseAccess.getArtifacts(session, query, transaction, query::list, new ArrayList<>());

		if (entityIds.size() != 1) {
			return null;
		}

		return getEntity(Long.parseLong(entityIds.get(0).toString()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Entity> getEntities(long parentId, long entityTypeId) {
		Session session = databaseAccess.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();

		Query<Entity> query = session.createQuery("FROM EntityImpl entity "
				+ "WHERE entity.typeId = :entityTypeId AND entity.parentId = :parentId",
				Entity.class)
				.setParameter("entityTypeId", entityTypeId)
				.setParameter("parentId", parentId);

		return databaseAccess.getArtifacts(session, query, transaction, query::list, new ArrayList<>());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EntityType getEntityType(long id) {
		Session session = databaseAccess.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();

		Query<EntityType> query = session.createQuery("FROM EntityTypeImpl entityType WHERE entityType.id = :id",
				EntityType.class)
				.setParameter("id", id);

		return databaseAccess.getArtifacts(session, query, transaction, query::getSingleResult, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<EntityType> getEntityTypes() {
		Session session = databaseAccess.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();

		Query<EntityType> query = session.createQuery("FROM EntityTypeImpl entityType",
				EntityType.class);

		return databaseAccess.getArtifacts(session, query, transaction, query::list, new ArrayList<>());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getEntityTypesCount() {
		Session session = databaseAccess.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();

		Table entityTypeTable = Entity.class.getAnnotation(Table.class);
		String entityTypeTableName = "EntityType";

		if (entityTypeTable != null) {
			entityTypeTableName = entityTypeTable.name();
		}

		String sqlQuery = String.format("SELECT count(*) FROM %1$s", entityTypeTableName);
		Query query = session.createNativeQuery(sqlQuery);

		String stringResult = databaseAccess.getArtifacts(session, query, transaction, query::getSingleResult, 0).toString();
		return Integer.parseInt(stringResult);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Event> getEvents(long eventTypeId, int listStartIndex, int listEndIndex, Long... entityIds) {
		if (entityIds.length == 0) {
			return new ArrayList<>();
		}

		StringBuilder sqlWhere = new StringBuilder();
		sqlWhere.append(String.format("(event.entityId = %1$d)", entityIds[0]));

		for (int i = 1; i < entityIds.length; i++) {
			sqlWhere.append(String.format(" OR (event.entityId = %1$d)", entityIds[i]));
		}

		Session session = databaseAccess.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();

		String sqlQuery = String.format(
				"FROM EventImpl event "
						+ "WHERE event.typeId = %1$d"
						+ "AND (%2$s)",
				eventTypeId,
				sqlWhere.toString()
		);

		Query<Event> query = session.createQuery(sqlQuery,
				Event.class)
				.setFirstResult(listStartIndex)
				.setMaxResults(listEndIndex);

		return databaseAccess.getArtifacts(session, query, transaction, query::getResultList, new ArrayList<>());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Event> getEvents(long entityOwnerId) {
		Session session = databaseAccess.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();

		Query<Event> query = session.createQuery("FROM EventImpl event "
						+ "WHERE event.entityId = :entityOwnerId",
				Event.class)
				.setParameter("entityOwnerId", entityOwnerId);

		return databaseAccess.getArtifacts(session, query, transaction, query::list, new ArrayList<>());
	}

	/**
	 * {@inheritDoc}
	 * @param entityIds
	 */
	@Override
	public List<EventType> getEventTypes(Long... entityIds) {
		if (entityIds.length == 0) {
			return new ArrayList<>();
		}

		List<Long> eventTypeIds = getEventTypeIds(entityIds);
		Session session = databaseAccess.getSessionFactory().openSession();
		return databaseAccess.getArtifactsById(session, EventTypeImpl.class, eventTypeIds.toArray(new Long[eventTypeIds.size()]));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getEventCountOfEntity(long entityId, long eventTypeId) {
		Session session = databaseAccess.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();

		Table eventTable = EventImpl.class.getAnnotation(Table.class);
		String eventTableName = "Event";

		if (eventTable != null) {
			eventTableName = eventTable.name();
		}

		String sqlQuery = String.format("SELECT count(*) FROM %1$s WHERE %1$s.typeId = %2$d and %1$s.entityId = %3$d",
				eventTableName, eventTypeId, entityId);
		Query query = session.createNativeQuery(sqlQuery);

		String stringResult = databaseAccess.getArtifacts(session, query, transaction, query::getSingleResult, 0).toString();
		return Integer.parseInt(stringResult);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Event> getEventsOfEventType(long eventTypeId) {
		Session session = databaseAccess.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();

		Query<Event> query = session.createQuery("FROM EventImpl event "
						+ "WHERE event.typeId = :eventTypeId",
				Event.class)
				.setParameter("eventTypeId", eventTypeId);

		return databaseAccess.getArtifacts(session, query, transaction, query::list, new ArrayList<>());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getEventCountOfEventType(long eventTypeId) {
		Session session = databaseAccess.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();

		Table eventTable = EventImpl.class.getAnnotation(Table.class);
		String eventTableName = "Event";

		if (eventTable != null) {
			eventTableName = eventTable.name();
		}

		String sqlQuery = String.format("SELECT count(*) FROM %1$s WHERE %1$s.typeId = %2$d", eventTableName, eventTypeId);
		Query query = session.createNativeQuery(sqlQuery);

		String stringResult = databaseAccess.getArtifacts(session, query, transaction, query::getSingleResult, 0).toString();
		return Integer.parseInt(stringResult);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventType getEventType(long id) {
		Session session = databaseAccess.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();

		Query<EventType> query = session.createQuery("FROM EventTypeImpl eventType WHERE eventType.id = :id",
				EventType.class)
				.setParameter("id", id);

		return databaseAccess.getArtifacts(session, query, transaction, query::getSingleResult, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<EventType> getEventTypes() {
		Session session = databaseAccess.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();

		Query<EventType> query = session.createQuery("FROM EventTypeImpl eventType",
				EventType.class);

		return databaseAccess.getArtifacts(session, query, transaction, query::list, new ArrayList<>());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<EventQuery> getEventQueries(long eventTypeId) {
		Session session = databaseAccess.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();

		Query<EventQuery> query = session.createQuery("FROM EventQueryImpl eventQuery WHERE eventQuery.typeId = :eventTypeId",
				EventQuery.class)
				.setParameter("eventTypeId", eventTypeId);

		return databaseAccess.getArtifacts(session, query, transaction, query::list, new ArrayList<>());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventQuery getEventQuery(long eventQueryId) {
		Session session = databaseAccess.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();

		Query<EventQuery> query = session.createQuery("FROM EventQueryImpl eventQuery WHERE eventQuery.id = :id",
				EventQuery.class)
				.setParameter("id", eventQueryId);

		return databaseAccess.getArtifacts(session, query, transaction, query::getSingleResult, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventEntityMapping getEventEntityMapping(long mappingId) {
		Session session = databaseAccess.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();

		Query<EventEntityMapping> query = session.createQuery("FROM EventEntityMappingImpl mapping WHERE mapping.id = :id",
				EventEntityMapping.class)
				.setParameter("id", mappingId);

		return databaseAccess.getArtifacts(session, query, transaction, query::getSingleResult, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<EventEntityMapping> getEventEntityMappingsForEntityType(long entityTypeId) {
		Session session = databaseAccess.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();

		Query<EventEntityMapping> query = session.createQuery("FROM EventEntityMappingImpl eventEntityMapping WHERE "
				+ "eventEntityMapping.entityTypeId = :entityTypeId",
				EventEntityMapping.class)
				.setParameter("entityTypeId", entityTypeId);

		return databaseAccess.getArtifacts(session, query, transaction, query::list, new ArrayList<>());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<EventEntityMapping> getEventEntityMappingsForEventType(long eventTypeId) {
		Session session = databaseAccess.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();

		Query<EventEntityMapping> query = session.createQuery("FROM EventEntityMappingImpl eventEntityMapping WHERE "
						+ "eventEntityMapping.eventTypeId = :eventTypeId",
				EventEntityMapping.class)
				.setParameter("eventTypeId", eventTypeId);

		return databaseAccess.getArtifacts(session, query, transaction, query::list, new ArrayList<>());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<MappingCondition> getMappingConditions(long eventEntityMappingId) {
		Session session = databaseAccess.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();

		Query<MappingCondition> query = session.createQuery("FROM MappingConditionImpl mappingCondition WHERE "
						+ "mappingCondition.mappingId = :eventEntityMappingId",
				MappingCondition.class)
				.setParameter("eventEntityMappingId", eventEntityMappingId);

		return databaseAccess.getArtifacts(session, query, transaction, query::list, new ArrayList<>());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DataFile getDataFile(String path) {
		Session session = databaseAccess.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();

		Query<DataFile> query = session.createQuery("FROM DataFileImpl dataFile WHERE "
						+ "dataFile.path = :path",
				DataFile.class)
				.setParameter("path", path);

		return databaseAccess.getArtifacts(session, query, transaction, query::getSingleResult, null);
	}

	/**
	 * This method returns a list of unique identifiers of eventTypes for a list of entities.
	 * @param entityIds - a list of unique identifiers of entities
	 * @return - a list of all eventTypeIds, where at least one event occurred for at least one of the given entities
	 */
	private List<Long> getEventTypeIds(Long... entityIds) {

		Session session = databaseAccess.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();

		StringBuilder eventTypeIdsWhere = new StringBuilder();
		eventTypeIdsWhere.append(String.format("(event.EntityId = %1$d)", entityIds[0]));

		for (int i = 1; i < entityIds.length; i++) {
			eventTypeIdsWhere.append(String.format(" OR (event.EntityId = %1$d)", entityIds[i]));
		}

		Table eventTable = EventImpl.class.getAnnotation(Table.class);
		String eventTableName = "Event";

		if (eventTable != null) {
			eventTableName = eventTable.name();
		}

		String eventTypeIdsQuery = String.format(
				"SELECT TypeId "
						+ "FROM ( "
						+ "SELECT TypeId "
						+ "FROM %1$s AS event "
						+ "WHERE %2$s "
						+ "GROUP BY TypeId ) AS EventTypeTable",
				eventTableName,
				eventTypeIdsWhere);

		Query query = session.createNativeQuery(eventTypeIdsQuery);

		List<Object> resultList = databaseAccess.getArtifacts(session, query, transaction, query::list, new ArrayList<>());

		List<Long> eventTypeIds = new ArrayList<>();

		try {
			for (Object result : resultList) {
				eventTypeIds.add(Long.parseLong(result.toString()));
			}
		} catch (Exception e) {
			LoggerUtilImpl.getInstance().error(logger, "cannot convert result to long", e);
			return new ArrayList<>();
		}

		return eventTypeIds;
	}
}
