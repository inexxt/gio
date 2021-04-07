package calendar.utilities.database;

import com.gio.calendar.utilities.database.ConnectionManager;


import java.sql.*;

import static org.junit.Assert.assertTrue;

public class ConnectionManagerTest {
    @org.junit.Test
    public void tablesCreationTest() {
        try {
            ConnectionManager.initialize();
            Connection conn = ConnectionManager.getConnection();
            String tables [] = {"tasks", "events", "task_tags", "event_tags", "event_people"};
            for (String t : tables) {
                String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='"+t+"';";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery();
                assertTrue(rs.next());
            }
        }
        catch (Exception e){
            assertTrue(false);
        }
    }
}