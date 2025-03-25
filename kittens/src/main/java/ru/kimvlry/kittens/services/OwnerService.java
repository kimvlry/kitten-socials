package ru.kimvlry.kittens.services;

import ru.kimvlry.kittens.dao.OwnerDao;
import ru.kimvlry.kittens.entities.Owner;
import ru.kimvlry.kittens.exceptions.DatabaseException;
import ru.kimvlry.kittens.exceptions.EntityInstanceNotFoundException;
import ru.kimvlry.kittens.exceptions.InvalidInstanceException;

import java.util.List;
import java.util.Optional;

public class OwnerService {
    private final OwnerDao dao;

    public OwnerService(OwnerDao dao) {
        this.dao = dao;
    }

    public Owner addOwner(Owner owner) throws DatabaseException, InvalidInstanceException {
        if (owner.getName() == null || owner.getOwnedKittens() == null) {
            throw new InvalidInstanceException("null is invalid value for 'name' and 'ownedKittens' attributes of Owner entity instance");
        }
        return dao.save(owner);
    }

    public void deleteOwner(Owner entity) throws DatabaseException, EntityInstanceNotFoundException {
        Optional<Owner> owner = Optional.ofNullable(dao.getById(entity.getId()));
        if (owner.isEmpty()) {
            throw new EntityInstanceNotFoundException("Owner");
        }
        dao.deleteById(entity.getId());
    }

    public void deleteOwnerById(long id) throws DatabaseException, EntityInstanceNotFoundException {
        Optional<Owner> owner = Optional.ofNullable(dao.getById(id));
        if (owner.isEmpty()) {
            throw new EntityInstanceNotFoundException("Owner");
        }
        dao.deleteById(id);
    }

    public void deleteAllOwners() throws DatabaseException {
        dao.deleteAll();
    }

    public Owner update(Owner newEntity) throws DatabaseException, InvalidInstanceException {
        return addOwner(newEntity);
    }

    public Owner getById(long id) {
        return dao.getById(id);
    }

    public List<Owner> getAllOwners() {
        return dao.getAll();
    }
}
