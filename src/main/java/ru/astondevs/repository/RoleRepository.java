package ru.astondevs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.astondevs.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
