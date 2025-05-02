package ru.kimvlry.kittens.web.security.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ru.kimvlry.kittens.web.security.role.Role;
import ru.kimvlry.kittens.web.security.user.User;

import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    @Value("${JWT_SECRET}")
    private String jwtSecret;

    @Value("${JWT_EXPIRATION}")
    private long expirationMs;

    @PostConstruct
    public void init() {
        if (jwtSecret == null || jwtSecret.isEmpty()) {
            throw new IllegalStateException("JWT_SECRET is not set in .env");
        }
        if (expirationMs <= 0) {
            throw new IllegalStateException("JWT_EXPIRATION must be positive");
        }
    }

    public String GenerateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String authorities = userDetails.getAuthorities().stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));

        return generate(authorities, userDetails.getUsername());
    }

    public String GenerateToken(User user) {
        String authorities = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.joining(","));

        return generate(authorities, user.getUsername());
    }

    private String generate(String authorities, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return JWT.create()
                .withSubject(subject)
                .withClaim("roles", authorities)
                .withIssuedAt(now)
                .withExpiresAt(expiryDate)
                .sign(Algorithm.HMAC512(jwtSecret));
    }

    public String GetUsernameFromToken(String token) {
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(jwtSecret))
                .build()
                .verify(token);
        return decodedJWT.getSubject();
    }

    public boolean ValidateToken(String token) {
        try {
            JWT.require(Algorithm.HMAC512(jwtSecret))
                    .build()
                    .verify(token);
            return true;
        }
        catch (JWTVerificationException e) {
            return false;
        }
    }
}