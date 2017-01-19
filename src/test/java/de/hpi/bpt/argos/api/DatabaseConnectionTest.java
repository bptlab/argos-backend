package de.hpi.bpt.argos.api;

import de.hpi.bpt.argos.database.DatabaseConnectionImpl;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseConnectionTest {
    private static DatabaseConnectionImpl databaseService;

    @BeforeClass
    public static void setUp() {
        databaseService = new DatabaseConnectionImpl();
    }

    @Test
    public void testCreateArgosDatabase() throws SQLException {
        databaseService.createArgosDatabase();
        Connection con = DriverManager.getConnection ("jdbc:mysql://localhost:3306", "root", "");
        ResultSet rs = con.getMetaData().getCatalogs();
        boolean found_database = false;
        while (rs.next()) {
            if (rs.getString("TABLE_CAT").equals(new String("argosbackend"))) {
                found_database = true;
            }
        }
        assertEquals(true, found_database);
    }
}
