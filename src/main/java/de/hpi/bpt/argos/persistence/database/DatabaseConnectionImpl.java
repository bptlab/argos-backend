package de.hpi.bpt.argos.persistence.database;

import de.hpi.bpt.argos.core.Argos;
import de.hpi.bpt.argos.persistence.model.event.Event;
import de.hpi.bpt.argos.persistence.model.event.type.EventType;
import de.hpi.bpt.argos.persistence.model.parsing.DataFile;
import de.hpi.bpt.argos.persistence.model.product.Product;
import de.hpi.bpt.argos.persistence.model.product.ProductConfiguration;
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
	protected int batchSize;

	protected static final int TO_PERCENT = 100;

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

			batchSize = Integer.parseInt(configuration.getProperty("hibernate.jdbc.batch_size"));

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
			logger.error("Can't connect to the database server");
			logger.trace("Reason: ", e);
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
	public ProductConfiguration getProductConfiguration(long productConfigurationId) {
		Session session = databaseSessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		Query<ProductConfiguration> query = session.createQuery("FROM ProductConfiguration pc "
						+ " left join fetch pc.errorTypes"
						+ " where pc.id = :productConfigurationId",
				ProductConfiguration.class)
				.setParameter("productConfigurationId", productConfigurationId);

		return getEntities(session, query, transaction, query::uniqueResult, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<EventType, Integer> getEventTypesForProduct(long productId) {
		Session session = databaseSessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		Query<Event> query = session.createQuery("FROM EventImpl e WHERE e.productConfiguration.product.id = :productId",
				Event.class)
				.setParameter("productId", productId);

		List<Event> events = getEntities(session, query, transaction, query::list, new ArrayList<>());

		return getEventTypesOccurrences(events);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<EventType, Integer> getEventTypesForProductConfiguration(long productConfigurationId) {
		Session session = databaseSessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		Query<Event> query = session.createQuery("FROM EventImpl e WHERE e.productConfiguration.id = :configurationId",
				Event.class)
				.setParameter("configurationId", productConfigurationId);

		List<Event> events = getEntities(session, query, transaction, query::list, new ArrayList<>());

		return getEventTypesOccurrences(events);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Event> getEventsForProduct(long productId, long eventTypeId, int indexFrom, int indexTo) {
		Session session = databaseSessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		Query<Event> query = session.createQuery("FROM EventImpl ev "
						+ "WHERE ev.productConfiguration.product.id = :productId AND ev.eventType.id = :eventTypeId "
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
	public List<Event> getEventsForProductConfiguration(long productConfigurationId, long eventTypeId, int indexFrom, int indexTo) {
		Session session = databaseSessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		Query<Event> query = session.createQuery("FROM EventImpl ev "
						+ "WHERE ev.productConfiguration.id = :configurationId AND ev.eventType.id = :eventTypeId "
						+ "ORDER BY ev.id ASC",
				Event.class)
				.setParameter("configurationId", productConfigurationId)
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
	public Product getProductByExternalId(long externalProductId) {
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
	public DataFile getDataFile(String path) {
		Session session = databaseSessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		Query<DataFile> query = session.createQuery("FROM DataFileImpl df "
				+ "WHERE df.path = :path", DataFile.class)
				.setParameter("path", path);

		return getEntities(session, query, transaction, query::getSingleResult, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean saveEntities(PersistenceEntity... entities) {
		Session session = databaseSessionFactory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			int count = 0;

			for (PersistenceEntity entity : entities) {
				session.saveOrUpdate(entity);

				if (++count % batchSize == 0) {

					logger.debug(String.format("saving entities... %1$d / %2$d = %3$,2f%%",
							count,
							entities.length,
							count * TO_PERCENT / (double) entities.length));

					session.flush();
					session.clear();
				}
			}

			tx.commit();
			return true;
		} catch (Exception exception) {
			if (tx != null) {
				tx.rollback();
			}
			logger.error("can't save entities in database");
			logger.trace("Reason: ", exception);
			return false;
		} finally {
			session.close();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean deleteEntities(PersistenceEntity... entities) {
		Session session = databaseSessionFactory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();

			for (PersistenceEntity entity : entities) {
				session.delete(entity);
			}

			tx.commit();
			return true;
		} catch (Exception exception) {
			if (tx != null) {
				tx.rollback();
			}
			logger.error("can't delete entities in database");
			logger.trace("Reason: ", exception);
			return false;
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
	protected <R, Q> R getEntities(Session session, Query<Q> query, Transaction transaction, Callable<R> getValue, R defaultValue) {

		try {
			R result = getValue.call();
			transaction.commit();
			return result;
		} catch (NoResultException e) {
			logNoEntitiesFound(query, e);
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
	 * This method returns a map of event types with their occurrences.
	 * @param events - the events to get the types and their occurrences from
	 * @return - a map of event types with their occurrences
	 */
	protected Map<EventType, Integer> getEventTypesOccurrences(List<Event> events) {
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
	 * This method logs an exception which occurs while trying to get entities from the database server.
	 * @param exception - a thrown exception
	 * @param query - the query that caused the exception
	 */
	protected void logErrorWhileGettingEntities(Throwable exception, Query query) {
		String queryString = "<empty query>";
		if (query != null) {
			queryString = query.getQueryString();
		}
		logger.error(String.format("can't retrieve entities from database: %1$s", queryString));
		logger.trace("Reason: ", exception);
	}

	/**
	 * This method logs an info, when no entities were found in the database.
	 * @param query - the query
	 * @param exception - the thrown exception
	 */
	protected void logNoEntitiesFound(Query query, Throwable exception) {
		String queryString = "<empty query>";
		if (query != null) {
			queryString = query.getQueryString();
		}
		logger.debug(String.format("no entities for query '%1$s' found", queryString));
		logger.trace("Reason: ", exception);
	}
}
