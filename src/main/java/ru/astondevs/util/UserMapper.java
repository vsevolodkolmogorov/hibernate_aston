package ru.astondevs.util;

import org.springframework.stereotype.Component;
import ru.astondevs.dto.UserDto;
import ru.astondevs.entity.Role;
import ru.astondevs.entity.User;

@Component
public class UserMapper {

    public User convertDtoToEntity(UserDto dto, Role role) {
       return new User(dto.getName(),dto.getEmail(),dto.getAge(), role);
    }

    public UserDto convertEntityToDto(User user) {
        return new UserDto(user.getName(),user.getEmail(),user.getAge(),user.getRole().getId());
    }
}
