package ru.kimvlry.kittens.socials.security.utils.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ru.kimvlry.kittens.socials.dto.security.TokenPair;
import ru.kimvlry.kittens.socials.entities.RefreshToken;
import ru.kimvlry.kittens.socials.entities.Role;
import ru.kimvlry.kittens.socials.entities.User;
import ru.kimvlry.kittens.socials.repository.RefreshTokenRepository;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
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

    public TokenPair updAccessAndRefreshTokens(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = (User) userDetails;

        refreshTokenRepository.deleteAllByUser(user);
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
        refreshToken.setExpiryTimestamp(Instant.now().plusMillis(refreshExpirationMs));
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
        String username = decodedJWT.getSubject();
        log.debug("Extracted username from token: {}", username);
        return username;
    }

    public List<String> getRolesFromToken(String token) {
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(jwtSecret))
                .build()
                .verify(token);
        String roles = decodedJWT.getClaim("roles").asString();
        log.debug("Extracted roles from token: {}", roles);
        return Arrays.asList(roles.split(","));
    }

    public boolean validateToken(String accessToken) {
        try {
            JWT.require(Algorithm.HMAC512(jwtSecret))
                    .build()
                    .verify(accessToken);
            log.debug("Token validated successfully");
            return true;
        }
        catch (JWTVerificationException e) {
            log.error("failed to validate access token", e);
            return false;
        }
    }
}