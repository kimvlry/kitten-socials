package ru.kimvlry.kittens;

import ru.kimvlry.kittens.dao.KittenDao;
import ru.kimvlry.kittens.dao.OwnerDao;
import ru.kimvlry.kittens.entities.*;
import ru.kimvlry.kittens.exceptions.DatabaseException;
import ru.kimvlry.kittens.exceptions.InvalidInstanceException;
import ru.kimvlry.kittens.services.KittenService;
import ru.kimvlry.kittens.services.OwnerService;

import java.time.LocalDateTime;
import java.util.Set;
import org.flywaydb.core.Flyway;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;


public class Main {
    public static void main(String[] args) throws InvalidInstanceException, DatabaseException {
        String jdbcUrl = "jdbc:postgresql://localhost:5432/kittens_demo";
        String username = "postgres";
        String password = "postgres";

        Flyway flyway = Flyway.configure()
                .dataSource(jdbcUrl, username, password)
                .load();
        flyway.migrate();

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("kittens_demo");
        EntityManager em = emf.createEntityManager();

        OwnerService ownerService = new OwnerService(new OwnerDao(em));
        KittenService kittenService = new KittenService(new KittenDao(em));

        try {
            demoCRUD(ownerService, kittenService);
            demoFriendship(kittenService);
        } finally {
            em.close();
            emf.close();
        }
    }

    private static void demoCRUD(OwnerService ownerService, KittenService kittenService) throws InvalidInstanceException, DatabaseException {
        System.out.println("\nCRUD");

        Owner owner = new Owner();
        owner.setName("Owner");
        owner.setBirthTimestamp(LocalDateTime.now());

        Kitten kitten = new Kitten();
        kitten.setName("Kitty");
        kitten.setBirthTimestamp(LocalDateTime.now());
        kitten.setBreed(KittenBreed.SIBERIAN);

        owner.setOwnedKittens(Set.of(kitten));
        kitten.setOwner(owner);

        Owner savedOwner = ownerService.addOwner(owner);
        System.out.println("Saved owner: " + savedOwner.getName() + " with " +
                savedOwner.getOwnedKittens().size() + " kittens");

        savedOwner.setName("Cooler Owner");
        ownerService.update(savedOwner);
        System.out.println("Updated owner name: " + ownerService.getById(savedOwner.getId()).getName());
    }

    private static void demoFriendship(KittenService kittenService) throws InvalidInstanceException, DatabaseException {
        System.out.println("\nfriendship");

        Kitten cat1 = new Kitten();
        cat1.setName("Kit");
        cat1.setBirthTimestamp(LocalDateTime.now());
        cat1.setBreed(KittenBreed.BRITISH_SHORTHAIR);

        Kitten cat2 = new Kitten();
        cat2.setName("Kat");
        cat2.setBirthTimestamp(LocalDateTime.now());
        cat2.setBreed(KittenBreed.MAINE_COON);

        cat1.getFriends().add(cat2);
        cat2.getFriends().add(cat1);

        Kitten savedCat1 = kittenService.addKitten(cat1);
        Kitten savedCat2 = kittenService.addKitten(cat2);

        System.out.println(savedCat1.getName() + " has " +
                savedCat1.getFriends().size() + " friends");
    }
}