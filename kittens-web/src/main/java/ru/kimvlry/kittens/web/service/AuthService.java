package ru.kimvlry.kittens.web.service;

import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.kimvlry.kittens.entities.Owner;
import ru.kimvlry.kittens.web.dto.auth.AuthRequest;
import ru.kimvlry.kittens.web.dto.auth.RegistrationRequest;
import ru.kimvlry.kittens.web.repository.OwnerRepository;
import ru.kimvlry.kittens.web.repository.RoleRepository;
import ru.kimvlry.kittens.web.repository.UserOwnerMappingRepository;
import ru.kimvlry.kittens.web.repository.UserRepository;
import ru.kimvlry.kittens.web.security.auth.JwtTokenProvider;
import ru.kimvlry.kittens.web.security.role.Role;
import ru.kimvlry.kittens.web.security.user.User;
import ru.kimvlry.kittens.web.security.user.UserOwnerMapping;

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

    public AuthService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            OwnerRepository ownerRepository,
            UserOwnerMappingRepository mappingRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authManager,
            JwtTokenProvider jwtTokenProvider
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.ownerRepository = ownerRepository;
        this.mappingRepository = mappingRepository;
        this.passwordEncoder = passwordEncoder;
        this.authManager = authManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public String register(RegistrationRequest request) {
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
        owner.setBirthTimestamp(request.birthDate());
        owner = ownerRepository.save(owner);

        UserOwnerMapping mapping = new UserOwnerMapping();
        mapping.setUser(user);
        mapping.setOwner(owner);
        mappingRepository.save(mapping);

        return jwtTokenProvider.GenerateToken(user);
    }

    public String login(AuthRequest request) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );
        return jwtTokenProvider.GenerateToken(authentication);
    }
}