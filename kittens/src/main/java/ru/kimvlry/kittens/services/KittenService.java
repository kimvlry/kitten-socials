package ru.kimvlry.kittens.services;

import ru.kimvlry.kittens.dao.KittenDao;
import ru.kimvlry.kittens.entities.Kitten;
import ru.kimvlry.kittens.exceptions.DatabaseException;
import ru.kimvlry.kittens.exceptions.EntityInstanceNotFoundException;
import ru.kimvlry.kittens.exceptions.InvalidInstanceException;

import java.util.List;
import java.util.Optional;

public class KittenService {
    private final KittenDao dao;

    public KittenService(KittenDao dao) {
        this.dao = dao;
    }

    public Kitten addKitten(Kitten kitten) throws DatabaseException, InvalidInstanceException {
        if (kitten.getName() == null || kitten.getOwner() == null) {
            throw new InvalidInstanceException("null is invalid value for 'name' and 'owner' attributes of Kitten entity instance");
        }
        return dao.save(kitten);
    }

    public void deleteKitten(Kitten entity) throws DatabaseException, EntityInstanceNotFoundException {
        Optional<Kitten> kitten = Optional.ofNullable(dao.getById(entity.getId()));
        if (kitten.isEmpty()) {
            throw new EntityInstanceNotFoundException("Kitten");
        }
        dao.deleteById(entity.getId());
    }

    public void deleteKittenById(long id) throws DatabaseException, EntityInstanceNotFoundException {
        Optional<Kitten> kitten = Optional.ofNullable(dao.getById(id));
        if (kitten.isEmpty()) {
            throw new EntityInstanceNotFoundException("Kitten");
        }
        dao.deleteById(id);
    }

    public void deleteAllKittens() throws DatabaseException {
        dao.deleteAll();
    }

    public Kitten update(Kitten newEntity) throws DatabaseException, InvalidInstanceException {
        if (newEntity.getName() == null || newEntity.getOwner() == null) {
            throw new InvalidInstanceException("null is invalid value for 'name' and 'owner' attributes of Kitten entity instance");
        }
        return addKitten(newEntity);
    }

    public Kitten getById(long id) {
        return dao.getById(id);
    }

    public List<Kitten> getAllKittens() {
        return dao.getAll();
    }

    public Kitten addFriendship(Kitten cat1, Kitten cat2) throws DatabaseException {
        cat1.getFriends().add(cat2);
        cat2.getFriends().add(cat1);
        dao.save(cat2);
        return dao.save(cat1);
    }
}
