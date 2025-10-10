package ru.astondevs.service;

import ru.astondevs.entity.Role;

public interface RoleInternalService {
    Role findEntityById(Long id);
}
