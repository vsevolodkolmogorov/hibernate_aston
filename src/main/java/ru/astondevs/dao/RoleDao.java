package ru.astondevs.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import ru.astondevs.entity.Role;
import ru.astondevs.util.ConnectionManager;

import java.util.List;

public class RoleDao {
    private final static RoleDao INSTANCE = new RoleDao();

    private RoleDao() {}

    public static RoleDao getInstance() {
        return INSTANCE;
    }

    public Role save(Role role) {
        Session session = ConnectionManager.openSession();
        Transaction transaction = session.getTransaction();
        try {
            transaction.begin();
            session.persist(role);
            transaction.commit();
            ConnectionManager.closeSession(session);
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        } finally {
            ConnectionManager.closeSession(session);
        }

        return role;
    }

    public void delete(Long id) {
        Session session = ConnectionManager.openSession();
        Transaction transaction = session.getTransaction();
        try {
            transaction.begin();

            Role role = session.find(Role.class, id);
            session.remove(role);

            transaction.commit();
            ConnectionManager.closeSession(session);
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        } finally {
            ConnectionManager.closeSession(session);
        }
    }

    public Role findById(Long id) {
        Session session = ConnectionManager.openSession();
        Transaction transaction = session.getTransaction();
        Role role;
        try {
            transaction.begin();

            Query<Role> query = session.createQuery("from Role r where r.id = :id", Role.class);
            query.setParameter("id", id);
            role = query.uniqueResult();

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        } finally {
            ConnectionManager.closeSession(session);
        }

        return role;
    }

    public List<Role> findAll() {
        Session session = ConnectionManager.openSession();
        Transaction transaction = session.getTransaction();
        List<Role> roleList;
        try {
            transaction.begin();

            roleList = session.createQuery("from Role", Role.class).list();

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        } finally {
            ConnectionManager.closeSession(session);
        }

        return roleList;
    }
}
