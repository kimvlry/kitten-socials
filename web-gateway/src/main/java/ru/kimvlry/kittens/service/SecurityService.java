package ru.kimvlry.kittens.service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.kimvlry.kittens.common.OwnerDto;
import ru.kimvlry.kittens.service.publisher.MessageClient;
import ru.kimvlry.kittens.security.AuthenticationRequest;
import ru.kimvlry.kittens.security.RefreshRequest;
import ru.kimvlry.kittens.security.RegistrationRequest;
import ru.kimvlry.kittens.security.TokenPair;
import ru.kimvlry.kittens.entities.RefreshToken;
import ru.kimvlry.kittens.repository.RefreshTokenRepository;
import ru.kimvlry.kittens.repository.RoleRepository;
import ru.kimvlry.kittens.repository.UserRepository;
import ru.kimvlry.kittens.security.utils.jwt.JwtTokenProvider;
import ru.kimvlry.kittens.entities.Role;
import ru.kimvlry.kittens.entities.User;

import java.time.Instant;
import java.util.Collections;


@Service
public class SecurityService {
    private final String queue = "owners.request";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MessageClient messageClient;

    public SecurityService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authManager,
            JwtTokenProvider jwtTokenProvider,
            RefreshTokenRepository refreshTokenRepository,
            MessageClient messageClient) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authManager = authManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
        this.messageClient = messageClient;
    }

    @Transactional
    public TokenPair register(@Valid RegistrationRequest request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setEmail(request.email());

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new IllegalArgumentException("Role ROLE_USER not found"));
        user.setRoles(Collections.singleton(userRole));
        user = userRepository.save(user);

        OwnerDto ownerDto = new OwnerDto(null, request.name(), null, null);
        OwnerDto createdOwner = messageClient.sendRequest(
                "owners.request",
                "CREATE_OWNER",
                ownerDto,
                OwnerDto.class
        );

        user.setOwnerId(createdOwner.id());
        userRepository.save(user);

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