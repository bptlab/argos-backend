package de.hpi.bpt.argos.storage;

import de.hpi.bpt.argos.core.Argos;
import de.hpi.bpt.argos.storage.dataModel.PersistenceArtifact;
import de.hpi.bpt.argos.util.LoggerUtilImpl;
import de.hpi.bpt.argos.util.ObjectWrapper;
import de.hpi.bpt.argos.util.performance.WatchImpl;
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
import java.util.List;
import java.util.concurrent.Callable;


/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class DatabaseAccessImpl implements DatabaseAccess {
	private static final Logger logger = LoggerFactory.getLogger(DatabaseAccessImpl.class);

	private SessionFactory sessionFactory;
	private int sessionBatchSize;

	/**
	 * This constructor initializes all members with its default value.
	 */
	public DatabaseAccessImpl() {
		sessionBatchSize = 1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean establishConnection() {

		Configuration configuration = configureDatabaseConnection();
		sessionBatchSize = Integer.parseInt(configuration.getProperty("hibernate.jdbc.batch_size"));

		if (Argos.shouldWaitForDatabase()) {
			boolean connected = tryConnect(configuration);
			final long retryTime = 5000;
			long startTime = System.currentTimeMillis();

			while (!connected) {
				logger.info(String.format("waiting for database to be up ... (%1$d ms)", System.currentTimeMillis() - startTime));

				try {
					Thread.sleep(retryTime);
				} catch (InterruptedException e) {
					LoggerUtilImpl.getInstance().error(logger, "thread sleep was interrupted", e);
					Thread.currentThread().interrupt();
				}

				connected = tryConnect(configuration);
			}

			return true;
		} else {
			return tryConnect(configuration);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean saveArtifacts(PersistenceArtifact... artifacts) {
		Transaction transaction = null;
		try (Session session = sessionFactory.openSession()) {
			transaction = session.beginTransaction();
			int batchCount = 0;

			for (PersistenceArtifact artifact : artifacts) {
				session.saveOrUpdate(artifact);

				if (++batchCount % sessionBatchSize == 0) {

					logger.debug(String.format("saving artifacts... %1$d / %2$d", batchCount, artifacts.length));

					session.flush();
					session.clear();
				}
			}

			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}

			LoggerUtilImpl.getInstance().error(logger, "cannot save/update artifacts in database", e);
			return false;
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean deleteArtifacts(PersistenceArtifact... artifacts) {
		Transaction transaction = null;
		try (Session session = sessionFactory.openSession()) {
			transaction = session.beginTransaction();

			for (PersistenceArtifact artifact : artifacts) {
				session.delete(artifact);
			}

			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}

			LoggerUtilImpl.getInstance().error(logger, "cannot delete artifacts in database", e);
			return false;
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <ResultType, QueryType> ResultType getArtifacts(Session session,
														   Query<QueryType> query,
														   Transaction transaction,
														   Callable<ResultType> getValue,
														   ResultType defaultValue) {

		ObjectWrapper<ResultType> result = new ObjectWrapper<>();
		WatchImpl.measure("get artifacts",
				() -> result.set(executeGetArtifacts(session, query, transaction, getValue, defaultValue)));

		return result.get();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <ResultType, ResultImplType extends ResultType> List<ResultType> getArtifactsById(Session session,
																							 Class<ResultImplType> resultTypeClass,
																							 Long... ids) {

		ObjectWrapper<List<ResultType>> result = new ObjectWrapper<>();
		WatchImpl.measure("get artifacts by id",
				() -> result.set(executeGetArtifactsById(session, resultTypeClass, ids)));
		return result.get();
	}

	/**
	 * This method actually executes the getArtifact-method.
	 * @param session - the database session, which must be open
	 * @param query - the query to execute and to retrieve the results from
	 * @param transaction - the current transaction
	 * @param getValue - the function to get the results from the query
	 * @param defaultValue - a fall back default value in case anything went wrong or no entities were found
	 * @param <ResultType> - the result type
	 * @param <QueryType> - the query type
	 * @return - an object of the result value type
	 */
	private <ResultType, QueryType> ResultType executeGetArtifacts(Session session,
																   Query<QueryType> query,
																   Transaction transaction,
																   Callable<ResultType> getValue,
																   ResultType defaultValue) {
		try {
			ResultType result = getValue.call();
			transaction.commit();
			return result;
		} catch (NoResultException e) {
			LoggerUtilImpl.getInstance().info(logger, String.format("no entities found. query: '%1$s'", query.getQueryString()), e);
			return defaultValue;
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			LoggerUtilImpl.getInstance().error(logger,
					String.format("cannot retrieve artifacts from database. query: '%1$s'", query.getQueryString()), e);
			return defaultValue;
		} finally {
			session.close();
		}
	}

	/**
	 * This method actually executes the getArtifactsById-method.
	 * @param <ResultType> - the result type
	 * @param <ResultImplType> - the implementation type of the result type
	 * @param session - the database session, which must be open
	 * @param resultTypeClass - the class of the result type
	 * @param ids - the ids to fetch from the database
	 * @return - a list of all entities, which were contained in the ids
	 */
	private <ResultType, ResultImplType extends ResultType> List<ResultType> executeGetArtifactsById(Session session,
																							 Class<ResultImplType> resultTypeClass,
																							 Long... ids) {
		try {
			return new ArrayList<>(session.byMultipleIds(resultTypeClass).enableSessionCheck(true).multiLoad(ids));
		} catch (NoResultException e) {
			LoggerUtilImpl.getInstance().info(logger, "no entities for given ids found", e);
			return new ArrayList<>();
		} catch (Exception e) {
			LoggerUtilImpl.getInstance().error(logger, "cannot retrieve artifacts by id from database", e);
			return new ArrayList<>();
		} finally {
			session.close();
		}
	}

	/**
	 * This method configures the database connector.
	 * @return - the configuration for the database connector
	 */
	private Configuration configureDatabaseConnection() {
		Configuration configuration = new Configuration();
		configuration.configure();

		configuration.setProperty("hibernate.connection.username", DatabaseAccess.getConnectionUsername());
		configuration.setProperty("hibernate.connection.password", DatabaseAccess.getConnectionPassword());

		if (Argos.isInTestMode()) {
			configuration.setProperty("hibernate.connection.url",
					String.format("jdbc:mysql://%1$s/argosbackend_test?createDatabaseIfNotExist=true", DatabaseAccess.getConnectionHost()));

			// drop existing schema and re-create
			configuration.setProperty("hibernate.hbm2ddl.auto", "create");
		} else {
			configuration.setProperty("hibernate.connection.url",
					String.format("jdbc:mysql://%1$s/argosbackend?createDatabaseIfNotExist=true", DatabaseAccess.getConnectionHost()));

			configuration.setProperty("hibernate.hbm2ddl.auto", "update");
		}

		return configuration;
	}

	/**
	 * This method tries to connect to the database service.
	 * @param configuration - the configuration to use for the connection
	 * @return - the status of the connection process
	 */
	private boolean tryConnect(Configuration configuration) {
		try {
			sessionFactory = configuration.buildSessionFactory();
		} catch (ServiceException e) {
			LoggerUtilImpl.getInstance().error(logger, "cannot connect to database server", e);
			return false;
		}

		return true;
	}
}
