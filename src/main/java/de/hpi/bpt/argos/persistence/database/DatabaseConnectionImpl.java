package de.hpi.bpt.argos.persistence.database;

import de.hpi.bpt.argos.persistence.model.event.Event;
import de.hpi.bpt.argos.persistence.model.event.EventType;
import de.hpi.bpt.argos.persistence.model.product.Product;
import de.hpi.bpt.argos.persistence.model.product.ProductFamily;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.spi.ServiceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
		} catch (HibernateException exception) {
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
	public List<EventType> listAllEventTypesForProduct(Integer productId) {
		Session session = databaseSessionFactory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			List<Event> events = session.createQuery("FROM EventImpl e WHERE e.product.id = :productId",
					Event.class)
					.setParameter("productId", productId)
					.list();
			Set<EventType> eventTypes = new HashSet<>();
			for (Event event: events) {
				eventTypes.add(event.getEventType());
			}
			tx.commit();
			session.close();
			return new ArrayList<>(eventTypes);
		} catch (HibernateException exception) {
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
	public List<Event> listEventsForProductOfTypeInRange(Integer productId, Integer eventTypeId, Integer indexFrom, Integer
			indexTo) {
		Session session = databaseSessionFactory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			List<Event> events = session.createQuery("FROM EventImpl e WHERE e.product.id = :productId AND e" +
							".eventType.id = :eventTypeId",
					Event.class)
					.setParameter("productId", productId)
					.setParameter("eventTypeId", eventTypeId)
					.setFirstResult(indexFrom)
					.setMaxResults(indexTo)
					.list();
			tx.commit();
			session.close();
			return events;
		} catch (HibernateException exception) {
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
	 * This method logs an exception which occurs while trying to fetch product families from database server
	 * @param exception
	 */
	protected void logErrorWhileGettingListOfProductFamilies(Throwable exception) {
		logError("can't get list of product families from database", exception);
	}
}
