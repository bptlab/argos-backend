package de.hpi.bpt.argos.persistence.database;

import de.hpi.bpt.argos.persistence.model.event.data.Event;
import de.hpi.bpt.argos.persistence.model.event.type.EventType;
import de.hpi.bpt.argos.persistence.model.product.Product;
import de.hpi.bpt.argos.persistence.model.product.ProductFamily;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
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
		try {
			tx = session.beginTransaction();
			List<ProductFamily> productFamilies = session.createQuery("FROM ProductFamilyImpl", ProductFamily.class)
					.list();
			for (ProductFamily productFamily: productFamilies) {
				for (Product product: productFamily.getProducts()) {
					product.getEvents();
					product.setNumberOfEvents();
				}
			}
			tx.commit();
			session.close();
			return productFamilies;
		} catch (Exception exception) {
			if (tx != null) {
				tx.rollback();
			}
			logErrorWhileGettingListOfProductFamilies(exception);
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
		try {
			tx = session.beginTransaction();
			List<Event> events = session.createQuery("FROM EventImpl e WHERE e.product.id = :productId",
					Event.class)
					.setParameter("productId", productId)
					.list();
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
			logErrorWhileGettingEventTypesForProduct(exception);
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
		try {
			tx = session.beginTransaction();

			List<Event> events = session.createQuery("FROM EventImpl ev " +
							"WHERE ev.product.id = :productId AND ev.eventType.id = :eventTypeId " +
							"ORDER BY ev.id ASC", Event.class
							)
						.setParameter("productId", productId)
						.setParameter("eventTypeId", eventTypeId)
						.setFirstResult(indexFrom)
						.setMaxResults(indexTo)
						.list();
			tx.commit();
			return events;
		} catch (Exception exception) {
			if (tx != null) {
				tx.rollback();
			}
			logErrorWhileGettingEventsForProduct(exception);
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
		try {
			tx = session.beginTransaction();
			EventType eventType = session.createQuery("FROM EventTypeImpl et " +
						"WHERE et.id = :eventTypeId", EventType.class)
					.setParameter("eventTypeId", eventTypeId)
					.getSingleResult();

			tx.commit();
			return eventType;
		} catch (Exception exception) {
			if (tx != null) {
				tx.rollback();
			}
			logErrorWhileGettingEventType(exception);
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
		try {
			tx = session.beginTransaction();
			Product product = session.createQuery("FROM ProductImpl pr " +
					"WHERE pr.orderNumber = :orderNumber", Product.class)
					.setParameter("orderNumber", productOrderNumber)
					.getSingleResult();

			tx.commit();
			return product;
		} catch (Exception exception) {
			if (tx != null) {
				tx.rollback();
			}
			logErrorWhileGettingProduct(exception);
			return null;
		} finally {
			session.close();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProductFamily getProductFamily(String name) {
		Session session = databaseSessionFactory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			ProductFamily productFamily = session.createQuery("FROM ProductFamilyImpl pf " +
					"WHERE pf.name = :productFamilyName", ProductFamily.class)
					.setParameter("productFamilyName", name)
					.getSingleResult();

			tx.commit();
			return productFamily;
		} catch (Exception exception) {
			if (tx != null) {
				tx.rollback();
			}
			logErrorWhileGettingProductFamily(exception);
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
		Session session = databaseSessionFactory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();

			for(ProductFamily productFamily : productFamilies) {
				session.saveOrUpdate(productFamily);
			}

			tx.commit();
		} catch (Exception exception) {
			if (tx != null) {
				tx.rollback();
			}
			logErrorWhileSavingProductFamilies(exception);
		} finally {
			session.close();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<EventType> listEvenTypes() {
		Session session = databaseSessionFactory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			List<EventType> eventTypes = session.createQuery("FROM EventTypeImpl et ",
						EventType.class)
					.list();

			tx.commit();
			return eventTypes;
		} catch (Exception exception) {
			if (tx != null) {
				tx.rollback();
			}
			logErrorWhileGettingEventTypes(exception);
			return new ArrayList<>();
		} finally {
			session.close();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void saveEventTypes(List<EventType> eventTypes) {
		Session session = databaseSessionFactory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();

			for(EventType eventType : eventTypes) {
				session.saveOrUpdate(eventType);
			}

			tx.commit();
		} catch (Exception exception) {
			if (tx != null) {
				tx.rollback();
			}
			logErrorWhileSavingEventTypes(exception);
		} finally {
			session.close();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void saveEvents(List<Event> events) {
		Session session = databaseSessionFactory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();

			for(Event event : events) {
				session.saveOrUpdate(event);
			}

			tx.commit();
		} catch (Exception exception) {
			if (tx != null) {
				tx.rollback();
			}
			logErrorWhileSavingEvents(exception);
		} finally {
			session.close();
		}
	}

	/**
	 * This methods logs exceptions to the console.
	 * @param head - a description what happened
	 * @param exception - the thrown exception
	 */
	protected void logError(String head, Throwable exception) {
		logger.error(head, exception);
	}

	/**
	 * This method logs exception which occurred while trying to connect to the database server.
	 * @param exception - the thrown exception
	 */
	protected void logErrorWhileConnectingToDatabaseServer(Throwable exception) {
		logError("can't connect to database server: ", exception);
	}

	/**
	 * This method logs an exception which occurs while trying to fetch product families from database server.
	 * @param exception - the thrown exception
	 */
	protected void logErrorWhileGettingListOfProductFamilies(Throwable exception) {
		logError("can't get list of product families from database", exception);
	}

	/**
	 * This method logs an exception which occurs while trying to fetch event types for a product from database server.
	 * @param exception - the thrown exception
	 */
	protected void logErrorWhileGettingEventTypesForProduct(Throwable exception) {
		logError("can't get event types for product from database", exception);
	}

	/**
	 * This method logs an exception which occurs while trying to fetch events for a product from database server.
	 * @param exception - the thrown exception
	 */
	protected void logErrorWhileGettingEventsForProduct(Throwable exception) {
		logError("can't get events for product from database", exception);
	}

	/**
	 * This method logs an exception which occurs while trying to fetch an event type from database server.
	 * @param exception - the thrown exception
	 */
	protected void logErrorWhileGettingEventType(Throwable exception) {
		logError("can't get event type from database", exception);
	}

	/**
	 * This method logs an exception which occurs while trying to fetch all event types from database server.
	 * @param exception - the thrown exception
	 */
	protected void logErrorWhileGettingEventTypes(Throwable exception) {
		logError("can't get event types from database", exception);
	}

	/**
	 * This method logs an exception which occurs while trying to fetch a product from database server.
	 * @param exception - the thrown exception
	 */
	protected void logErrorWhileGettingProduct(Throwable exception) {
		logError("can't get product from database", exception);
	}

	/**
	 * This method logs an exception which occurs while trying to fetch a product family from database server.
	 * @param exception - the thrown exception
	 */
	protected void logErrorWhileGettingProductFamily(Throwable exception) {
		logError("can't get product family from database", exception);
	}

	/**
	 * This method logs an exception which occurs while trying to save event types in database server.
	 * @param exception - the thrown exception
	 */
	protected void logErrorWhileSavingEventTypes(Throwable exception) {
		logError("can't save event types in database", exception);
	}

	/**
	 * This method logs an exception which occurs while trying to save product families in database server.
	 * @param exception - the thrown exception
	 */
	protected void logErrorWhileSavingProductFamilies(Throwable exception) {
		logError("can't save product families in database", exception);
	}

	/**
	 * This method logs an exception which occurs while trying to save events in database server.
	 * @param exception - the thrown exception
	 */
	protected void logErrorWhileSavingEvents(Throwable exception) {
		logError("can't save events in database", exception);
	}
}
