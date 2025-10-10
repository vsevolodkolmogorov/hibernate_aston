package ru.astondevs.service;

import ru.astondevs.dto.RoleDto;

import java.util.List;

public interface RoleService {
    RoleDto save(RoleDto roleDto);
    void delete(Long id);
    List<RoleDto> findAll();
    RoleDto findById(Long id);
}
