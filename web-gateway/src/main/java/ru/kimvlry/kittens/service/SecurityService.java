package ru.kimvlry.kittens.service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.kimvlry.kittens.common.CreateOwnerForUserDto;
import ru.kimvlry.kittens.entities.Role;
import ru.kimvlry.kittens.repository.RoleRepository;
import ru.kimvlry.kittens.security.AuthenticationRequest;
import ru.kimvlry.kittens.security.RefreshRequest;
import ru.kimvlry.kittens.security.RegistrationRequest;
import ru.kimvlry.kittens.security.TokenPair;
import ru.kimvlry.kittens.entities.RefreshToken;
import ru.kimvlry.kittens.repository.RefreshTokenRepository;
import ru.kimvlry.kittens.repository.UserRepository;
import ru.kimvlry.kittens.security.utils.jwt.JwtTokenProvider;
import ru.kimvlry.kittens.entities.User;

import java.time.Instant;
import java.util.Collections;


@Service
public class SecurityService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RabbitTemplate rabbitTemplate;

    public SecurityService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            AuthenticationManager authManager,
            JwtTokenProvider jwtTokenProvider,
            RefreshTokenRepository refreshTokenRepository,
            RabbitTemplate rabbitTemplate) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.authManager = authManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Transactional
    public TokenPair register(@Valid RegistrationRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username already exists");
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User user = new User();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setEmail(request.email());
        user.setRoles(Collections.singleton(userRole));

        userRepository.save(user);

        CreateOwnerForUserDto payload = new CreateOwnerForUserDto(user.getId(), user.getUsername());
        rabbitTemplate.convertAndSend(
                "owner.queue",
                payload,
                message -> {
                    message.getMessageProperties().setHeader("action", "CREATE_OWNER_FOR_USER");
                    return message;
                }
        );

        return jwtTokenProvider.generateAccessAndRefreshTokens(user);
    }

    @Transactional
    public TokenPair login(@Valid AuthenticationRequest request) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );
        return jwtTokenProvider.updAccessAndRefreshTokens(authentication);
    }

    @Transactional
    public TokenPair refresh(@Valid RefreshRequest request) {
        String token = request.refreshToken();

        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        if (refreshToken.isRevoked()) {
            throw new IllegalArgumentException("Token has been revoked");
        }

        if (refreshToken.getExpiryTimestamp().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Token has expired");
        }

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        User user = refreshToken.getUser();
        return jwtTokenProvider.generateAccessAndRefreshTokens(user);
    }
}