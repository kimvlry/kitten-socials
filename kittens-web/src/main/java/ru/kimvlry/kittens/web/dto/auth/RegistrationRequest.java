package ru.kimvlry.kittens.web.dto.auth;

import java.time.Instant;

public record RegistrationRequest(
        String username,
        String password,
        String email,
        String name,
        Instant birthDate
) {
}
