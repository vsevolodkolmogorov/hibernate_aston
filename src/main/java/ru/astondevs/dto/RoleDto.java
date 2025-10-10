package ru.astondevs.dto;

import lombok.*;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@NoArgsConstructor
@AllArgsConstructor
public class RoleDto {
    private String name;
}
