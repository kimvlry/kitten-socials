package ru.kimvlry.kittens.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kimvlry.kittens.web.dto.auth.AuthRequest;
import ru.kimvlry.kittens.web.dto.auth.RefreshRequest;
import ru.kimvlry.kittens.web.dto.auth.TokenPair;
import ru.kimvlry.kittens.web.dto.auth.RegistrationRequest;
import ru.kimvlry.kittens.web.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<TokenPair> register(@RequestBody RegistrationRequest request) {
        TokenPair jwts = authService.register(request);
        return ResponseEntity.ok(jwts);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenPair> login(@RequestBody AuthRequest request) {
        TokenPair jwts = authService.login(request);
        return ResponseEntity.ok(jwts);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenPair> refresh(@RequestBody RefreshRequest request) {
        TokenPair jwts = authService.refresh(request);
        return ResponseEntity.ok(jwts);
    }
}