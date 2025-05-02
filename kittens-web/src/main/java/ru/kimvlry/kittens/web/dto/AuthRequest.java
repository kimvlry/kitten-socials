package ru.kimvlry.kittens.web.dto;

public record AuthRequest (
        String username,
        String password
) {
}
