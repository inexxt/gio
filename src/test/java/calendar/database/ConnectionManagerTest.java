package calendar.database;

import com.gio.calendar.database.ConnectionManager;

import javax.persistence.EntityManager;

import static org.junit.Assert.assertEquals;

public class ConnectionManagerTest {
    @org.junit.Test
    public void EntityManagerIsSingleton() {
        EntityManager e1 = ConnectionManager.getEntityManager();
        EntityManager e2 = ConnectionManager.getEntityManager();
        assertEquals(e1, e2);
    }

    @org.junit.Test
    public void ConnectionManagerUnitName() {
        assertEquals(ConnectionManager.getUnitName(), "Autocalendar");
    }
}