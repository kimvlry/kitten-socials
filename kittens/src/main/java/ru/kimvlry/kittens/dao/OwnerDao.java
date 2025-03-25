package ru.kimvlry.kittens.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import ru.kimvlry.kittens.entities.Owner;
import ru.kimvlry.kittens.exceptions.DatabaseException;

import java.util.List;

public class OwnerDao implements Dao<Owner> {
    private EntityManager em;

    @Override
    public Owner save(Owner entity) throws DatabaseException {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            if (entity.getId() == null) {
                em.persist(entity);
            } else {
                em.merge(entity);
            }
            transaction.commit();
            return entity;

        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new DatabaseException("saving", e.getMessage());
        }
    }

    @Override
    public void deleteById(long id) throws DatabaseException {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            Owner owner = em.find(Owner.class, id);
            em.remove(owner);
            transaction.commit();

        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new DatabaseException("deletion", e.getMessage());
        }
    }

    @Override
    public void deleteByEntity(Owner entity) throws DatabaseException {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            Owner owner = em.find(Owner.class, entity.getId());
            em.remove(owner);
            transaction.commit();

        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new DatabaseException("deletion", e.getMessage());
        }
    }

    @Override
    public void deleteAll() throws DatabaseException {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.createQuery("DELETE from Owner").executeUpdate();
            transaction.commit();

        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new DatabaseException("deletion", e.getMessage());
        }
    }

    @Override
    public Owner update(Owner entity) throws DatabaseException {
        return save(entity);
    }

    @Override
    public Owner getById(long id) {
        return em.find(Owner.class, id);
    }

    @Override
    public List<Owner> getAll() {
        return em.createQuery("SELECT owners FROM Owner owners", Owner.class).getResultList();
    }
}
