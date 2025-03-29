package ru.kimvlry.kittens.dao;

import ru.kimvlry.kittens.exceptions.DatabaseException;
import ru.kimvlry.kittens.exceptions.EntityInstanceNotFoundException;

import java.util.List;

public interface Dao<T> {
    T save(T entity) throws DatabaseException;
    void deleteById(long id) throws DatabaseException, EntityInstanceNotFoundException;
    void deleteByEntity(T entity) throws DatabaseException, EntityInstanceNotFoundException;
    void deleteAll() throws DatabaseException;
    T update(T entity) throws DatabaseException;
    T getById(long id);
    List<T> getAll();
}
