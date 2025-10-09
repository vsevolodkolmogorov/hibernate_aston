package ru.astondevs.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.astondevs.dao.RoleDao;
import ru.astondevs.dao.UserDao;
import ru.astondevs.dto.UserDto;
import ru.astondevs.entity.Role;
import ru.astondevs.entity.User;
import ru.astondevs.errors.EmptyFieldException;
import ru.astondevs.errors.UserNotFoundedException;
import ru.astondevs.util.UserMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private RoleDao roleDao;

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserService userService;

    private UserDto userDto;
    private User userEntity;
    private final List<User> userList = new ArrayList<>();
    private Role roleUser;

    private static Stream<Arguments> invalidUserProvider() {
        return Stream.of(
                Arguments.of(new UserDto(null, "user@gmail.com", 18, 1L), "Поле name пустое"),
                Arguments.of(new UserDto("user", null, 18, 1L), "Поле email пустое"),
                Arguments.of(new UserDto("user", "user@gmail.com", 0, 1L), "Поле age пустое")
        );
    }

    @BeforeEach
    void initDto() {
        roleUser = new Role();
        roleUser.setName("user");

        userDto = new UserDto("user", "user@gmail.com", 18, 1L);
        userEntity = UserMapper.convertDtoToEntity(userDto, roleUser);

        userList.add(userEntity);
    }

    @Nested
    @DisplayName("Метод save()")
    class SaveTests {
        @Test
        @DisplayName("позитивное сохранение")
        void save_whenValidUserDto_thenReturnSavedUserDto() {
            when(roleDao.findById(userDto.getRole_id())).thenReturn(roleUser);
            when(userDao.save(userEntity)).thenReturn(userEntity);

            try (MockedStatic<UserMapper> mockedMapper = Mockito.mockStatic(UserMapper.class)) {
                mockedMapper.when(() -> UserMapper.convertDtoToEntity(userDto, roleUser))
                        .thenReturn(userEntity);
                mockedMapper.when(() -> UserMapper.convertEntityToDto(userEntity))
                        .thenReturn(userDto);

                UserDto result = userService.save(userDto);

                assertThat(result)
                        .isNotNull()
                        .extracting(UserDto::getName, UserDto::getEmail, UserDto::getAge, UserDto::getRole_id)
                        .containsExactly("user", "user@gmail.com", 18, 1L);
                verify(roleDao).findById(userDto.getRole_id());
                verify(userDao).save(userEntity);
            }
        }

        @DisplayName("негативный тест параметризованный")
        @ParameterizedTest(name = "negative save() → {1}")
        @MethodSource("ru.astondevs.service.UserServiceTest#invalidUserProvider")
        void save_whenInvalidUserDto_thenThrowsException(UserDto invalidDto, String expectedMessage) {
            when(roleDao.findById(invalidDto.getRole_id())).thenReturn(roleUser);

            assertThatThrownBy(() -> userService.save(invalidDto))
                    .isInstanceOf(RuntimeException.class)
                    .hasCauseInstanceOf(EmptyFieldException.class)
                    .hasRootCauseMessage(expectedMessage);
        }
    }

    @Nested
    @DisplayName("Метод delete()")
    class DeleteTests {

        @Test
        @DisplayName("позитивное удаление")
        void delete_whenValidUserId_thenDelete() {
            doNothing().when(userDao).delete(1L);
            userService.delete(1L);
            verify(userDao).delete(1L);
        }

        @Test
        @DisplayName("негативное удаление")
        void delete_whenValidUserDto_thenThrowNotFoundedException() {
            assertThatThrownBy(() -> userService.delete(null))
                    .isInstanceOf(UserNotFoundedException.class)
                    .hasMessage("Пользователь с id: null не найден!");
        }
    }

    @Nested
    @DisplayName("Метод findAll()")
    class FindAllTests {

        @Test
        @DisplayName("позитивный поиск")
        void findAll_whenDataBaseHaveOneUser_thenReturnUser() {
            when(userDao.findAll()).thenReturn(userList);

            try (MockedStatic<UserMapper> mockedMapper = Mockito.mockStatic(UserMapper.class)) {
                mockedMapper.when(() -> UserMapper.convertDtoToEntity(userDto, roleUser))
                        .thenReturn(userEntity);
                mockedMapper.when(() -> UserMapper.convertEntityToDto(userEntity))
                        .thenReturn(userDto);

                List<UserDto> result = userService.findAll();

                assertThat(result)
                        .isNotNull()
                        .hasSize(1)
                        .extracting(UserDto::getName, UserDto::getEmail, UserDto::getAge, UserDto::getRole_id)
                        .containsExactly(tuple("user", "user@gmail.com", 18, 1L));
                verify(userDao).findAll();
            }
        }

        @Test
        @DisplayName("негативный поиск")
        void findAll_whenDataBaseEmpty_thenReturnUser() {
            when(userDao.findAll()).thenReturn(new ArrayList<>());

            try (MockedStatic<UserMapper> mockedMapper = Mockito.mockStatic(UserMapper.class)) {
                mockedMapper.when(() -> UserMapper.convertDtoToEntity(userDto, roleUser))
                        .thenReturn(userEntity);
                mockedMapper.when(() -> UserMapper.convertEntityToDto(userEntity))
                        .thenReturn(userDto);

                List<UserDto> result = userService.findAll();

                assertThat(result)
                        .isNotNull()
                        .isEmpty();
                verify(userDao).findAll();
            }
        }
    }

    @Nested
    @DisplayName("Метод findById()")
    class FindById {
        @Test
        @DisplayName("позитивный тест")
        void findById_whenDataBaseHaveUser_thenReturnUser() {
            when(userDao.findById(1L)).thenReturn(userEntity);

            try (MockedStatic<UserMapper> mockedMapper = Mockito.mockStatic(UserMapper.class)) {
                mockedMapper.when(() -> UserMapper.convertDtoToEntity(userDto, roleUser))
                        .thenReturn(userEntity);
                mockedMapper.when(() -> UserMapper.convertEntityToDto(userEntity))
                        .thenReturn(userDto);

                UserDto result = userService.findById(1L);

                assertThat(result)
                        .isNotNull()
                        .extracting(UserDto::getName, UserDto::getEmail, UserDto::getAge, UserDto::getRole_id)
                        .containsExactly("user", "user@gmail.com", 18, 1L);
                verify(userDao).findById(1L);
            }
        }

        @Test
        @DisplayName("негативный тест")
        void findById_whenUserNotFound_thenThrowUserNotFoundedException() {
            when(userDao.findById(999L)).thenReturn(null);

            assertThatThrownBy(() -> userService.findById(999L))
                    .isInstanceOf(UserNotFoundedException.class)
                    .hasMessage("Пользователь с id: 999 не найден!");
        }
    }

    @Nested
    @DisplayName("Метод update()")
    class Update {
        @Test
        @DisplayName("позитивный тест")
        void update_whenValidUserDto_thenReturnUpdatedUserDto() {
            UserDto userUpdateDto = new UserDto("userUpdated", "user@gmail.com", 18, 1L);
            User userEntityUpdate = UserMapper.convertDtoToEntity(userUpdateDto, roleUser);

            when(roleDao.findById(userUpdateDto.getRole_id())).thenReturn(roleUser);
            when(userDao.findById(anyLong())).thenReturn(userEntity);
            when(userDao.update(1L, userEntityUpdate)).thenReturn(userEntityUpdate);

            try (MockedStatic<UserMapper> mockedMapper = Mockito.mockStatic(UserMapper.class)) {
                mockedMapper.when(() -> UserMapper.convertDtoToEntity(userUpdateDto, roleUser))
                        .thenReturn(userEntityUpdate);
                mockedMapper.when(() -> UserMapper.convertEntityToDto(userEntityUpdate))
                        .thenReturn(userUpdateDto);

                UserDto result = userService.update(1L, userUpdateDto);

                assertThat(result)
                        .isNotNull()
                        .extracting(UserDto::getName, UserDto::getEmail, UserDto::getAge, UserDto::getRole_id)
                        .containsExactly("userUpdated", "user@gmail.com", 18, 1L);

                verify(roleDao).findById(userUpdateDto.getRole_id());
                verify(userDao).update(1L, userEntityUpdate);
            }
        }

        @Test
        @DisplayName("негативный тест пользователь не найден")
        void update_whenUserNotFound_thenThrowUserNotFoundedException() {
            UserDto userUpdateDto = new UserDto("userUpdated", "user@gmail.com", 18, 1L);
            assertThatThrownBy(() -> userService.update(100L, userUpdateDto))
                    .isInstanceOf(UserNotFoundedException.class)
                    .hasMessage("Пользователь с id: 100 не найден!");
        }

        @Test
        @DisplayName("негативный тест роль не найдена")
        void update_whenRoleNotFound_thenThrowUserNotFoundedException() {
            UserDto userUpdateDto = new UserDto("userUpdated", "user@gmail.com", 18, 99L);
            when(roleDao.findById(99L)).thenReturn(null);
            when(userDao.findById(anyLong())).thenReturn(userEntity);

            assertThatThrownBy(() -> userService.update(1L, userUpdateDto))
                    .isInstanceOf(RuntimeException.class)
                    .hasCauseInstanceOf(EmptyFieldException.class)
                    .hasRootCauseMessage("Поле role пустое");
        }

        /**
         * Тест проверяет обновление пользователя.
         * Особенность: метод update в сервисе обновляет пользователя не по отдельным полям,
         * а заменяет всю сущность целиком.
         * Поэтому перед обновлением необходимо создать через консоль полный объект UserDto,
         * а не частично заполненный.
         */
        @DisplayName("негативный тест параметризованный")
        @ParameterizedTest(name = "negative save() → {1}")
        @MethodSource("ru.astondevs.service.UserServiceTest#invalidUserProvider")
        void update_whenInvalidUserDto_thenThrowsException(UserDto invalidDto, String expectedMessage) {
            when(roleDao.findById(invalidDto.getRole_id())).thenReturn(roleUser);
            when(userDao.findById(anyLong())).thenReturn(userEntity);

            assertThatThrownBy(() -> userService.update(1L, invalidDto))
                    .isInstanceOf(RuntimeException.class)
                    .hasCauseInstanceOf(EmptyFieldException.class)
                    .hasRootCauseMessage(expectedMessage);
        }
    }
}