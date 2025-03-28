package ru.kimvlry.kittens;

import ru.kimvlry.kittens.dao.KittenDao;
import ru.kimvlry.kittens.dao.OwnerDao;
import ru.kimvlry.kittens.entities.Kitten;
import ru.kimvlry.kittens.entities.Owner;
import ru.kimvlry.kittens.exceptions.DatabaseException;

import java.util.Map;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;



@Testcontainers
public class Tests {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");

    private static EntityManagerFactory emf;
    private EntityManager em;
    private KittenDao kittenDao;
    private OwnerDao ownerDao;

    @BeforeAll
    static void setupDatabase() {
        emf = Persistence.createEntityManagerFactory("meow", Map.of(
                "jakarta.persistence.jdbc.url", postgres.getJdbcUrl(),
                "jakarta.persistence.jdbc.user", postgres.getUsername(),
                "jakarta.persistence.jdbc.password", postgres.getPassword(),
                "hibernate.hbm2ddl.auto", "create-drop"
        ));
    }

    @BeforeEach
    void setup() {
        em = emf.createEntityManager();
        kittenDao = new KittenDao(em);
        ownerDao = new OwnerDao(em);
    }

    @AfterEach
    void cleanup() {
        em.close();
    }

    @AfterAll
    static void tearDown() {
        emf.close();
    }

    @Test
    void testSaveAndFindKitten() throws DatabaseException {
        Owner owner = new Owner();
        owner.setName("cool");

        Kitten kitten = new Kitten();
        kitten.setName("cool_kitten");
        kitten.setOwner(owner);

        kittenDao.save(kitten);
        ownerDao.save(owner);
        Kitten found = kittenDao.getById(kitten.getId());
        Owner found_owner = ownerDao.getById(owner.getId());

        Assertions.assertNotNull(found);
        Assertions.assertEquals("cool_kitten", found.getName());
        Assertions.assertEquals("cool", found.getOwner().getName());
        Assertions.assertEquals("cool", found_owner.getName());
    }
}
