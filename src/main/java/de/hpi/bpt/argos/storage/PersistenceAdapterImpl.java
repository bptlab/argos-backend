package de.hpi.bpt.argos.storage;

import de.hpi.bpt.argos.common.ObservableImpl;
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
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public final class PersistenceAdapterImpl extends ObservableImpl<PersistenceArtifactUpdateObserver> implements PersistenceAdapter {
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
	public List<Event> getEvents(long entityOwnerId, long eventTypeId, int listStartIndex, int listEndIndex) {
		Session session = databaseAccess.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();

		Query<Event> query = session.createQuery("FROM EventImpl event "
				+ "WHERE event.typeId = :eventTypeId AND event.entityId = :entityOwnerId",
				Event.class)
				.setParameter("eventTypeId", eventTypeId)
				.setParameter("entityOwnerId", entityOwnerId);

		return databaseAccess.getArtifacts(session, query, transaction, query::list, new ArrayList<>());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getEventCountOfEventType(long eventTypeId) {
		Session session = databaseAccess.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();

		Query<Integer> query = session.createQuery("SELECT count(*) FROM EventImpl event "
						+ "WHERE event.typeId = :eventTypeId",
				Integer.class)
				.setParameter("eventTypeId", eventTypeId);

		return databaseAccess.getArtifacts(session, query, transaction, query::getSingleResult, 0);
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
	public List<MappingCondition> getMappingConditionsForMapping(long entityMappingId) {
		Session session = databaseAccess.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();

		Query<MappingCondition> query = session.createQuery("FROM MappingConditionImpl mappingCondition WHERE "
						+ "mappingCondition.mappingId = :entityMappingId",
				MappingCondition.class)
				.setParameter("entityMappingId", entityMappingId);

		return databaseAccess.getArtifacts(session, query, transaction, query::list, new ArrayList<>());
	}
}
