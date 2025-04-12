package ru.kimvlry.kittens;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.flywaydb.core.Flyway;
import ru.kimvlry.kittens.dao.KittenDao;
import ru.kimvlry.kittens.dao.OwnerDao;
import ru.kimvlry.kittens.entities.Kitten;
import ru.kimvlry.kittens.entities.KittenBreed;
import ru.kimvlry.kittens.entities.KittenCoatColor;
import ru.kimvlry.kittens.entities.Owner;
import ru.kimvlry.kittens.exceptions.DatabaseException;
import ru.kimvlry.kittens.exceptions.InvalidInstanceException;
import ru.kimvlry.kittens.services.KittenService;
import ru.kimvlry.kittens.services.OwnerService;

import java.time.LocalDateTime;
import java.util.HashSet;

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
            Owner sabrina = new Owner();
            sabrina.setName("Sabrina");
            sabrina.setBirthTimestamp(LocalDateTime.now());

            Owner jon = new Owner();
            jon.setName("Jon");
            jon.setBirthTimestamp(LocalDateTime.now());

            Kitten salem = new Kitten();
            salem.setName("Salem");
            salem.setBirthTimestamp(LocalDateTime.now());
            salem.setBreed(KittenBreed.SIBERIAN);
            salem.setCoatColor(KittenCoatColor.CAPPUCCINO);
            salem.setPurrLoudnessRate(8);
            salem.setOwner(sabrina);

            Kitten garfield = new Kitten();
            garfield.setName("Garfield");
            garfield.setBirthTimestamp(LocalDateTime.now());
            garfield.setBreed(KittenBreed.MAINE_COON);
            garfield.setCoatColor(KittenCoatColor.LATTE);
            garfield.setPurrLoudnessRate(1);
            garfield.setOwner(sabrina);

            Kitten pumpkin = new Kitten();
            pumpkin.setName("Pumpkin");
            pumpkin.setBirthTimestamp(LocalDateTime.now());
            pumpkin.setBreed(KittenBreed.BRITISH_SHORTHAIR);
            pumpkin.setCoatColor(KittenCoatColor.LATTE);
            pumpkin.setPurrLoudnessRate(5);
            pumpkin.setOwner(jon);

            sabrina.setOwnedKittens(new HashSet<>());
            sabrina.getOwnedKittens().add(salem);
            sabrina.getOwnedKittens().add(garfield);

            jon.setOwnedKittens(new HashSet<>());
            jon.getOwnedKittens().add(pumpkin);

            ownerService.addOwner(sabrina);
            ownerService.addOwner(jon);

            salem.getFriends().add(garfield);
            garfield.getFriends().add(salem);

            garfield.getFriends().add(pumpkin);
            pumpkin.getFriends().add(garfield);

            kittenService.update(salem);
            kittenService.update(garfield);
            kittenService.update(pumpkin);

        } finally {
            em.close();
            emf.close();
        }
    }
}
