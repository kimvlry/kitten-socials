package ru.kimvlry.kittens.socials.security;

import jakarta.validation.constraints.NotBlank;

public record TokenPair(
        @NotBlank
        String accessToken,
        @NotBlank
        String refreshToken
) {
}
