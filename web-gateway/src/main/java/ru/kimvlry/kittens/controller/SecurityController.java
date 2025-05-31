package ru.kimvlry.kittens.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kimvlry.kittens.security.AuthenticationRequest;
import ru.kimvlry.kittens.security.RefreshRequest;
import ru.kimvlry.kittens.security.RegistrationRequest;
import ru.kimvlry.kittens.security.TokenPair;
import ru.kimvlry.kittens.service.SecurityService;

@RestController
@RequestMapping("/auth")
public class SecurityController {
    private final SecurityService securityService;

    public SecurityController(SecurityService securityService) {
        this.securityService = securityService;
    }

    @PostMapping("/register")
    public ResponseEntity<TokenPair> register(@Valid @RequestBody RegistrationRequest request) {
        TokenPair jwts = securityService.register(request);
        return ResponseEntity.ok(jwts);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenPair> login(@Valid @RequestBody AuthenticationRequest request) {
        TokenPair jwts = securityService.login(request);
        return ResponseEntity.ok(jwts);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenPair> refresh(@Valid @RequestBody RefreshRequest request) {
        TokenPair jwts = securityService.refresh(request);
        return ResponseEntity.ok(jwts);
    }
}