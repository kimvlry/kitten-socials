package ru.kimvlry.kittens.web.dto.security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Date;

public record RegistrationRequest(
        @NotBlank String username,
        @Size(min = 8, max = 20) String password,
        @Email String email,
        @NotBlank String name,
        @NotBlank Date birthDate
) {
}
