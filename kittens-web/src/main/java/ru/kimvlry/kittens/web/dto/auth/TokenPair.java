package ru.kimvlry.kittens.web.dto.auth;

public record TokenPair(
        String accessToken,
        String refreshToken
) {
}
