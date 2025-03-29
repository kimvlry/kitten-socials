package ru.kimvlry.kittens.exceptions;

import java.io.IOException;

public class InvalidInstanceException extends IOException {
    public InvalidInstanceException(String message) {
        super(message);
    }
}
