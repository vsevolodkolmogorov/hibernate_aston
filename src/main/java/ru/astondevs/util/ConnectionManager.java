package ru.astondevs.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import ru.astondevs.entity.Role;
import ru.astondevs.entity.User;

import java.io.InputStream;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;

public class ConnectionManager {

    private static SessionFactory sessionFactory;

    public static void init() {
        try (InputStream inputStream = ConnectionManager.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) {

            if (inputStream == null) {
                throw new RuntimeException("config.properties not found in classpath");
            }

            Properties properties = new Properties();
            properties.load(inputStream);

            init(properties);

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize ConnectionManager", e);
        }
    }

    public static void init(Properties properties) {
        Configuration configuration = new Configuration();
        configuration.setProperties(properties);
        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(Role.class);
        sessionFactory = configuration.buildSessionFactory();
    }

    private ConnectionManager() {
    }

    public static Session openSession() {
        if (sessionFactory == null) throw new IllegalStateException("SessionFactory not initialized");
        return sessionFactory.openSession();
    }

    public static void closeSession(Session session) {
        if (session != null && session.isOpen()) {
            session.close();
        }
    }

    public static <T> T executeInTransaction(Function<Session, T> action) {
        try (Session session = ConnectionManager.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                T result = action.apply(session);
                transaction.commit();
                return result;
            } catch (Exception e) {
                transaction.rollback();
                throw e;
            }
        }
    }

    public static void executeInTransaction(Consumer<Session> action) {
        try (Session session = ConnectionManager.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                action.accept(session);
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
                throw e;
            }
        }
    }
}
