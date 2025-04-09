package ru.kimvlry.kittens;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.flywaydb.core.Flyway;
import ru.kimvlry.kittens.dao.KittenDao;
import ru.kimvlry.kittens.dao.OwnerDao;
import ru.kimvlry.kittens.entities.Kitten;
import ru.kimvlry.kittens.entities.KittenBreed;
import ru.kimvlry.kittens.entities.Owner;
import ru.kimvlry.kittens.exceptions.DatabaseException;
import ru.kimvlry.kittens.exceptions.InvalidInstanceException;
import ru.kimvlry.kittens.services.KittenService;
import ru.kimvlry.kittens.services.OwnerService;

import java.time.LocalDateTime;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws InvalidInstanceException, DatabaseException {

        String jdbcUrl = "jdbc:postgresql://localhost:5432/kittens_demo";
        String username = "postgres";
        String password = "postgres";

        Flyway flyway = Flyway.configure()
                .dataSource(jdbcUrl, username, password)
                .cleanDisabled(false)
                .load();

        flyway.clean();
        flyway.migrate();

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("kittens_demo");
        EntityManager em = emf.createEntityManager();

        OwnerService ownerService = new OwnerService(new OwnerDao(em));
        KittenService kittenService = new KittenService(new KittenDao(em));

        try {
            Owner alice = new Owner();
            alice.setName("Alice");
            alice.setBirthTimestamp(LocalDateTime.now());

            Owner bob = new Owner();
            bob.setName("Bob");
            bob.setBirthTimestamp(LocalDateTime.now());

            Kitten whiskers = new Kitten();
            whiskers.setName("Whiskers");
            whiskers.setBirthTimestamp(LocalDateTime.now());
            whiskers.setBreed(KittenBreed.SIBERIAN);
            whiskers.setOwner(alice);
            alice.setOwnedKittens(Set.of(whiskers));

            Kitten muffin = new Kitten();
            muffin.setName("Muffin");
            muffin.setBirthTimestamp(LocalDateTime.now());
            muffin.setBreed(KittenBreed.MAINE_COON);
            muffin.setOwner(bob);
            bob.setOwnedKittens(Set.of(muffin));

            ownerService.addOwner(alice);
            ownerService.addOwner(bob);

            whiskers.getFriends().add(muffin);
            muffin.getFriends().add(whiskers);
            kittenService.update(whiskers);
            kittenService.update(muffin);

            Kitten fetched = kittenService.getById(whiskers.getId());
            System.out.println(fetched.getName() + " has " + fetched.getFriends().size() + " friends.");

        } finally {
            em.close();
            emf.close();
        }
    }
}
