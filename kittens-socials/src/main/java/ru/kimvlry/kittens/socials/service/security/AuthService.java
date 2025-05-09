package ru.kimvlry.kittens.socials.service.security;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.kimvlry.kittens.socials.entities.Owner;
import ru.kimvlry.kittens.socials.dto.security.AuthRequest;
import ru.kimvlry.kittens.socials.dto.security.RefreshRequest;
import ru.kimvlry.kittens.socials.dto.security.RegistrationRequest;
import ru.kimvlry.kittens.socials.dto.security.TokenPair;
import ru.kimvlry.kittens.socials.entities.security.RefreshToken;
import ru.kimvlry.kittens.socials.repository.OwnerRepository;
import ru.kimvlry.kittens.socials.repository.security.RefreshTokenRepository;
import ru.kimvlry.kittens.socials.repository.security.RoleRepository;
import ru.kimvlry.kittens.socials.repository.security.UserOwnerMappingRepository;
import ru.kimvlry.kittens.socials.repository.security.UserRepository;
import ru.kimvlry.kittens.socials.security.utils.jwt.JwtTokenProvider;
import ru.kimvlry.kittens.socials.entities.security.Role;
import ru.kimvlry.kittens.socials.entities.security.User;
import ru.kimvlry.kittens.socials.security.utils.user.UserOwnerMapping;

import java.time.Instant;
import java.util.Collections;


@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OwnerRepository ownerRepository;
    private final UserOwnerMappingRepository mappingRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            OwnerRepository ownerRepository,
            UserOwnerMappingRepository mappingRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authManager,
            JwtTokenProvider jwtTokenProvider,
            RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.ownerRepository = ownerRepository;
        this.mappingRepository = mappingRepository;
        this.passwordEncoder = passwordEncoder;
        this.authManager = authManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
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

        Owner owner = new Owner();
        owner.setName(request.name());
        owner.setBirthDate(request.birthDate());
        owner = ownerRepository.save(owner);

        UserOwnerMapping mapping = new UserOwnerMapping();
        mapping.setUser(user);
        mapping.setOwner(owner);
        mappingRepository.save(mapping);

        return jwtTokenProvider.generateAccessAndRefreshTokens(user);
    }

    @Transactional
    public TokenPair login(@Valid AuthRequest request) {
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