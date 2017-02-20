package de.hpi.bpt.argos.persistence.database;

import de.hpi.bpt.argos.persistence.model.event.Event;
import de.hpi.bpt.argos.persistence.model.event.EventQuery;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

			Configuration configuration = new Configuration();

			configuration.configure();
			configuration.setProperty("hibernate.connection.username",
					propertyEditor.getProperty(DatabaseConnection.getDatabaseConnectionUsernamePropertyKey()));

			configuration.setProperty("hibernate.connection.password",
					propertyEditor.getProperty(DatabaseConnection.getDatabaseConnectionPasswordPropertyKey()));

			configuration.setProperty("hibernate.connection.url",
					String.format("jdbc:mysql://%1$s/argosbackend?createDatabaseIfNotExist=true",
							propertyEditor.getProperty(DatabaseConnection.getDatabaseConnectionHostPropertyKey())));


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
		Transaction tx = null;
		Query<ProductFamily> query = null;
		try {
			tx = session.beginTransaction();
			query = session.createQuery("FROM ProductFamilyImpl",
						ProductFamily.class);

			List<ProductFamily> productFamilies = query.list();

			tx.commit();
			return productFamilies;
		} catch (Exception exception) {
			if (tx != null) {
				tx.rollback();
			}
			logErrorWhileGettingEntities(exception, query);
			return new ArrayList<>();
		} finally {
			session.close();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProductFamily getProductFamily(long productFamilyId) {
		Session session = databaseSessionFactory.openSession();
		Transaction tx = null;
		Query<ProductFamily> query = null;
		try {
			tx = session.beginTransaction();
			query = session.createQuery("FROM ProductFamilyImpl pf "
							+ "WHERE pf.id = :productFamilyId",
						ProductFamily.class)
						.setParameter("productFamilyId", productFamilyId);

			ProductFamily productFamily = query.uniqueResult();

			tx.commit();
			return productFamily;
		} catch (Exception exception) {
			if (tx != null) {
				tx.rollback();
			}
			logErrorWhileGettingEntities(exception, query);
			return null;
		} finally {
			session.close();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Product getProduct(long productId) {
		Session session = databaseSessionFactory.openSession();
		Transaction tx = null;
		Query<Product> query = null;
		try {
			tx = session.beginTransaction();
			query = session.createQuery("FROM ProductImpl p "
							+ "WHERE p.id = :productId",
					Product.class)
					.setParameter("productId", productId);

			Product product = query.uniqueResult();

			tx.commit();
			return product;
		} catch (Exception exception) {
			if (tx != null) {
				tx.rollback();
			}
			logErrorWhileGettingEntities(exception, query);
			return null;
		} finally {
			session.close();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<EventType, Integer> getEventTypes(long productId) {
		Session session = databaseSessionFactory.openSession();
		Transaction tx = null;
		Query<Event> query = null;
		try {
			tx = session.beginTransaction();

			query = session.createQuery("FROM EventImpl e WHERE e.product.id = :productId",
						Event.class)
						.setParameter("productId", productId);

			List<Event> events = query.list();

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

			tx.commit();
			return eventTypes;
		} catch (Exception exception) {
			if (tx != null) {
				tx.rollback();
			}
			logErrorWhileGettingEntities(exception, query);
			return new HashMap<>();
		} finally {
			session.close();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Event> getEvents(long productId, long eventTypeId, int indexFrom, int indexTo) {
		Session session = databaseSessionFactory.openSession();
		Transaction tx = null;
		Query<Event> query = null;
		try {
			tx = session.beginTransaction();

			query = session.createQuery("FROM EventImpl ev "
							+ "WHERE ev.product.id = :productId AND ev.eventType.id = :eventTypeId "
							+ "ORDER BY ev.id ASC",
						Event.class)
						.setParameter("productId", productId)
						.setParameter("eventTypeId", eventTypeId)
						.setFirstResult(indexFrom)
						.setMaxResults(indexTo);

			List<Event> events = query.list();
			tx.commit();
			return events;
		} catch (Exception exception) {
			if (tx != null) {
				tx.rollback();
			}
			logErrorWhileGettingEntities(exception, query);
			return new ArrayList<>();
		} finally {
			session.close();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventType getEventType(long eventTypeId) {
		Session session = databaseSessionFactory.openSession();
		Transaction tx = null;
		Query<EventType> query = null;
		try {
			tx = session.beginTransaction();

			query = session.createQuery("FROM EventTypeImpl et "
							+ "WHERE et.id = :eventTypeId",
						EventType.class)
						.setParameter("eventTypeId", eventTypeId);

			EventType eventType = query.getSingleResult();

			tx.commit();
			return eventType;
		} catch (Exception exception) {
			if (tx != null) {
				tx.rollback();
			}
			logErrorWhileGettingEntities(exception, query);
			return null;
		} finally {
			session.close();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Product getProduct(int productOrderNumber) {
		Session session = databaseSessionFactory.openSession();
		Transaction tx = null;
		Query<Product> query = null;
		try {
			tx = session.beginTransaction();

			query = session.createQuery("FROM ProductImpl pr "
							+ "WHERE pr.orderNumber = :orderNumber",
						Product.class)
						.setParameter("orderNumber", productOrderNumber);

			Product product = query.getSingleResult();

			tx.commit();
			return product;
		} catch (Exception exception) {
			if (tx != null) {
				tx.rollback();
			}
			logErrorWhileGettingEntities(exception, query);
			return null;
		} finally {
			session.close();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProductFamily getProductFamily(String productFamilyName) {
		Session session = databaseSessionFactory.openSession();
		Transaction tx = null;
		Query<ProductFamily> query = null;
		try {
			tx = session.beginTransaction();

			query = session.createQuery("FROM ProductFamilyImpl pf "
							+ "WHERE pf.name = :productFamilyName",
						ProductFamily.class)
						.setParameter("productFamilyName", productFamilyName);

			ProductFamily productFamily = query.getSingleResult();

			tx.commit();
			return productFamily;
		} catch (Exception exception) {
			if (tx != null) {
				tx.rollback();
			}
			logErrorWhileGettingEntities(exception, query);
			return null;
		} finally {
			session.close();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<EventType> getEventTypes() {
		Session session = databaseSessionFactory.openSession();
		Transaction tx = null;
		Query<EventType> query = null;
		try {
			tx = session.beginTransaction();

			query = session.createQuery("FROM EventTypeImpl et ",
						EventType.class);

			List<EventType> eventTypes = query.list();

			tx.commit();
			return eventTypes;
		} catch (Exception exception) {
			if (tx != null) {
				tx.rollback();
			}
			logErrorWhileGettingEntities(exception, query);
			return new ArrayList<>();
		} finally {
			session.close();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Event getEvent(long eventId) {
		Session session = databaseSessionFactory.openSession();
		Transaction tx = null;
		Query<Event> query = null;
		try {
			tx = session.beginTransaction();

			query = session.createQuery("FROM EventImpl e "
							+ "WHERE e.id = :eventId", Event.class)
						.setParameter("eventId", eventId);

			Event event = query.getSingleResult();

			tx.commit();
			return event;
		} catch (Exception exception) {
			if (tx != null) {
				tx.rollback();
			}
			logErrorWhileGettingEntities(exception, query);
			return null;
		} finally {
			session.close();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<EventQuery> getEventQueries() {
		Session session = databaseSessionFactory.openSession();
		Transaction tx = null;
		Query<EventQuery> query = null;
		try {
			tx = session.beginTransaction();

			query = session.createQuery("FROM EventQueryImpl eq",
					EventQuery.class);

			List<EventQuery> eventQueries = query.list();

			tx.commit();
			return eventQueries;
		} catch (Exception exception) {
			if (tx != null) {
				tx.rollback();
			}
			logErrorWhileGettingEntities(exception, query);
			return new ArrayList<EventQuery>();
		} finally {
			session.close();
		}
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
}
