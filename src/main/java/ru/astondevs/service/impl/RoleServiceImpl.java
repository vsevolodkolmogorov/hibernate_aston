package ru.astondevs.service.impl;

import org.springframework.stereotype.Service;
import ru.astondevs.dto.RoleDto;
import ru.astondevs.entity.Role;
import ru.astondevs.errors.RoleNotFoundedException;
import ru.astondevs.repository.RoleRepository;
import ru.astondevs.service.RoleInternalService;
import ru.astondevs.service.RoleService;

import java.util.List;
import java.util.logging.Logger;

@Service
public class RoleServiceImpl implements RoleService, RoleInternalService {
    private final RoleRepository repository;
    private static final Logger log = Logger.getLogger(UserServiceImpl.class.getName());

    public RoleServiceImpl(RoleRepository repository) {
        this.repository = repository;
    }

    public RoleDto save(RoleDto roleDto) {
        Role role = repository.save(new Role(roleDto.getName()));
        return new RoleDto(role.getName());
    }

    public void delete(Long id) {
        Role role = findEntityById(id);
        if (role == null) throw new RoleNotFoundedException("Роль c id " + id + " не найден");
        repository.delete(role);
    }

    public List<RoleDto> findAll() {
        List<Role> roleList = repository.findAll();
        if (roleList.isEmpty()) throw new RoleNotFoundedException("Ролей нет в базе данных");
        return roleList.stream().map(role -> new RoleDto(role.getName())).toList();
    }

    public RoleDto findById(Long id) {
        Role role = repository.findById(id)
                .orElseThrow(() -> new RoleNotFoundedException("Роль c id " + id + " не найден"));

        return new RoleDto(role.getName());
    }

    public Role findEntityById(Long id) {
        Role role = repository.findById(id)
                .orElseThrow(() -> new RoleNotFoundedException("Роль c id " + id + " не найден"));
        return role;
    }
}
