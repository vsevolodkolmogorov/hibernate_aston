package ru.astondevs.dao;

import jakarta.persistence.PersistenceException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import ru.astondevs.entity.User;
import ru.astondevs.errors.UserNotFoundedException;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static ru.astondevs.util.ConnectionManager.executeInTransaction;

public class UserDao {
    private final static UserDao INSTANCE = new UserDao();
    private static final Logger log = Logger.getLogger(UserDao.class.getName());

    private UserDao() {}

    public static UserDao getInstance() {
        return INSTANCE;
    }

    public User save(User user) {
        try {
            User userNew = executeInTransaction(session -> {
                session.persist(user);
                return user;
            });
            log.info("Пользователь создан: " + userNew);
            return userNew;
        } catch (PersistenceException e) {
            log.log(Level.SEVERE, "Ошибка при сохранении пользователя: " + e.getMessage(), e);
            throw e;
        }
    }

    public void delete(Long id) {
        try {
            executeInTransaction(session -> {
                User user = session.find(User.class, id);
                if (user == null) {
                    log.warning("Пользователь с id " + id + " не найден для удаления");
                    throw new UserNotFoundedException("Пользователь с id " + id + " не найден для удаления");
                }
                session.remove(user);
                log.info("Пользователь удалён: " + user);
                return null;
            });
        } catch (PersistenceException e) {
            log.log(Level.SEVERE, "Ошибка при удалении пользователя: " + e.getMessage(), e);
            throw e;
        }
    }

    public List<User> findAll() {
        try {
            List<User> users = executeInTransaction(session -> {
                CriteriaBuilder cb = session.getCriteriaBuilder();
                CriteriaQuery<User> cq = cb.createQuery(User.class);
                Root<User> root = cq.from(User.class);
                cq.select(root);
                return session.createQuery(cq).getResultList();
            });
            log.info("Найдено пользователей: " + users.size());
            return users;
        } catch (PersistenceException e) {
            log.log(Level.SEVERE, "Ошибка при поиске всех пользователей: " + e.getMessage(), e);
            throw e;
        }
    }

    public User findById(Long id) {
        try {
            User user = executeInTransaction(session -> {
                return session.find(User.class, id);
            });

            if (user == null) {
                log.warning("Пользователь с id " + id + " не найден");
            } else {
                log.info("Пользователь найден: " + user);
            }

            return user;
        } catch (PersistenceException e) {
            log.log(Level.SEVERE, "Ошибка при поиске пользователя: " + e.getMessage(), e);
            throw e;
        }
    }

    public User update(Long id, User user) {
        try {
            User userUpdated = executeInTransaction(session -> {
                User userOld = session.find(User.class, id);
                if (userOld == null) {
                    log.warning("Пользователь с id " + id + " не найден для обновления");
                    throw new UserNotFoundedException("User with id " + id + " not found");
                }

                if (user.getName() != null) userOld.setName(user.getName());
                if (user.getEmail() != null) userOld.setEmail(user.getEmail());
                if (user.getRole() != null) userOld.setRole(user.getRole());
                if (user.getAge() != 0) userOld.setAge(user.getAge());

                log.info("Поля пользователя обновлены: " + userOld);
                return userOld;
            });
            log.info("Пользователь обновлён: " + userUpdated);
            return userUpdated;
        } catch (PersistenceException e) {
            log.log(Level.SEVERE, "Ошибка при обновлении пользователя: " + e.getMessage(), e);
            throw e;
        }
    }
}
