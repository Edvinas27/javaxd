package com.example.courseprifs.dbContext;

import com.example.courseprifs.constants.DbConstants;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class DbContext {
    private static EntityManager em;
    private static EntityManagerFactory emf;

    public static void connect() {
        emf = Persistence.createEntityManagerFactory(DbConstants.DB_NAME);
        em = emf.createEntityManager();
    }

    public static void disconnect() {
        if (em != null) {
            em.close();
        }

        if (emf != null) {
            emf.close();
        }
    }

    public static void beginTransaction() {
        em.getTransaction().begin();
    }

    public static void persist(Object entity) {
        em.persist(entity);
    }

    public static void commitTransaction() {
        em.getTransaction().commit();
    }

    public static void delete(Object entity) {
        em.remove(entity);
    }

    public static EntityManager getEntityManager() {
        return em;
    }
}
