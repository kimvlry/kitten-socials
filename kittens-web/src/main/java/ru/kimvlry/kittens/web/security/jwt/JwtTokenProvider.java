package ru.kimvlry.kittens.web.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ru.kimvlry.kittens.web.dto.auth.TokenPair;
import ru.kimvlry.kittens.web.entities.RefreshToken;
import ru.kimvlry.kittens.web.entities.Role;
import ru.kimvlry.kittens.web.entities.User;
import ru.kimvlry.kittens.web.repository.RefreshTokenRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Component
public class JwtTokenProvider {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access_token_expiration}")
    private long accessExpirationMs;

    @Value("${jwt.refresh_token_expiration}")
    private long refreshExpirationMs;

    public JwtTokenProvider(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @PostConstruct
    public void init() {
        if (jwtSecret == null || jwtSecret.isEmpty()) {
            throw new IllegalStateException("JWT_SECRET is not set in .env");
        }
        if (accessExpirationMs <= 0 || refreshExpirationMs <= 0) {
            throw new IllegalStateException("JWT_ACCESS_EXPIRATION, JWT_REFRESH_EXPIRATION must be positive");
        }
    }

    public TokenPair generateAccessAndRefreshTokens(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = (User) userDetails;

        String authorities = userDetails.getAuthorities().stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));

        return getTokenPair(userDetails, user, authorities);
    }

    public TokenPair generateAccessAndRefreshTokens(User user) {
        String authorities = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.joining(","));

        return getTokenPair(user, user, authorities);
    }

    @NotNull
    private TokenPair getTokenPair(UserDetails userDetails, User user, String authorities) {
        String access = generateAccessToken(authorities, userDetails.getUsername());
        String refresh = UUID.randomUUID().toString();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(refresh);
        refreshToken.setExpiryDate(LocalDateTime.from(Instant.now().plusMillis(refreshExpirationMs)));
        refreshTokenRepository.save(refreshToken);

        return new TokenPair(access, refresh);
    }

    public String generateAccessToken(String authorities, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessExpirationMs);

        return JWT.create()
                .withSubject(subject)
                .withClaim("roles", authorities)
                .withIssuedAt(now)
                .withExpiresAt(expiryDate)
                .sign(Algorithm.HMAC512(jwtSecret));

    }

    public String getUsernameFromToken(String token) {
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(jwtSecret))
                .build()
                .verify(token);
        return decodedJWT.getSubject();
    }

    public boolean validateToken(String accessToken) {
        try {
            JWT.require(Algorithm.HMAC512(jwtSecret))
                    .build()
                    .verify(accessToken);
            return true;
        }
        catch (JWTVerificationException e) {
            return false;
        }
    }
}