package ru.kimvlry.kittens.web.exception;

public record ErrorResponse(
        int status,
        String error,
        String message
) {
}