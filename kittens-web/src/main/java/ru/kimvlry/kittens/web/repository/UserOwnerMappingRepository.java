package ru.kimvlry.kittens.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kimvlry.kittens.web.security.user.UserOwnerMapping;

import java.util.Optional;

public interface UserOwnerMappingRepository extends JpaRepository<UserOwnerMapping, Long> {
    Optional<UserOwnerMapping> findByUserId(Long userId);
}