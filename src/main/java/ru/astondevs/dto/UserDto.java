package ru.astondevs.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@Schema(name = "UserDto", description = "DTO для пользователя")
public class UserDto {
    @Schema(description = "ID пользователя", example = "1", required = true)
    private Long id;

    @Schema(description = "Имя пользователя", example = "Иван Иванов", required = true)
    private String name;

    @Schema(description = "Email пользователя", example = "ivan@example.com", required = true)
    private String email;

    @Schema(description = "Возраст пользователя", example = "30", required = true)
    private int age;

    @Schema(description = "ID роли пользователя", example = "1", required = true)
    private Long role_id;

    public UserDto(String name, String email, int age, Long role_id) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.role_id = role_id;
    }

    public UserDto(Long id, String name, String email, int age, Long role_id) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
        this.role_id = role_id;
    }

    public UserDto() {}

    @Override
    public String toString() {
        return "UserDto{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                ", role_id=" + role_id +
                '}';
    }
}
