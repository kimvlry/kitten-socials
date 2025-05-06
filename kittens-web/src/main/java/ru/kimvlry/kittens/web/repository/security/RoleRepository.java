package ru.kimvlry.kittens.web.repository.security;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kimvlry.kittens.web.entities.security.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
