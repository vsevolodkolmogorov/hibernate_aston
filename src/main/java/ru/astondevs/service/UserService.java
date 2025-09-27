package ru.astondevs.service;

import ru.astondevs.dao.RoleDao;
import ru.astondevs.dao.UserDao;
import ru.astondevs.dto.UserDto;
import ru.astondevs.entity.Role;
import ru.astondevs.entity.User;
import ru.astondevs.errors.EmptyFieldException;
import ru.astondevs.util.UserMapper;

import java.lang.reflect.Field;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserService {
    private final RoleDao roleDao = RoleDao.getInstance();
    private final UserDao userDao = UserDao.getInstance();
    private static final Logger log = Logger.getLogger(UserService.class.getName());

    public UserDto save(UserDto user) {
        try {
            log.info("Сохраняем нового пользователя");
            Role role = roleDao.findById(user.getRole_id());
            User entityUser = UserMapper.convertDtoToEntity(user, role);
            userFieldsValidation(entityUser);

            User userNew = userDao.save(entityUser);
            log.info("Пользователь сохранён с id: " + userNew.getId());
            return UserMapper.convertEntityToDto(userNew);
        } catch (EmptyFieldException e) {
            log.log(Level.WARNING, "Ошибка валидации: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public void delete(Long id) {
        log.info("Удаляем пользователя с id: " + id);
        userDao.delete(id);
    }

    public List<UserDto> findAll() {
        log.info("Получаем всех пользователей");
        List<User> users = userDao.findAll();
        log.info("Найдено пользователей: " + users.size());
        return users.stream().map(UserMapper::convertEntityToDto).toList();
    }

    public UserDto findById(Long id) {
        log.info("Ищем пользователя с id: " + id);
        User user = userDao.findById(id);
        if (user == null) return null;
        return UserMapper.convertEntityToDto(user);
    }

    public UserDto update(Long id, UserDto user) {
        try {
            log.info("Обновляем пользователя с id: " + id);
            Role role = roleDao.findById(user.getRole_id());
            User entityUser = UserMapper.convertDtoToEntity(user, role);
            userFieldsValidation(entityUser);

            User userUpdated = userDao.update(id, entityUser);
            log.info("Пользователь с id " + id + " успешно обновлён");
            return UserMapper.convertEntityToDto(userUpdated);
        } catch (EmptyFieldException e) {
            log.log(Level.WARNING, "Ошибка валидации: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private void userFieldsValidation(User user) throws EmptyFieldException {
        for (Field field : user.getClass().getDeclaredFields()) {
            if (field.getName().equals("id")) continue;
            field.setAccessible(true);
            try {
                Object value = field.get(user);
                if (value == null
                        || (value instanceof String && ((String) value).isEmpty())
                        || (value instanceof Number && ((Number) value).intValue() == 0)) {
                    log.warning("Поле " + field.getName() + " пустое");
                    throw new EmptyFieldException("Field " + field.getName() + " is empty");
                }
            } catch (IllegalAccessException e) {
                log.log(Level.SEVERE, "Ошибка доступа к полю: " + e.getMessage(), e);
                throw new EmptyFieldException(e.getMessage());
            }
        }
        log.info("Валидация полей пользователя успешно завершена");
    }
}
