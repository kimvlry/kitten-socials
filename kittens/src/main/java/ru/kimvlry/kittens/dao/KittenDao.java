package ru.kimvlry.kittens.dao;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import ru.kimvlry.kittens.entities.Kitten;
import ru.kimvlry.kittens.entities.Owner;
import ru.kimvlry.kittens.exceptions.DatabaseException;

public class KittenDao implements Dao<Kitten> {
    private final EntityManager em;

    public KittenDao(EntityManager em) {
        this.em = em;
    }

    @Override
    public Kitten save(Kitten entity) throws DatabaseException {
        EntityTransaction transaction = em.getTransaction();
        try {
            Owner owner = em.find(Owner.class, entity.getOwner().getId());
            if (owner == null) {
                throw new DatabaseException("saving", "Owner not found");
            }

            transaction.begin();
            if (entity.getId() != null) {
                Kitten managedKitten = em.merge(entity);

                if (!managedKitten.getOwner().equals(owner)) {
                    managedKitten.getOwner().getOwnedKittens().remove(managedKitten);
                    owner.getOwnedKittens().add(managedKitten);
                    managedKitten.setOwner(owner);
                }
                transaction.commit();
                return managedKitten;
            } else {
                owner.getOwnedKittens().add(entity);
                entity.setOwner(owner);
                em.persist(entity);
                transaction.commit();
                return entity;
            }

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
            Kitten kitten = em.find(Kitten.class, id);
            em.remove(kitten);
            transaction.commit();

        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new DatabaseException("deletion", e.getMessage());
        }
    }

    @Override
    public void deleteByEntity(Kitten entity) throws DatabaseException {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            Kitten kitten = em.find(Kitten.class, entity.getId());
            em.remove(kitten);
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
    public Kitten update(Kitten newEntity) throws DatabaseException {
        return save(newEntity);
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
