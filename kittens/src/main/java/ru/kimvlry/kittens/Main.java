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
    }
}
