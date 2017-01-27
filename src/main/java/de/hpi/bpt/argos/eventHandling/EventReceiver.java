package de.hpi.bpt.argos.eventHandling;

import de.hpi.bpt.argos.common.RestEndpoint;
import de.hpi.bpt.argos.persistence.database.DatabaseConnection;
import spark.Request;
import spark.Response;

/**
 * This interface represents an event receiver that is called when an event is sent from the event processing platform.
 * It extends the RestEndpoint.
 */
public interface EventReceiver extends RestEndpoint {
    /**
     * This method is responsible for receiving events by reacting to the spark request sent from the event
     * processing platform.
     * @param request - spark request to be used
     * @param response - spark request to be used
     * @return - returns the message body for testing purposes
     * //TODO: update javadoc (return)
     */
	String receiveEvent(Request request, Response response);

	/**
	 * This method sets the database connection for this event receiver.
	 * @param databaseConnection - the database connection to be set
	 */
	void setDatabaseConnection(DatabaseConnection databaseConnection);
}
