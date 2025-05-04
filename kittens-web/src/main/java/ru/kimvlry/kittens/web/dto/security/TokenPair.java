package ru.kimvlry.kittens.web.dto.security;

public record TokenPair(
        String accessToken,
        String refreshToken
) {
}
