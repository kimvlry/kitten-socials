package ru.kimvlry.kittens.socials.repository.security;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kimvlry.kittens.socials.entities.security.RefreshToken;
import ru.kimvlry.kittens.socials.entities.security.User;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteAllByUser(User user);
}
