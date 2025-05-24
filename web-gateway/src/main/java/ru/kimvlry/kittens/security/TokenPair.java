package ru.kimvlry.kittens.security;

import jakarta.validation.constraints.NotBlank;

public record TokenPair(
        @NotBlank
        String accessToken,
        @NotBlank
        String refreshToken
) {
}
