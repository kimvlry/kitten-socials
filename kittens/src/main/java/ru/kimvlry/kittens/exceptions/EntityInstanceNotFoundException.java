package ru.kimvlry.kittens.exceptions;

import java.io.IOException;

public class EntityInstanceNotFoundException extends IOException {
    public EntityInstanceNotFoundException(String entity_name) {
        super(String.format("couldn't find %s", entity_name));
    }
}
