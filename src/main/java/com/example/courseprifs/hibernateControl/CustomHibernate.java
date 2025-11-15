package com.example.courseprifs.hibernateControl;

import com.example.courseprifs.dbContext.DbContext;
import com.example.courseprifs.model.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;

public class CustomHibernate  {
    public static User getUserByCredentials(String login, String psw) {
        User user = null;
        try {
            DbContext.connect();
            EntityManager em = DbContext.getEntityManager();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<User> query = cb.createQuery(User.class);
            Root<User> root = query.from(User.class);

            query.select(root).where(cb.and(
                    cb.equal(root.get("login"), login),
                    cb.equal(root.get("password"), psw)));
            Query q = em.createQuery(query);
            user = (User) q.getSingleResult();
        } catch (Exception e) {
            // alert
        } finally {
            DbContext.disconnect();
        }
        return user;
    }

    public static List<FoodOrder> getRestaurantOrders(Restaurant restaurant) {
        List<FoodOrder> orders = new ArrayList<>();
        try {
            DbContext.connect();
            EntityManager em = DbContext.getEntityManager();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<FoodOrder> query = cb.createQuery(FoodOrder.class);
            Root<FoodOrder> root = query.from(FoodOrder.class);

            query.select(root).where(cb.equal(root.get("restaurant"), restaurant));
            Query q = em.createQuery(query);
            orders = q.getResultList();
        } catch (Exception e) {
            // alert
        } finally {
            DbContext.disconnect();
        }
        return orders;
    }

    public static List<Cuisine> getRestaurantCuisine(Restaurant restaurant) {
        List<Cuisine> menu = new ArrayList<>();
        try {
            DbContext.connect();
            EntityManager entityManager = DbContext.getEntityManager();
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Cuisine> query = cb.createQuery(Cuisine.class);
            Root<Cuisine> root = query.from(Cuisine.class);

            query.select(root).where(cb.equal(root.get("restaurant"), restaurant));
            Query q = entityManager.createQuery(query);
            menu = q.getResultList();
        } catch (Exception e) {
            // alert
        } finally {
            DbContext.disconnect();
        }
        return menu;
    }

    public static List<FoodOrder> getOrdersByStatus(OrderStatus orderStatus) {
        List<FoodOrder> orders = new ArrayList<>();
        try {
            DbContext.connect();
            EntityManager em = DbContext.getEntityManager();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<FoodOrder> query = cb.createQuery(FoodOrder.class);
            Root<FoodOrder> root = query.from(FoodOrder.class);

            query.select(root).where(cb.equal(root.get("orderStatus"), orderStatus));
            Query q = em.createQuery(query);
            orders = q.getResultList();
        } catch (Exception e) {
            // alert
        } finally {
            DbContext.disconnect();
        }
        return orders;
    }
}
