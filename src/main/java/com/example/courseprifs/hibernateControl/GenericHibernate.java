package com.example.courseprifs.hibernateControl;

import com.example.courseprifs.dbContext.DbContext;
import jakarta.persistence.criteria.CriteriaQuery;

import java.util.List;

public class GenericHibernate {

    public static <T> List<T> getAll(Class<T> klass) {
        List<T> entities = null;
        try {
            DbContext.connect();
            var em = DbContext.getEntityManager();

            CriteriaQuery<T> cq = em.getCriteriaBuilder().createQuery(klass);
            cq.select(cq.from(klass));

            entities = em.createQuery(cq).getResultList();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            DbContext.disconnect();
        }
        return entities;
    }

    public static <T> T getById(Class<T> klass, int id) {
        T entity = null;
        try {
            DbContext.connect();
            var em = DbContext.getEntityManager();
            entity = em.find(klass, id);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            DbContext.disconnect();
        }
        return entity;
    }

    public static <T> void create(T entity) {
        try {
            DbContext.connect();

            DbContext.beginTransaction();
            DbContext.persist(entity);
            DbContext.commitTransaction();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            DbContext.disconnect();
        }
    }

    public static <T> void delete(Class<T> klass, int id) {
        try {
            DbContext.connect();
            T entity = DbContext.getEntityManager().find(klass, id);
            if (entity != null) {
                DbContext.beginTransaction();
                DbContext.delete(entity);
                DbContext.commitTransaction();
            } else {
                System.err.println("Entity not found for deletion.");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            DbContext.disconnect();
        }
    }

    public static <T> void update(T entity) {
        try {
            DbContext.connect();

            DbContext.beginTransaction();
            var em = DbContext.getEntityManager();
            em.merge(entity);
            DbContext.commitTransaction();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            DbContext.disconnect();
        }
    }

}
