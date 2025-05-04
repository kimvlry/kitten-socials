package ru.kimvlry.kittens.web.dto.security;

import java.util.Date;

public record RegistrationRequest(
        String username,
        String password,
        String email,
        String name,
        Date birthDate
) {
}
