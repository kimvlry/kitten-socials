package ru.kimvlry.kittens.exceptions;

import java.io.IOException;

public class DatabaseException extends IOException {
    public DatabaseException(String operation, String msg) {
        super(String.format("Error occured while performing DB '%s' operation: %s", operation, msg));
    }
}
