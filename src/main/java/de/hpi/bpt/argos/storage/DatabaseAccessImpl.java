package de.hpi.bpt.argos.storage;

import de.hpi.bpt.argos.core.Argos;
import de.hpi.bpt.argos.storage.dataModel.PersistenceArtifact;
import de.hpi.bpt.argos.util.LoggerUtilImpl;
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
		try {
			Configuration configuration = new Configuration();
			configuration.configure();

			sessionBatchSize = Integer.parseInt(configuration.getProperty("hibernate.jdbc.batch_size"));

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

			sessionFactory = configuration.buildSessionFactory();
		} catch (ServiceException e) {
			LoggerUtilImpl.getInstance().error(logger, "cannot connect to database server", e);
			return false;
		}

		return true;
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
	public <R, Q> R getArtifacts(Session session, Query<Q> query, Transaction transaction, Callable<R> getValue, R defaultValue) {
		try {
			R result = getValue.call();
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
	 * {@inheritDoc}
	 */
	@Override
	public <ResultType, ResultImplType extends ResultType> List<ResultType> getArtifactsById(Session session,
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
}
