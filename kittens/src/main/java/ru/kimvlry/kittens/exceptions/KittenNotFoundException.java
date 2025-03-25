package ru.kimvlry.kittens.exceptions;

import java.io.IOException;

public class KittenNotFoundException extends IOException {
    public KittenNotFoundException() {
        super("couldn't find kitten");
    }
}
