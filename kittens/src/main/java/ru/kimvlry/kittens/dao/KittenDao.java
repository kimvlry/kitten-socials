package ru.kimvlry.kittens.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
            transaction.begin();

            Owner owner = em.find(Owner.class, entity.getOwner().getId());
            if (owner == null) {
                throw new DatabaseException("saving", "Owner not found");
            }

            Kitten managedKitten;
            if (entity.getId() != null) {
                managedKitten = em.merge(entity);
            } else {
                managedKitten = entity;
                em.persist(managedKitten);
            }

            managedKitten.setOwner(owner);
            owner.getOwnedKittens().add(managedKitten);

            Set<Kitten> managedFriends = new HashSet<>();
            for (Kitten friend : entity.getFriends()) {
                Kitten managedFriend = em.find(Kitten.class, friend.getId());
                if (managedFriend != null) {
                    managedFriends.add(managedFriend);
                    managedFriend.getFriends().add(managedKitten);
                }
            }
            managedKitten.setFriends(managedFriends);

            transaction.commit();
            return managedKitten;

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
