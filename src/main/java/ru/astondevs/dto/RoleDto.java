package ru.astondevs.dto;

import lombok.*;
import org.springframework.stereotype.Component;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@NoArgsConstructor
@Component
@Schema(name = "RoleDto", description = "DTO для роли пользователя")
public class RoleDto {
    @Schema(description = "ID пользователя", example = "1", required = false)
    private Long id;

    @Schema(description = "Название роли", example = "ADMIN", required = true)
    private String name;

    public RoleDto(String name) {
        this.name = name;
    }

    public RoleDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
