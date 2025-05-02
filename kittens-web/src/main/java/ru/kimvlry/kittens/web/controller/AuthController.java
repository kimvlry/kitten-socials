package ru.kimvlry.kittens.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kimvlry.kittens.web.dto.AuthRequest;
import ru.kimvlry.kittens.web.dto.AuthResponse;
import ru.kimvlry.kittens.web.dto.RegistrationRequest;
import ru.kimvlry.kittens.web.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegistrationRequest request) {
        String jwt = authService.register(request);
        return ResponseEntity.ok(new AuthResponse(jwt));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        String jwt = authService.login(request);
        return ResponseEntity.ok(new AuthResponse(jwt));
    }
}