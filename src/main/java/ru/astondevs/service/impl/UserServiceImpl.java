package ru.astondevs.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.astondevs.dto.UserDto;
import ru.astondevs.entity.Role;
import ru.astondevs.entity.User;
import ru.astondevs.errors.EmptyFieldException;
import ru.astondevs.errors.UserNotFoundedException;
import ru.astondevs.repository.UserRepository;
import ru.astondevs.service.RoleInternalService;
import ru.astondevs.service.UserService;
import ru.astondevs.util.BeanUtilsHelper;
import ru.astondevs.util.UserMapper;

import java.lang.reflect.Field;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final RoleInternalService roleService;
    private final UserMapper userMapper;
    private static final Logger log = Logger.getLogger(UserServiceImpl.class.getName());

    public UserDto save(UserDto userDto) {
        Role role = roleService.findEntityById(userDto.getRole_id());
        User user = userMapper.convertDtoToEntity(userDto, role);
        userFieldsValidation(user);
        User userSaved = repository.save(user);
        return userMapper.convertEntityToDto(userSaved);
    }

    public void delete(Long id) {
        User user = findEntityById(id);
        repository.delete(user);
    }

    public List<UserDto> findAll() {
        List<User> userList = repository.findAll();
        if (userList.isEmpty()) throw new UserNotFoundedException("Пользователей нет в базе данных");
        return userList.stream().map(userMapper::convertEntityToDto).toList();
    }


    public UserDto update(Long id, UserDto userDto) {
        User existing = findEntityById(id);
        Role role = roleService.findEntityById(userDto.getRole_id());
        User newEntity  = userMapper.convertDtoToEntity(userDto, role);

        BeanUtilsHelper.copyNonNullProperties(newEntity, existing, "id");
        User userNew = repository.save(existing);
        return userMapper.convertEntityToDto(userNew);
    }


    public UserDto findById(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundedException("Пользователь c id " + id + " не найден"));
        return userMapper.convertEntityToDto(user);
    }

    private User findEntityById(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundedException("Пользователь c id " + id + " не найден"));
        return user;
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
                    throw new EmptyFieldException("Поле " + field.getName() + " пустое");
                }
            } catch (IllegalAccessException e) {
                log.log(Level.SEVERE, "Ошибка доступа к полю: " + e.getMessage(), e);
                throw new EmptyFieldException(e.getMessage());
            }
        }
        log.info("Валидация полей пользователя успешно завершена");
    }
}
