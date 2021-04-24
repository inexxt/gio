package com.gio.calendar.database;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;

import static java.lang.System.exit;

/* Singleton class managing the connection */
public class ConnectionManager
{
    private static final String unitName = "Autocalendar"; // Has to match the name in the persistence.xml

    @PersistenceContext
    private static EntityManager entityManagerInstance = null;

    public static EntityManager getEntityManager() {
        if (entityManagerInstance == null) {
            EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(unitName);
            entityManagerInstance = entityManagerFactory.createEntityManager();
            if (!entityManagerInstance.isOpen()) {
                System.out.println("Error - entityManagerInstance is closed");
                exit(1);
            }
        }
        return entityManagerInstance;
    }

    public static void close() {
        if (entityManagerInstance != null) {
            entityManagerInstance.close();
        }
    }

    public static String getUnitName() {
        return unitName;
    }
}