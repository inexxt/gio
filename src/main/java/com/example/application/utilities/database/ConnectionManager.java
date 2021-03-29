package com.example.application.utilities.database;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Miscellaneous SQL connection facilities provider
 */
public class ConnectionManager {
    /** Returns new Connection based on properties stored in database.properties file
     * @return new Connection object allowing connection to the database
     */
    public static Connection getNewConnection() throws SQLException, IOException {
        Properties databaseProperties = new Properties();

        /* Read properties from file */
        try(InputStream in = Files.newInputStream(Paths.get("database.properties"))) {
            databaseProperties.load(in);
        }
        catch(IOException e) {
            throw e;
        }

        String drivers = databaseProperties.getProperty("jdbc.drivers");

        if(drivers != null)
            System.setProperty("jdbc.drivers", drivers);

        /* Extract relevant properties */
        String url = databaseProperties.getProperty("jdbc.url");
        String username = databaseProperties.getProperty("jdbc.username");
        String password = databaseProperties.getProperty("jdbc.password");

        /* Return connection created by DriverManager according to extracted
         * properties
         */

        return DriverManager.getConnection(url, username, password);
    }
}
