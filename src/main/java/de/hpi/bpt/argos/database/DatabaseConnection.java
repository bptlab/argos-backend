package de.hpi.bpt.argos.database;

/**
 * This interface represents a connection to the database server and handles database queries.
 */
public interface DatabaseConnection {

    /**
     * This method creates a database on the database server specified above.
     * @return - boolean based on the success of the database creation
     */
    boolean createArgosDatabase();

    /**
     * This method returns the database server host.
     * @return - returns the database server host as a string
     */
    String getDatabaseServer();

    /**
     * This method sets the database server host.
     * @param databaseServer - the database server host to connect to
     */
    void setDatabaseServer(String databaseServer);

    /**
     * This method returns the database server port.
     * @return - returns the database server port as an int
     */
    int getDatabaseServerPort();

    /**
     * This method sets the database server port.
     * @param databaseServerPort - the database server port to connect to
     */
    void setDatabaseServerPort(int databaseServerPort);

    /**
     * This method returns the database server user to use.
     * @return - returns the database server user to use as a string
     */
    String getDatabaseServerUser();

    /**
     * This method sets the database server user to use.
     * @param databaseServerUser - the database server user to use
     */
    void setDatabaseServerUser(String databaseServerUser);

    /**
     * This method returns the database server password to use.
     * @return - returns the database server password to use as a string
     */
    String getDatabaseServerPassword();

    /**
     * This method sets the database server password to use.
     * @param databaseServerPassword - the databasee server password to use
     */
    void setDatabaseServerPassword(String databaseServerPassword);
}
