package ru.kimvlry.kittens.web.dto.security;

import jakarta.validation.constraints.NotBlank;

public record AuthRequest (
        @NotBlank
        String username,
        @NotBlank
        String password
) {
}
