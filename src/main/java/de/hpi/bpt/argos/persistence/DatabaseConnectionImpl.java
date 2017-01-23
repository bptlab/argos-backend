package de.hpi.bpt.argos.persistence;

import de.hpi.bpt.argos.core.ArgosImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class DatabaseConnectionImpl implements DatabaseConnection {
    protected static final Logger logger = LoggerFactory.getLogger(ArgosImpl.class);

    protected static final String DEFAULT_DATABASE_SERVER = "localhost";
    protected static final int DEFAULT_DATABASE_SERVER_PORT = 3306;
    protected static final String DEFAULT_DATABASE_SERVER_USER = "root";
    protected static final String DEFAULT_DATABASE_SERVER_PASSWORD = "";

    protected String databaseServer;
    protected int databaseServerPort;
    protected String databaseServerUser;
    protected String databaseServerPassword;

    /**
     * This is the constructor.
     * @param databaseServer - the persistence server to connect to
     * @param databaseServerPort - the persistence server port to connect to
     * @param databaseServerUser - the persistence user to use
     * @param databaseServerPassword- the persistence password to use
     */
    public DatabaseConnectionImpl(String databaseServer, int databaseServerPort, String databaseServerUser,
                              String databaseServerPassword) {
        this.databaseServer = databaseServer;
        this.databaseServerPort = databaseServerPort;
        this.databaseServerUser = databaseServerUser;
        this.databaseServerPassword = databaseServerPassword;
    }

    /**
     * This is the constructor that set ups some default values.
     */
    public DatabaseConnectionImpl() {
        this(DEFAULT_DATABASE_SERVER, DEFAULT_DATABASE_SERVER_PORT,
                DEFAULT_DATABASE_SERVER_USER,
                DEFAULT_DATABASE_SERVER_PASSWORD);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDatabaseServer() {
        return databaseServer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDatabaseServer(String databaseServer) {
        this.databaseServer = databaseServer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getDatabaseServerPort() {
        return databaseServerPort;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDatabaseServerPort(int databaseServerPort) {
        this.databaseServerPort = databaseServerPort;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDatabaseServerUser() {
        return databaseServerUser;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDatabaseServerUser(String databaseServerUser) {
        this.databaseServerUser = databaseServerUser;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDatabaseServerPassword() {
        return databaseServerPassword;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDatabaseServerPassword(String databaseServerPassword) {
        this.databaseServerPassword = databaseServerPassword;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean createArgosDatabase() {
        Connection connection = null;
        boolean connectionSuccess = false;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + databaseServer + ":" +
                            databaseServerPort, databaseServerUser,
                    databaseServerPassword);
            Statement stmt = connection.createStatement();
            try {
                stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS argosbackend");
            } finally {
                stmt.close();
            }
        } catch (SQLException e) {
            logDatabaseConnectionError(e);
            return false;
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                    connectionSuccess = true;
                }
            } catch (SQLException e) {
                logDatabaseConnectionError(e);
                connectionSuccess = false;
            }
        }
        return connectionSuccess;
    }

    /**
     * This method logs errors on error level.
     * @param head - error message to be logged
     */
    protected void logError(String head, Throwable exception) {
        logger.error(head, exception);
    }

    /**
     * This method logs erros while connecting to the persistence server.
     * @param exception - exception to be logged
     */
    protected void logDatabaseConnectionError (SQLException exception) {
        logError("Can't connect to persistence: ", exception);
    }
}
