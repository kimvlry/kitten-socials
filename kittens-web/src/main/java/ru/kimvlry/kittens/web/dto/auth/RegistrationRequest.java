package ru.kimvlry.kittens.web.dto.auth;

import java.time.LocalDate;

public record RegistrationRequest(
        String username,
        String password,
        String email,
        String name,
        LocalDate birthDate
) {
}
