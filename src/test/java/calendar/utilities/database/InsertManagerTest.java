package calendar.utilities.database;

import com.gio.calendar.utilities.calendar.tag.Tag;
import com.gio.calendar.utilities.database.ConnectionManager;
import com.gio.calendar.utilities.database.InsertManager;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class InsertManagerTest {

    @Before
    public void setUp() {
        try {
            ConnectionManager.initialize();
        }
        catch (Exception e) {
            fail();
        }
    }

        @org.junit.Test
        public void addTaskandTagsTest() {
            LocalDate date = LocalDate.now();
            String name = "pierwsze";
            String desc = "desc";
            String duration = "1";
            ArrayList<Integer> keys = new ArrayList<Integer>();
            try {
                ResultSet rs = InsertManager.addTask(date, name, desc, duration);

                boolean executed = false;
                while (rs.next()) {
                    executed = true;
                    String sql = "select name, desc, task_duration from tasks where id = ?;";
                    PreparedStatement pstmt = ConnectionManager.getConnectionManager().getConn().prepareStatement(sql);

                    pstmt.setInt(1, rs.getInt(1));
                    keys.add(rs.getInt(1));
                    ResultSet rs1 = pstmt.executeQuery();
                    assertEquals(name, rs1.getString("name"));
                    assertEquals(desc, rs1.getString("desc"));
                    assertEquals(1, rs1.getInt("task_duration"));
                }
                assertTrue(executed);
                assertEquals(keys.size(), 1);
            } catch (Exception e) {
                fail();
            }

            try {
                List<Tag> tagList = new ArrayList<Tag>();

                tagList.add(new Tag("a"));
                String sql = "select id from tasks where id = ?;";
                PreparedStatement pstmt = ConnectionManager.getConnectionManager().getConn().prepareStatement(sql);

                pstmt.setInt(1, keys.get(0));
                System.out.println(pstmt);
                ResultSet res = pstmt.executeQuery();

                List<String> ids = new ArrayList<>();
                while (res.next()) {
                    ids.add(res.getString(1));
                }

                InsertManager.addTags("task", ids, tagList);
                boolean executed = false;
                for (int x : keys) {
                    executed = true;
                    String sql_tag = "select tag from task_tags where task = ?;";
                    PreparedStatement pstmt_tag = ConnectionManager.getConnectionManager().getConn().prepareStatement(sql_tag);

                    pstmt_tag.setInt(1, x);
                    ResultSet rs1 = pstmt_tag.executeQuery();
                    assertEquals("a", rs1.getString("tag"));

                }
                assertTrue(executed);
            } catch (Exception e) {
                fail();
            }
        }

        @org.junit.Test
        public void addEventTest() {
            LocalDate eventDate = LocalDate.now();
            LocalTime eventStartTime = LocalTime.of(15, 0);
            LocalTime eventEndTime = LocalTime.of(16, 0);
            String name = "name";
            String desc = "desc";
            String place = "Warsaw";
            try {
                ResultSet rs = InsertManager.addEvent(eventDate, eventStartTime, eventEndTime, name, desc, place);
                boolean executed = false;
                while (rs.next()) {
                    executed = true;
                    String sql = "select name, desc, place from events where id = ?;";
                    PreparedStatement pstmt = ConnectionManager.getConnectionManager().getConn().prepareStatement(sql);

                    pstmt.setInt(1, rs.getInt(1));
                    ResultSet rs1 = pstmt.executeQuery();
                    assertEquals(name, rs1.getString("name"));
                    assertEquals(desc, rs1.getString("desc"));
                    assertEquals(place, rs1.getString("place"));
                }
                assertTrue(executed);
            }
            catch (Exception e) {
                fail();
            }
        }

}
