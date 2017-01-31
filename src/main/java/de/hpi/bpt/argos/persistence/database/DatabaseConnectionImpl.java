package de.hpi.bpt.argos.persistence.database;

import de.hpi.bpt.argos.persistence.model.event.data.Event;
import de.hpi.bpt.argos.persistence.model.event.type.EventType;
import de.hpi.bpt.argos.persistence.model.product.Product;
import de.hpi.bpt.argos.persistence.model.product.ProductFamily;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.hibernate.service.spi.ServiceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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
			databaseSessionFactory = new Configuration()
					.configure()
					.buildSessionFactory();
		} catch (ServiceException e) {
			logErrorWhileConnectingToDatabaseServer(e);
			return false;
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProductFamily> listAllProductFamilies() {
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
		}finally {
			session.close();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<EventType, Integer> listAllEventTypesForProduct(int productId) {
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
	public List<Event> listEventsForProductOfTypeInRange(int productId, int eventTypeId, int indexFrom, int
			indexTo) {
		Session session = databaseSessionFactory.openSession();
		Transaction tx = null;
		Query<Event> query = null;
		try {
			tx = session.beginTransaction();

			query = session.createQuery("FROM EventImpl ev " +
							"WHERE ev.product.id = :productId AND ev.eventType.id = :eventTypeId " +
							"ORDER BY ev.id ASC",
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
	public EventType getEventType(int eventTypeId) {
		Session session = databaseSessionFactory.openSession();
		Transaction tx = null;
		Query<EventType> query = null;
		try {
			tx = session.beginTransaction();

			query = session.createQuery("FROM EventTypeImpl et " +
							"WHERE et.id = :eventTypeId",
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

			query = session.createQuery("FROM ProductImpl pr " +
							"WHERE pr.orderNumber = :orderNumber",
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

			query = session.createQuery("FROM ProductFamilyImpl pf " +
							"WHERE pf.name = :productFamilyName",
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
	public List<EventType> listEventTypes() {
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
	public Event getSingleEvent(int eventId) {
		Session session = databaseSessionFactory.openSession();
		Transaction tx = null;
		Query<Event> query = null;
		try {
			tx = session.beginTransaction();

			query = session.createQuery("FROM EventImpl e " +
							"WHERE e.id = :eventId", Event.class)
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
	public void saveProductFamilies(List<ProductFamily> productFamilies) {
		saveEntities(productFamilies);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void saveEventTypes(List<EventType> eventTypes) {
		saveEntities(eventTypes);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void saveEvents(List<Event> events) {
		saveEntities(events);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void saveProducts(List<Product> products) {
		saveEntities(products);
	}

	/**
	 * This method makes the database call to save a set of entities in the database server.
	 * @param entities - a list of entities to save
	 * @param <T> - the type of each entity
	 */
	protected <T> void saveEntities(List<T> entities) {
		Session session = databaseSessionFactory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();

			for(T entity : entities) {
				session.saveOrUpdate(entity);
			}

			tx.commit();
		} catch (Exception exception) {
			if (tx != null) {
				tx.rollback();
			}
			logErrorWhileSavingEntities(exception);
		} finally {
			session.close();
		}
	}

	/**
	 * This method logs exceptions to the console.
	 * @param head - a description what happened
	 * @param exception - the thrown exception
	 */
	protected void logError(String head, Throwable exception) {
		logger.error(head, exception);
	}

	/**
	 * This method logs an exception which occurs while trying to connect to the database server.
	 * @param exception - the thrown exception
	 */
	protected void logErrorWhileConnectingToDatabaseServer(Throwable exception) {
		logError("can't connect to the database server", exception);
	}

	/**
	 * This method logs an exception which occurs while trying to get entities from the database server.
	 * @param exception
	 */
	protected void logErrorWhileGettingEntities(Throwable exception, Query query) {
		logError("can't retrieve entities from database: " + (query != null ? query.getQueryString() : "<empty query>"), exception);
	}

	/**
	 * This method logs an exception which occurs while trying to save entities in the database server.
	 * @param exception - the thrown exception
	 */
	protected void logErrorWhileSavingEntities(Throwable exception) {
		logError("can't save entities in database", exception);
	}
}
