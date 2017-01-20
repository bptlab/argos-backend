package de.hpi.bpt.argos.database;

import de.hpi.bpt.argos.database.DatabaseConnectionImpl;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseConnectionTest {
    private static final String JDBC_PREFIX = "jdbc:mysql://";
    private static final String TEST_HOST = "localhost";
    private static final int TEST_PORT = 3306;
    private static final String TEST_USER = "root";
    private static final String TEST_PASSWORD = "";
    private static final String ARGOS_SCHEMA_NAME = "argosbackend";

    private static DatabaseConnectionImpl databaseService;

    @BeforeClass
    public static void setUp() {
        databaseService = new DatabaseConnectionImpl(TEST_HOST, TEST_PORT, TEST_USER, TEST_PASSWORD);
    }

    @Test
    public void testCreateArgosDatabase() throws SQLException {
        databaseService.createArgosDatabase();
        Connection con = DriverManager.getConnection (String.format("%1$s%2$s:%3$d", JDBC_PREFIX, TEST_HOST, TEST_PORT), TEST_USER, TEST_PASSWORD);
        ResultSet result = con.getMetaData().getCatalogs();
        boolean databaseFound = false;
        while (result.next()) {
            if (result.getString("TABLE_CAT").equals(ARGOS_SCHEMA_NAME)) {
                databaseFound = true;
            }
        }
        assertEquals(true, databaseFound);
    }
}
