package ru.kimvlry.kittens.socials.dto.security;

import jakarta.validation.constraints.NotBlank;

public record AuthRequest (
        @NotBlank
        String username,
        @NotBlank
        String password
) {
}
