package ru.kimvlry.kittens.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kimvlry.kittens.web.entities.Role;
import ru.kimvlry.kittens.web.entities.User;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
