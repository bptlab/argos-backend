package de.hpi.bpt.argos.persistence;

/**
 * This interface represents a connection to the persistence server and handles persistence queries.
 */
public interface DatabaseConnection {

    /**
     * This method creates a persistence on the persistence server specified above.
     * @return - boolean based on the success of the persistence creation
     */
    boolean createArgosDatabase();

    /**
     * This method returns the persistence server host.
     * @return - returns the persistence server host as a string
     */
    String getDatabaseServer();

    /**
     * This method sets the persistence server host.
     * @param databaseServer - the persistence server host to connect to
     */
    void setDatabaseServer(String databaseServer);

    /**
     * This method returns the persistence server port.
     * @return - returns the persistence server port as an int
     */
    int getDatabaseServerPort();

    /**
     * This method sets the persistence server port.
     * @param databaseServerPort - the persistence server port to connect to
     */
    void setDatabaseServerPort(int databaseServerPort);

    /**
     * This method returns the persistence server user to use.
     * @return - returns the persistence server user to use as a string
     */
    String getDatabaseServerUser();

    /**
     * This method sets the persistence server user to use.
     * @param databaseServerUser - the persistence server user to use
     */
    void setDatabaseServerUser(String databaseServerUser);

    /**
     * This method returns the persistence server password to use.
     * @return - returns the persistence server password to use as a string
     */
    String getDatabaseServerPassword();

    /**
     * This method sets the persistence server password to use.
     * @param databaseServerPassword - the databasee server password to use
     */
    void setDatabaseServerPassword(String databaseServerPassword);
}
