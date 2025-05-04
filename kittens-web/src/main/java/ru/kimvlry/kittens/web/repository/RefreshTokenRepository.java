package ru.kimvlry.kittens.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kimvlry.kittens.web.entities.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
}
