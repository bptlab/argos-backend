package de.hpi.bpt.argos.persistence.database;

import de.hpi.bpt.argos.core.Argos;
import de.hpi.bpt.argos.persistence.model.event.Event;
import de.hpi.bpt.argos.persistence.model.event.type.EventType;
import de.hpi.bpt.argos.persistence.model.product.Product;
import de.hpi.bpt.argos.persistence.model.product.ProductFamily;
import de.hpi.bpt.argos.properties.PropertyEditor;
import de.hpi.bpt.argos.properties.PropertyEditorImpl;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class DatabaseConnectionImpl implements DatabaseConnection {
	protected static final Logger logger = LoggerFactory.getLogger(DatabaseConnectionImpl.class);

	protected SessionFactory databaseSessionFactory;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean setup() {
		try {

			PropertyEditor propertyEditor = new PropertyEditorImpl();
			boolean testMode = Boolean.parseBoolean(propertyEditor.getProperty(Argos.getArgosBackendTestModePropertyKey()));

			Configuration configuration = new Configuration();

			configuration.configure();
			configuration.setProperty("hibernate.connection.username",
					propertyEditor.getProperty(DatabaseConnection.getDatabaseConnectionUsernamePropertyKey()));

			configuration.setProperty("hibernate.connection.password",
					propertyEditor.getProperty(DatabaseConnection.getDatabaseConnectionPasswordPropertyKey()));

			if (!testMode) {
				configuration.setProperty("hibernate.connection.url",
						String.format("jdbc:mysql://%1$s/argosbackend?createDatabaseIfNotExist=true",
								propertyEditor.getProperty(DatabaseConnection.getDatabaseConnectionHostPropertyKey())));

				configuration.setProperty("hibernate.hbm2ddl.auto", "update");
			} else {
				configuration.setProperty("hibernate.connection.url",
						String.format("jdbc:mysql://%1$s/argosbackend_test?createDatabaseIfNotExist=true",
								propertyEditor.getProperty(DatabaseConnection.getDatabaseConnectionHostPropertyKey())));

				// drop existing schema and re-create
				configuration.setProperty("hibernate.hbm2ddl.auto", "create");
			}


			databaseSessionFactory = configuration.buildSessionFactory();

		} catch (ServiceException e) {
			logger.error("can't connect to the database server", e);
			return false;
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProductFamily> getProductFamilies() {
		Session session = databaseSessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		Query<ProductFamily> query = session.createQuery("FROM ProductFamilyImpl",
				ProductFamily.class);

		return getEntities(session, query, transaction, query::list, new ArrayList<>());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProductFamily getProductFamily(long productFamilyId) {
		Session session = databaseSessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		Query<ProductFamily> query = session.createQuery("FROM ProductFamilyImpl pf "
						+ "WHERE pf.id = :productFamilyId",
				ProductFamily.class)
				.setParameter("productFamilyId", productFamilyId);

		return getEntities(session, query, transaction, query::uniqueResult, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Product getProduct(long productId) {
		Session session = databaseSessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		Query<Product> query = session.createQuery("FROM ProductImpl p "
						+ "WHERE p.id = :productId",
				Product.class)
				.setParameter("productId", productId);

		return getEntities(session, query, transaction, query::uniqueResult, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<EventType, Integer> getEventTypes(long productId) {
		Session session = databaseSessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		Query<Event> query = session.createQuery("FROM EventImpl e WHERE e.product.id = :productId",
				Event.class)
				.setParameter("productId", productId);

		List<Event> events = getEntities(session, query, transaction, query::list, new ArrayList<>());

		Map<EventType, Integer> eventTypes = new HashMap<>();
		for (Event event: events) {
			EventType currentEventType = event.getEventType();
			if (eventTypes.containsKey(currentEventType)) {
				int oldNumberOfEventTypes = eventTypes.get(event.getEventType());
				eventTypes.put(currentEventType, oldNumberOfEventTypes + 1);
			} else {
				eventTypes.put(currentEventType, 1);
			}
		}
		return eventTypes;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Event> getEvents(long productId, long eventTypeId, int indexFrom, int indexTo) {
		Session session = databaseSessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		Query<Event> query = session.createQuery("FROM EventImpl ev "
						+ "WHERE ev.product.id = :productId AND ev.eventType.id = :eventTypeId "
						+ "ORDER BY ev.id ASC",
				Event.class)
				.setParameter("productId", productId)
				.setParameter("eventTypeId", eventTypeId)
				.setFirstResult(indexFrom)
				.setMaxResults(indexTo);

		return getEntities(session, query, transaction, query::list, new ArrayList<>());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventType getEventType(long eventTypeId) {
		Session session = databaseSessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		Query<EventType> query = session.createQuery("FROM EventTypeImpl et "
						+ "WHERE et.id = :eventTypeId",
				EventType.class)
				.setParameter("eventTypeId", eventTypeId);

		return getEntities(session, query, transaction, query::getSingleResult, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventType getEventType(String eventTypeName) {
		Session session = databaseSessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		Query<EventType> query = session.createQuery("FROM EventTypeImpl et "
						+ "WHERE et.name = :eventTypeName",
				EventType.class)
				.setParameter("eventTypeName", eventTypeName);

		return getEntities(session, query, transaction, query::getSingleResult, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Product getProduct(int externalProductId) {
		Session session = databaseSessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		Query<Product> query = session.createQuery("FROM ProductImpl pr "
						+ "WHERE pr.orderNumber = :orderNumber",
				Product.class)
				.setParameter("orderNumber", externalProductId);

		return getEntities(session, query, transaction, query::getSingleResult, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProductFamily getProductFamily(String productFamilyName) {
		Session session = databaseSessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		Query<ProductFamily> query = session.createQuery("FROM ProductFamilyImpl pf "
						+ "WHERE pf.name = :productFamilyName",
				ProductFamily.class)
				.setParameter("productFamilyName", productFamilyName);

		return getEntities(session, query, transaction, query::getSingleResult, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<EventType> getEventTypes() {
		Session session = databaseSessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		Query<EventType> query = session.createQuery("FROM EventTypeImpl et ",
				EventType.class);

		return getEntities(session, query, transaction, query::list, new ArrayList<>());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Event getEvent(long eventId) {
		Session session = databaseSessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		Query<Event> query = session.createQuery("FROM EventImpl e "
				+ "WHERE e.id = :eventId", Event.class)
				.setParameter("eventId", eventId);

		return getEntities(session, query, transaction, query::getSingleResult, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void saveEntities(PersistenceEntity... entities) {
		Session session = databaseSessionFactory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();

			for (PersistenceEntity entity : entities) {
				session.saveOrUpdate(entity);
			}

			tx.commit();
		} catch (Exception exception) {
			if (tx != null) {
				tx.rollback();
			}
			logger.error("can't save entities in database", exception);
		} finally {
			session.close();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteEntities(PersistenceEntity... entities) {
		Session session = databaseSessionFactory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();

			for (PersistenceEntity entity : entities) {
				session.delete(entity);
			}

			tx.commit();
		} catch (Exception exception) {
			if (tx != null) {
				tx.rollback();
			}
			logger.error("can't delete entities in database", exception);
		} finally {
			session.close();
		}
	}

	/**
	 * This method executes a query in a certain transaction context while a session is open and returns the result or a default value.
	 * @param session - the database session, which must be open
	 * @param query - the query to execute and to retrieve the results from
	 * @param transaction - the current transaction
	 * @param getValue - the function to get the results from the query
	 * @param defaultValue - a fall back default value in case anything went wrong or no entities were found
	 * @param <R> - the result type
	 * @param <Q> - the query type
	 * @return - an object of the result value type
	 */
	protected <R, Q> R getEntities(Session session,
								   Query<Q> query,
								   Transaction transaction,
								   Callable<R> getValue,
								   R defaultValue) {

		try {
			R result = getValue.call();
			transaction.commit();
			return result;
		} catch (NoResultException e) {
			logNoEntitiesFound(query);
			return defaultValue;
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			logErrorWhileGettingEntities(e, query);
			return defaultValue;
		} finally {
			session.close();
		}
	}

	/**
	 * This method logs an exception which occurs while trying to get entities from the database server.
	 * @param exception - a thrown exception
	 * @param query - the query that caused the exception
	 */
	protected void logErrorWhileGettingEntities(Throwable exception, Query query) {
		String queryString = "<empty query>";
		if (query != null) {
			queryString = query.getQueryString();
		}
		logger.error(String.format("can't retrieve entities from database: %1$s", queryString), exception);
	}

	/**
	 * This method logs an info, when no entities were found in the database.
	 * @param query - the query
	 */
	protected void logNoEntitiesFound(Query query) {
		String queryString = "<empty query>";
		if (query != null) {
			queryString = query.getQueryString();
		}
		logger.info(String.format("no entities for query '%1$s' found", queryString));
	}
}
