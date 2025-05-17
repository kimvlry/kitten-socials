package ru.kimvlry.kittens.socials.error.handling;

public record ErrorResponse(
        int status,
        String error,
        String message
) {
}