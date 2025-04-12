package ru.kimvlry.kittens.web.exception;

public class NonNullableException extends RuntimeException{
    public NonNullableException(String attribute) {
        super(String.format("attribute %s is non-nullable", attribute));
    }
}
