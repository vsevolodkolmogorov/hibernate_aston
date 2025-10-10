package ru.astondevs.service;

import ru.astondevs.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto save(UserDto userDto);
    void delete(Long id);
    List<UserDto> findAll();
    UserDto findById(Long id);
    UserDto update(Long id, UserDto user);
}
