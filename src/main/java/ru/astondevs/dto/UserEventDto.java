package ru.astondevs.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserEventDto extends UserDto{
    private String eventType;
}
