package de.hpi.bpt.argos.storage;

import de.hpi.bpt.argos.properties.PropertyEditorImpl;
import de.hpi.bpt.argos.storage.dataModel.PersistenceArtifact;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.concurrent.Callable;

/**
 * This interface represents the class to access the database (read/write).
 */
public interface DatabaseAccess {

	String DATABASE_CONNECTION_HOST_PROPERTY_KEY = "databaseConnectionHost";
	String DATABASE_CONNECTION_USERNAME_PROPERTY_KEY = "databaseConnectionUsername";
	String DATABASE_CONNECTION_PASSWORD_PROPERTY_KEY = "databaseConnectionPassword";

	/**
	 * This method tries to establish a connection to the database.
	 * @return - true, if the connection was established
	 */
	boolean establishConnection();

	/**
	 * This method returns the sessionFactory for the database.
	 * @return - the sessionFactory for the database
	 */
	SessionFactory getSessionFactory();

	/**
	 * This method stores/updates a list of persistenceArtifacts in the database.
	 * @param artifacts - the persistenceArtifacts to store
	 * @return - true, if the artifacts were stored completely
	 */
	boolean saveArtifacts(PersistenceArtifact... artifacts);

	/**
	 * This method deletes a list of persistenceArtifacts in the database.
	 * @param artifacts - the persistenceArtifacts to delete
	 * @return - true, if the artifacts were stored completely
	 */
	boolean deleteArtifacts(PersistenceArtifact... artifacts);

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
	<R, Q> R getArtifacts(Session session, Query<Q> query, Transaction transaction, Callable<R> getValue, R defaultValue);

	/**
	 * This method reads the connectionHost property from the properties-file and returns it's value.
	 * @return - the connectionHost, specified in the properties-file
	 */
	static String getConnectionHost() {
		return PropertyEditorImpl.getInstance().getProperty(DATABASE_CONNECTION_HOST_PROPERTY_KEY);
	}

	/**
	 * This method reads the connectionUsername property from the properties-file and returns it's value.
	 * @return - the connectionUsername, specified in the properties-file
	 */
	static String getConnectionUsername() {
		return PropertyEditorImpl.getInstance().getProperty(DATABASE_CONNECTION_USERNAME_PROPERTY_KEY);
	}

	/**
	 * This method reads the connectionPassword property from the properties-file and returns it's value.
	 * @return - the connectionPassword, specified in the properties-file
	 */
	static String getConnectionPassword() {
		return PropertyEditorImpl.getInstance().getProperty(DATABASE_CONNECTION_PASSWORD_PROPERTY_KEY);
	}
}
