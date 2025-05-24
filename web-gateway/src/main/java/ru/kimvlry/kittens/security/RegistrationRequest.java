package ru.kimvlry.kittens.security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Date;

public record RegistrationRequest(
        @NotBlank String username,
        @NotBlank @Size(min = 8, max = 20) String password,
        @NotNull @Email String email,
        @NotBlank String name,
        Date birthDate
) {
}
