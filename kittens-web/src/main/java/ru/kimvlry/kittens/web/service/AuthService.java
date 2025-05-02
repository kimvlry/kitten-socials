package ru.kimvlry.kittens.web.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.kimvlry.kittens.entities.Owner;
import ru.kimvlry.kittens.web.dto.AuthRequest;
import ru.kimvlry.kittens.web.dto.RegistrationRequest;
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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private UserOwnerMappingRepository mappingRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

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