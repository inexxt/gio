package com.gio.calendar.utilities.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;

public class ConnectionManager
{
    private static final String url = "jdbc:sqlite:calendar_sqlite.db";
    private static final String dbDriver = "org.sqlite.JDBC";

    /* Singleton class managing the connection
     */
    public static class ConnectionManagerInstance {
        private Connection conn;

        public Connection getConn() {
            return conn;
        }

        /*private void dropEventTable() throws SQLException, ClassNotFoundException {
            String sql = "DROP TABLE IF EXISTS events;";
            executeStatement(sql);
        }

        private void dropTaskTable() throws SQLException, ClassNotFoundException {
            String sql = "DROP TABLE IF EXISTS tasks;";
            executeStatement(sql);
        }

        private void dropTaskTagsTable() throws SQLException, ClassNotFoundException {
            String sql = "DROP TABLE IF EXISTS task_tags;";
            executeStatement(sql);
        }

        private void dropEventTagsTable() throws SQLException, ClassNotFoundException {
            String sql = "DROP TABLE IF EXISTS event_tags;";
            executeStatement(sql);
        }*/

        private void createEventTable() throws ClassNotFoundException, SQLException {
            String sql = "CREATE TABLE IF NOT EXISTS events (\n"
                    + "	id integer PRIMARY KEY AUTOINCREMENT,\n"
                    + "	name text NOT NULL,\n"
                    + "	desc text NOT NULL,\n"
                    + "	event_start integer NOT NULL,\n"
                    + "	event_end integer NOT NULL\n"
                    + ");";
            executeStatement(sql);
        }

        private void createTaskTable() throws ClassNotFoundException, SQLException {
            String sql = "CREATE TABLE IF NOT EXISTS tasks (\n"
                    + "	id integer PRIMARY KEY AUTOINCREMENT,\n"
                    + "	name text NOT NULL,\n"
                    + "	desc text NOT NULL,\n"
                    + "	task_date integer NOT NULL,\n"
                    + " task_duration integer NOT NULL\n"
                    + ");";
            executeStatement(sql);
        }

        private void createTaskTagsTable() throws ClassNotFoundException, SQLException {
            String sql = "CREATE TABLE IF NOT EXISTS task_tags (\n"
                    + "	id integer PRIMARY KEY AUTOINCREMENT,\n"
                    + " task integer,\n"
                    + "	tag text NOT NULL\n,"
                    + " FOREIGN KEY(task) REFERENCES tasks(id)\n"
                    + ");";
            executeStatement(sql);
        }

        private void createEventTagsTable() throws ClassNotFoundException, SQLException {
            String sql = "CREATE TABLE IF NOT EXISTS event_tags (\n"
                    + "	id integer PRIMARY KEY AUTOINCREMENT,\n"
                    + " event integer,\n"
                    + "	tag text NOT NULL\n,"
                    + " FOREIGN KEY(event) REFERENCES event(id)\n"
                    + ");";
            executeStatement(sql);
        }


        public void initConnection() throws SQLException, ClassNotFoundException, IOException {
            if (conn == null) {
                Class.forName(dbDriver);
                this.conn = DriverManager.getConnection(url);
                createEventTable();
                createTaskTable();
                createTaskTagsTable();
                createEventTagsTable();
            }
        }

        /*
        public ConnectionManagerInstance() {
        }*/

        public void executeStatement(String sql) throws SQLException {
            Statement stmt = getConn().createStatement();
            stmt.execute(sql);
        }

        /*public ResultSet query(String sql) throws SQLException {
            Statement stmt = getConn().createStatement();
            return stmt.executeQuery(sql);
        }

        public void closeConnection() {
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (SQLException e) {
            	/* Connection close failed.

                System.err.println(e.getMessage());
            }
        }*/
    }

    private static ConnectionManagerInstance instance = null;
    public static ConnectionManagerInstance getConnectionManager() throws SQLException, ClassNotFoundException {
        if (instance == null) {
            instance = new ConnectionManagerInstance();
        }
        return instance;
    }

    public static void initialize() throws SQLException, ClassNotFoundException, IOException {
        getConnectionManager().initConnection();
    }

    public static Connection getConnection() throws SQLException, IOException, ClassNotFoundException {
        return getConnectionManager().getConn();
    }
}

/**
 * Miscellaneous SQL connection facilities provider
 */
//public class ConnectionManager {
//    /** Returns new Connection based on properties stored in database.properties file
//     * @return new Connection object allowing connection to the database
//     */
//    public static Connection getNewConnection() throws SQLException, IOException {
//        Properties databaseProperties = new Properties();
//
//        /* Read properties from file */
//        try(InputStream in = Files.newInputStream(Paths.get("database.properties"))) {
//            databaseProperties.load(in);
//        }
//        catch (IOException e) {
//            throw new IOException("Database properties not found" + Paths.get())
//        }
//
//        String drivers = databaseProperties.getProperty("jdbc.drivers");
//
//        if(drivers != null)
//            System.setProperty("jdbc.drivers", drivers);
//
//        /* Extract relevant properties */
//        String url = databaseProperties.getProperty("jdbc.url");
//        String username = databaseProperties.getProperty("jdbc.username");
//        String password = databaseProperties.getProperty("jdbc.password");
//
//        /* Return connection created by DriverManager according to extracted
//         * properties
//         */
//
//        return DriverManager.getConnection(url, username, password);
//    }
//}