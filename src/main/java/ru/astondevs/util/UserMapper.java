package ru.astondevs.util;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import ru.astondevs.dto.UserDto;
import ru.astondevs.dto.UserEventDto;
import ru.astondevs.dto.enums.EventType;
import ru.astondevs.entity.Role;
import ru.astondevs.entity.User;

@Component
public class UserMapper {

    public User convertDtoToEntity(UserDto dto, Role role) {
       return new User(dto.getId(), dto.getName(),dto.getEmail(),dto.getAge(), role);
    }

    public UserDto convertEntityToDto(User user) {
        return new UserDto(user.getId(), user.getName(),user.getEmail(),user.getAge(),user.getRole().getId());
    }

    public UserEventDto convertEntityToEventDto(User user, EventType eventType) {
        UserEventDto eventDto = new UserEventDto();
        BeanUtils.copyProperties(user, eventDto);
        eventDto.setEventType(eventType.name());
        return eventDto;
    }
}
