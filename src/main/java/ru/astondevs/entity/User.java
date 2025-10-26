package ru.astondevs.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Entity
@Table(name = "users", schema = "public")
@Getter
@Setter
@Schema(name = "User", description = "Сущность пользователя")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID пользователя", example = "1", required = true)
    private Long id;

    @Schema(description = "Имя пользователя", example = "Иван Иванов", required = true)
    private String name;

    @Schema(description = "Email пользователя", example = "ivan@example.com", required = true)
    private String email;

    @Schema(description = "Возраст пользователя", example = "30", required = true)
    private int age;

    @Schema(description = "Дата создания пользователя", example = "2025-10-23T15:30:00", required = true)
    private LocalDateTime create_at;

    @ManyToOne
    @JoinColumn(name = "role_id")
    @Schema(description = "Роль пользователя")
    private Role role;

    public User(String name, String email, int age, Role role) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.create_at = LocalDateTime.now();
        this.role = role;
    }

    public User(Long id, String name, String email, int age, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
        this.create_at = LocalDateTime.now();
        this.role = role;
    }

    public User() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                ", create_at=" + create_at +
                '}';
    }
}
