package ru.astondevs.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import ru.astondevs.dto.UserDto;
import ru.astondevs.entity.Role;
import ru.astondevs.entity.User;
import ru.astondevs.errors.EmptyFieldException;
import ru.astondevs.errors.RoleNotFoundedException;
import ru.astondevs.errors.UserNotFoundedException;
import ru.astondevs.repository.UserRepository;
import ru.astondevs.service.impl.UserServiceImpl;
import ru.astondevs.util.UserMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleInternalService roleService;

    @Spy
    private UserMapper mapper = new UserMapper();

    @InjectMocks
    private UserServiceImpl userService;

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
        roleUser = new Role("user");
        roleUser.setId(1L);
        userDto = new UserDto("user", "user@gmail.com", 18, 1L);
        userEntity = mapper.convertDtoToEntity(userDto, roleUser);
        userEntity.setId(1L);
        userList.add(userEntity);
    }

    @Nested
    @DisplayName("Метод save()")
    class SaveTests {
        @Test
        @DisplayName("позитивное сохранение")
        void save_whenValidUserDto_thenReturnSavedUserDto() {
            when(roleService.findEntityById(userDto.getRole_id())).thenReturn(roleUser);
            when(userRepository.save(any(User.class))).thenReturn(userEntity);

            UserDto result = userService.save(userDto);

            assertThat(result)
                    .isNotNull()
                    .extracting(UserDto::getName, UserDto::getEmail, UserDto::getAge, UserDto::getRole_id)
                    .containsExactly("user", "user@gmail.com", 18, 1L);
            verify(roleService).findEntityById(userDto.getRole_id());
            verify(userRepository).save(argThat(user ->
                    "user".equals(user.getName()) &&
                            "user@gmail.com".equals(user.getEmail()) &&
                            user.getAge() == 18
            ));
        }

        @DisplayName("негативный тест параметризованный")
        @ParameterizedTest(name = "negative save() → {1}")
        @MethodSource("ru.astondevs.service.UserServiceTest#invalidUserProvider")
        void save_whenInvalidUserDto_thenThrowsException(UserDto invalidDto, String expectedMessage) {
            when(roleService.findEntityById(invalidDto.getRole_id())).thenReturn(roleUser);

            assertThatThrownBy(() -> userService.save(invalidDto))
                    .isInstanceOf(EmptyFieldException.class)
                    .hasMessage(expectedMessage);
        }
    }

    @Nested
    @DisplayName("Метод delete()")
    class DeleteTests {

        @Test
        @DisplayName("позитивное удаление")
        void delete_whenValidUserId_thenDelete() {
            when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(userEntity));
            doNothing().when(userRepository).delete(userEntity);
            userService.delete(1L);
            verify(userRepository).delete(userEntity);
        }

        @Test
        @DisplayName("негативное удаление")
        void delete_whenValidUserDto_thenThrowNotFoundedException() {
            assertThatThrownBy(() -> userService.delete(null))
                    .isInstanceOf(UserNotFoundedException.class);
        }
    }

    @Nested
    @DisplayName("Метод findAll()")
    class FindAllTests {

        @Test
        @DisplayName("позитивный поиск")
        void findAll_whenDataBaseHaveOneUser_thenReturnUser() {
            when(userRepository.findAll()).thenReturn(userList);

            List<UserDto> result = userService.findAll();

            assertThat(result)
                    .isNotNull()
                    .hasSize(1)
                    .extracting(UserDto::getName, UserDto::getEmail, UserDto::getAge, UserDto::getRole_id)
                    .containsExactly(tuple("user", "user@gmail.com", 18, 1L));
            verify(userRepository).findAll();
        }

        @Test
        @DisplayName("негативный поиск")
        void findAll_whenDataBaseEmpty_thenReturnUser() {
            assertThatThrownBy(() -> userService.findAll())
                    .isInstanceOf(UserNotFoundedException.class);
        }
    }

    @Nested
    @DisplayName("Метод findById()")
    class FindById {
        @Test
        @DisplayName("позитивный тест")
        void findById_whenDataBaseHaveUser_thenReturnUser() {
            when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(userEntity));

            UserDto result = userService.findById(1L);

            assertThat(result)
                    .isNotNull()
                    .extracting(UserDto::getName, UserDto::getEmail, UserDto::getAge, UserDto::getRole_id)
                    .containsExactly("user", "user@gmail.com", 18, 1L);
            verify(userRepository).findById(1L);
        }

        @Test
        @DisplayName("негативный тест")
        void findById_whenUserNotFound_thenThrowUserNotFoundedException() {
            assertThatThrownBy(() -> userService.findById(999L))
                    .isInstanceOf(UserNotFoundedException.class);
        }
    }

    @Nested
    @DisplayName("Метод update()")
    class Update {
        @Test
        @DisplayName("позитивный тест")
        void update_whenValidUserDto_thenReturnUpdatedUserDto() {
            UserDto userUpdateDto = new UserDto("userUpdated", "user@gmail.com", 18, 1L);
            User userEntityUpdate = mapper.convertDtoToEntity(userUpdateDto, roleUser);

            when(roleService.findEntityById(userUpdateDto.getRole_id())).thenReturn(roleUser);
            when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(userEntity));
            when(userRepository.save(any(User.class))).thenReturn(userEntityUpdate);

            UserDto result = userService.update(1L, userUpdateDto);

            assertThat(result)
                    .isNotNull()
                    .extracting(UserDto::getName, UserDto::getEmail, UserDto::getAge, UserDto::getRole_id)
                    .containsExactly("userUpdated", "user@gmail.com", 18, 1L);

            verify(roleService).findEntityById(userUpdateDto.getRole_id());
            verify(userRepository).save(argThat(user ->
                    "userUpdated".equals(user.getName()) &&
                            "user@gmail.com".equals(user.getEmail()) &&
                            user.getAge() == 18
            ));

        }

        @Test
        @DisplayName("негативный тест пользователь не найден")
        void update_whenUserNotFound_thenThrowUserNotFoundedException() {
            UserDto userUpdateDto = new UserDto("userUpdated", "user@gmail.com", 18, 1L);
            assertThatThrownBy(() -> userService.update(100L, userUpdateDto))
                    .isInstanceOf(UserNotFoundedException.class);
        }

        @Test
        @DisplayName("негативный тест роль не найдена")
        void update_whenRoleNotFound_thenThrowRoleNotFoundedException() {
            UserDto userUpdateDto = new UserDto("userUpdated", "user@gmail.com", 18, 99L);

            when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(userEntity));
            when(roleService.findEntityById(99L)).thenThrow(new RoleNotFoundedException("Роль не найдена"));

            assertThatThrownBy(() -> userService.update(1L, userUpdateDto))
                    .isInstanceOf(RoleNotFoundedException.class);
        }

    }
}