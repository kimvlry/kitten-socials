package ru.kimvlry.kittens.web.dto.security;

public record AuthRequest (
        String username,
        String password
) {
}
