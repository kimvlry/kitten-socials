package ru.kimvlry.kittens.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kimvlry.kittens.entities.RefreshToken;
import ru.kimvlry.kittens.entities.User;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteAllByUser(User user);
}
