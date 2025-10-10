package ru.astondevs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.astondevs.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
