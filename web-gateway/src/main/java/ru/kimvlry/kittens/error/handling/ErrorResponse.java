package ru.kimvlry.kittens.error.handling;

public record ErrorResponse(
        int status,
        String error,
        String message
) {
}