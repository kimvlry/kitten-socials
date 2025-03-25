package ru.kimvlry.kittens.exceptions;

import java.io.IOException;

public class OwnerNotFoundException extends IOException {
    public OwnerNotFoundException() {
        super("couldn't find owner");
    }
}
