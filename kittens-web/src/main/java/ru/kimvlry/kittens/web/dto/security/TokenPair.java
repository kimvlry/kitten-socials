package ru.kimvlry.kittens.web.dto.security;

import jakarta.validation.constraints.NotBlank;

public record TokenPair(
        @NotBlank
        String accessToken,
        @NotBlank
        String refreshToken
) {
}
