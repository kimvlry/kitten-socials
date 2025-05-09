package ru.kimvlry.kittens.socials.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kimvlry.kittens.socials.dto.security.AuthRequest;
import ru.kimvlry.kittens.socials.dto.security.RefreshRequest;
import ru.kimvlry.kittens.socials.dto.security.TokenPair;
import ru.kimvlry.kittens.socials.dto.security.RegistrationRequest;
import ru.kimvlry.kittens.socials.service.security.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<TokenPair> register(@Valid @RequestBody RegistrationRequest request) {
        TokenPair jwts = authService.register(request);
        return ResponseEntity.ok(jwts);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenPair> login(@Valid @RequestBody AuthRequest request) {
        TokenPair jwts = authService.login(request);
        return ResponseEntity.ok(jwts);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenPair> refresh(@Valid @RequestBody RefreshRequest request) {
        TokenPair jwts = authService.refresh(request);
        return ResponseEntity.ok(jwts);
    }
}