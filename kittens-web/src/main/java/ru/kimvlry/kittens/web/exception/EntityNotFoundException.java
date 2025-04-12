package ru.kimvlry.kittens.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String entityName, Long id) {
        super(String.format("couldn't find %s by id %d", entityName, id));
    }
}
