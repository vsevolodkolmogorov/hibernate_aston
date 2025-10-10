package ru.astondevs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.astondevs.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
