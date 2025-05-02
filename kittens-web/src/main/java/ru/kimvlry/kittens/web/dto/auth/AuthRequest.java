package ru.kimvlry.kittens.web.dto.auth;

public record AuthRequest (
        String username,
        String password
) {
}
