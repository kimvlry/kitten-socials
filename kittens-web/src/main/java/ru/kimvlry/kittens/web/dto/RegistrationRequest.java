package ru.kimvlry.kittens.web.dto;

import java.time.LocalDate;

public record RegistrationRequest(
        String username,
        String password,
        String email,
        String name,
        LocalDate birthDate
) {
}
