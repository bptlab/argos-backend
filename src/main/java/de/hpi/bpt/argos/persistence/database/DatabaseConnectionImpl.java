package de.hpi.bpt.argos.persistence.database;

import de.hpi.bpt.argos.persistence.model.event.*;
import de.hpi.bpt.argos.persistence.model.product.ProductFamilyImpl;
import de.hpi.bpt.argos.persistence.model.product.ProductImpl;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
}
