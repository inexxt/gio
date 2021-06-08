package com.gio.calendar.database;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;

import static java.lang.System.exit;

/**
 * Singleton class keeping the entity manager for database persistance
 */
public class ConnectionManager {
    private static final String unitName = "Autocalendar"; // Has to match the name in the persistence.xml

    @PersistenceContext
    private static EntityManager entityManagerInstance = null;

    /**
     * Getter for the EntityManager, constructs it when called the first time
     * @return EntityManager instance
     */
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

    /**
     * Closing the connection to persistance layer
     */
    public static void close() {
        if (entityManagerInstance != null) {
            entityManagerInstance.close();
        }
    }

    /**
     * Getter for the unit name - the name of the persistance layer unit
     * @return unit name
     */
    public static String getUnitName() {
        return unitName;
    }
}