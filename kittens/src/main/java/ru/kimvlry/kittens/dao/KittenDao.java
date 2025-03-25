package ru.kimvlry.kittens.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import ru.kimvlry.kittens.entities.Kitten;
import ru.kimvlry.kittens.exceptions.DatabaseException;
import ru.kimvlry.kittens.exceptions.KittenNotFoundException;

import java.util.List;

public class KittenDao implements Dao<Kitten> {
    private final EntityManager em;

    public KittenDao(EntityManager em) {
        this.em = em;
    }

    @Override
    public Kitten save(Kitten entity) throws DatabaseException {
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
    public void deleteById(long id) throws DatabaseException, KittenNotFoundException {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            Kitten kitten = em.find(Kitten.class, id);
            if (kitten != null) {
                em.remove(kitten);
            } else {
                throw new KittenNotFoundException();
            }
            transaction.commit();

        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            if (e instanceof KittenNotFoundException) {
                throw e;
            } else {
                throw new DatabaseException("deletion", e.getMessage());
            }
        }
    }

    @Override
    public void deleteByEntity(Kitten entity) throws DatabaseException, KittenNotFoundException {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            Kitten kitten = em.find(Kitten.class, entity.getId());
            if (kitten != null) {
                em.remove(kitten);
            } else {
                throw new KittenNotFoundException();
            }
            transaction.commit();

        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            if (e instanceof KittenNotFoundException) {
                throw e;
            } else {
                throw new DatabaseException("deletion", e.getMessage());
            }
        }
    }

    @Override
    public void deleteAll() throws DatabaseException {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.createQuery("DELETE from Kitten").executeUpdate();
            transaction.commit();

        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new DatabaseException("deletion", e.getMessage());
        }
    }

    @Override
    public Kitten update(Kitten entity) throws DatabaseException {
        return save(entity);
    }

    @Override
    public Kitten getById(long id) {
        return em.find(Kitten.class, id);
    }

    @Override
    public List<Kitten> getAll() {
        return em.createQuery("SELECT kittens FROM Kitten kittens", Kitten.class).getResultList();
    }
}
