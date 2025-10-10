package ru.astondevs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.astondevs.dto.UserDto;
import ru.astondevs.errors.EmptyFieldException;
import ru.astondevs.errors.RoleNotFoundedException;
import ru.astondevs.service.UserService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(UserControllerTest.TestConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    private UserDto userDto1;
    private UserDto userDto2;

    @BeforeEach
    void setup() {
        userDto1 = new UserDto("user1", "user1@gmail.com", 18, 1L);
        userDto2 = new UserDto("user2", "user2@gmail.com", 25, 2L);
    }

    @Test
    @DisplayName("GET /api/users - все пользователи")
    void getAllUsers_returnsList() throws Exception {
        List<UserDto> users = Arrays.asList(userDto1, userDto2);
        when(userService.findAll()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(users.size()))
                .andExpect(jsonPath("$[0].name").value("user1"))
                .andExpect(jsonPath("$[1].name").value("user2"));
    }

    @Test
    @DisplayName("GET /api/users/{role_id} - пользователь найден")
    void getUserByRoleId_returnsUser() throws Exception {
        when(userService.findById(1L)).thenReturn(userDto1);

        mockMvc.perform(get("/api/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("user1"))
                .andExpect(jsonPath("$.email").value("user1@gmail.com"))
                .andExpect(jsonPath("$.age").value(18))
                .andExpect(jsonPath("$.role_id").value(1));
    }

    @Test
    @DisplayName("POST /api/users - успешное создание")
    void createUser_returnsCreatedUser() throws Exception {
        UserDto inputDto = new UserDto("newUser", "new@gmail.com", 30, 1L);
        UserDto savedDto = new UserDto("newUser", "new@gmail.com", 30, 1L);

        when(userService.save(any(UserDto.class))).thenReturn(savedDto);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("newUser"))
                .andExpect(jsonPath("$.email").value("new@gmail.com"))
                .andExpect(jsonPath("$.age").value(30))
                .andExpect(jsonPath("$.role_id").value(1));
    }

    @Test
    @DisplayName("POST /api/users - ошибка валидации")
    void createUser_whenInvalid_throwsEmptyFieldException() throws Exception {
        UserDto inputDto = new UserDto(null, "", 25, 1L);

        when(userService.save(any(UserDto.class)))
                .thenThrow(new EmptyFieldException("Поле name пустое"));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Поле name пустое"));
    }

    @Test
    @DisplayName("DELETE /api/users/{role_id} - успешное удаление")
    void deleteUser_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/users/{id}", 1L))
                .andExpect(status().isNoContent());

        Mockito.verify(userService).delete(1L);
    }

    @Test
    @DisplayName("PUT /api/users/{role_id} - успешное обновление")
    void updateUser_returnsUpdatedUser() throws Exception {
        UserDto updateDto = new UserDto("updatedUser", "updated@gmail.com", 28, 1L);
        UserDto returnedDto = new UserDto("updatedUser", "updated@gmail.com", 28, 1L);

        when(userService.update(anyLong(), any(UserDto.class))).thenReturn(returnedDto);

        mockMvc.perform(put("/api/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("updatedUser"))
                .andExpect(jsonPath("$.email").value("updated@gmail.com"))
                .andExpect(jsonPath("$.age").value(28))
                .andExpect(jsonPath("$.role_id").value(1));
    }

    @Test
    @DisplayName("PUT /api/users/{role_id} - роль не найдена")
    void updateUser_whenRoleNotFound_thenThrows() throws Exception {
        UserDto updateDto = new UserDto("userUpdated", "user@gmail.com", 18, 99L);

        when(userService.update(anyLong(), any(UserDto.class)))
                .thenThrow(new RoleNotFoundedException("Роль не найдена"));

        mockMvc.perform(put("/api/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Роль не найдена"));
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public UserService userService() {
            return Mockito.mock(UserService.class);
        }
    }
}