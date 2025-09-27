package ru.astondevs.util;

import ru.astondevs.dto.UserDto;
import ru.astondevs.entity.Role;
import ru.astondevs.entity.User;

public class UserMapper {

    public static User convertDtoToEntity(UserDto dto, Role role) {
       return new User(dto.getName(),dto.getEmail(),dto.getAge(), role);
    }

    public static UserDto convertEntityToDto(User user) {
        return new UserDto(user.getName(),user.getEmail(),user.getAge(),user.getRole().getId());
    }
}
