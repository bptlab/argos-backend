package de.hpi.bpt.argos.persistence.database;

import de.hpi.bpt.argos.persistence.model.event.*;

import de.hpi.bpt.argos.persistence.model.product.Product;
import de.hpi.bpt.argos.persistence.model.product.ProductFamily;
import de.hpi.bpt.argos.persistence.model.product.ProductFamilyImpl;
import de.hpi.bpt.argos.persistence.model.product.ProductImpl;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.hibernate.service.spi.ServiceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

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
					.addAnnotatedClass(EventDataImpl.class)
					.addAnnotatedClass(EventSubscriptionQueryImpl.class)
					.addAnnotatedClass(EventAttributeImpl.class)
					.addAnnotatedClass(EventTypeImpl.class)
					.addAnnotatedClass(ProductImpl.class)
					.addAnnotatedClass(ProductFamilyImpl.class)
					.addAnnotatedClass(UpdateProductStateEventImpl.class)
					.addAnnotatedClass(EventImpl.class)
					.configure()
					.buildSessionFactory();
		} catch (ServiceException e) {
			logErrorWhileConnectingToDatabaseServer(e);
			return false;
		}

		return true;
	}

	@Override
	public void addProduct(Product product) {
		Session session = databaseSessionFactory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			session.save(product);
			tx.commit();
		}catch (HibernateException e) {
			if (tx != null) {
				tx.rollback();
			}
			logError("can't add product to database", e);
		}finally {
			session.close();
		}
	}

	@Override
	public List<ProductFamily> listAllProductFamilies() {
		Session session = databaseSessionFactory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			List<ProductFamily> productFamilies = session.createQuery("FROM de.hpi.bpt.argos.persistence.model.product" +
					".ProductFamily", ProductFamily.class)
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

	@Override
	public List<EventType> listAllEventTypesForProductFamily(String productFamilyId) {
		Session session = databaseSessionFactory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			Query query = session.createQuery("FROM de.hpi.bpt.argos.persistence.model.event.Event e " +
					"WHERE e.product =3", Event.class);
			//query.setParameter("product_Id", productFamilyId);
			List<Event> events = query.list();
			for (Event event: events) {
				System.out.println(event.getEventType());
			}
			tx.commit();
			session.close();
			return new ArrayList<>();
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
