package ru.kimvlry.kittens;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.*;
import ru.kimvlry.kittens.dao.KittenDao;
import ru.kimvlry.kittens.dao.OwnerDao;
import ru.kimvlry.kittens.entities.*;
import ru.kimvlry.kittens.exceptions.*;
import ru.kimvlry.kittens.services.KittenService;
import ru.kimvlry.kittens.services.OwnerService;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hibernate.Hibernate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class Tests {
    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");

    private static EntityManagerFactory emf;
    private EntityManager em;
    private KittenService kittenService;
    private OwnerService ownerService;

    @BeforeAll
    static void setupAll() {
        postgres.start();

        Flyway.configure()
                .dataSource(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())
                .locations("classpath:db/migration")
                .load()
                .migrate();

        emf = Persistence.createEntityManagerFactory("kittens_tests", Map.of(
                "jakarta.persistence.jdbc.url", postgres.getJdbcUrl(),
                "jakarta.persistence.jdbc.user", postgres.getUsername(),
                "jakarta.persistence.jdbc.password", postgres.getPassword(),
                "hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect",
                "hibernate.hbm2ddl.auto", "validate"
        ));
    }

    @BeforeEach
    void setup() {
        em = emf.createEntityManager();
        kittenService = new KittenService(new KittenDao(em));
        ownerService = new OwnerService(new OwnerDao(em));

        em.getTransaction().begin();
        try {
            em.createQuery("DELETE FROM Kitten").executeUpdate();
            em.createQuery("DELETE FROM Owner").executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Failed to clean database", e);
        }
    }

    @AfterEach
    void cleanup() {
        if (em != null && em.isOpen()) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    @AfterAll
    static void tearDownAll() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
        if (postgres != null && postgres.isRunning()) {
            postgres.stop();
        }
    }


    //  OwnerService Tests

    @Test
    void addOwner_ShouldThrow_WhenNameNull() {
        Owner owner = new Owner();
        Kitten kitten = createTestKitten("Salem", owner);
        owner.setOwnedKittens(Set.of(kitten));
        assertThrows(InvalidInstanceException.class, () -> ownerService.addOwner(owner));
    }

    @Test
    void addOwner_ShouldThrow_WhenNoKittens() {
        Owner owner = createOwnerWithNameOnly("Sabrina");
        assertThrows(InvalidInstanceException.class, () -> ownerService.addOwner(owner));
    }

    @Test
    void addOwner_ShouldSuccess_WhenValidWithKitten() throws Exception {
        Owner owner = createOwnerWithNameOnly("Sabrina");
        Kitten kitten = createTestKitten("Salem", owner);
        owner.setOwnedKittens(Set.of(kitten));

        Owner saved = ownerService.addOwner(owner);
        assertNotNull(saved.getId());
        assertEquals(1, saved.getOwnedKittens().size());
    }

    @Test
    void deleteOwnerById_ShouldSuccess_WhenExists() throws Exception {
        Owner owner = createValidOwnerWithKitten("Kiki", "Jiji");
        ownerService.deleteOwnerById(owner.getId());
        assertNull(ownerService.getById(owner.getId()));
    }

    @Test
    void deleteOwner_ShouldSuccess_WhenExists() throws Exception {
        Owner owner = createValidOwnerWithKitten("Jon", "Garfield");
        ownerService.deleteOwner(owner);
        assertNull(ownerService.getById(owner.getId()));
    }

    @Test
    void deleteAllOwners_ShouldEmptyDatabase() throws Exception {
        Owner owner1 = createValidOwnerWithKitten("Sabrina", "Salem");
        Owner owner2 = createValidOwnerWithKitten("Jon", "Garfield");
        assertEquals(2, ownerService.getAllOwners().size());

        ownerService.deleteAllOwners();
        assertEquals(0, ownerService.getAllOwners().size());
    }

    @Test
    void updateOwner_ShouldChangeName() throws Exception {
        Owner owner = createValidOwnerWithKitten("Karl", "Choupette");

        owner.setName("Karl Lagerfeld");
        Owner updated = ownerService.update(owner);

        em.clear();
        Owner fromDb = ownerService.getById(owner.getId());

        assertEquals("Karl Lagerfeld", fromDb.getName());
        assertEquals(1, fromDb.getOwnedKittens().size());
    }

    @Test
    void updateOwner_ShouldThrow_WhenRemovingAllKittens() throws Exception {
        Owner owner = createValidOwnerWithKitten("Sabrina", "Salem");

        em.clear();
        Owner freshOwner = ownerService.getById(owner.getId());

        Owner updateData = new Owner();
        updateData.setId(freshOwner.getId());
        updateData.setName(freshOwner.getName());
        updateData.setBirthTimestamp(freshOwner.getBirthTimestamp());
        updateData.setOwnedKittens(new HashSet<>());

        assertThrows(InvalidInstanceException.class, () -> ownerService.update(updateData));

        em.clear();
        Owner fromDb = ownerService.getById(owner.getId());
        assertEquals(1, fromDb.getOwnedKittens().size());
    }


    // KittenService Tests

    @Test
    void addKitten_ShouldSuccess_WhenValid() throws Exception {
        Owner owner = createValidOwnerWithKitten("вы продаете рыбов", "нет просто показываем");
        Kitten newKitten = createTestKitten("очень жаль", owner);
        Kitten saved = kittenService.addKitten(newKitten);

        assertNotNull(saved.getId());
        assertEquals(owner.getId(), saved.getOwner().getId());
    }

    @Test
    void addKitten_ShouldThrow_WhenOwnerNotSaved() {
        Owner unsavedOwner = createOwnerWithNameOnly("Chris");
        Kitten kitten = createTestKitten("Nyan Cat", unsavedOwner);

        assertThrows(DatabaseException.class, () -> kittenService.addKitten(kitten));
    }

    @Test
    void addKitten_ShouldThrow_WhenNameNull() throws Exception {
        Owner owner = createValidOwnerWithKitten("Shintarou Tsuji", "Hello Kitty");
        Kitten kitten = createTestKitten(null, owner);

        assertThrows(InvalidInstanceException.class, () -> kittenService.addKitten(kitten));
    }

    @Test
    void deleteKittenById_ShouldSuccess_WhenExists() throws Exception {
        Owner owner = createValidOwnerWithKitten("Ellen Ripley", "Jonesy");
        Kitten kitten = owner.getOwnedKittens().iterator().next();

        kittenService.deleteKittenById(kitten.getId());
        assertNull(kittenService.getById(kitten.getId()));
    }

    @Test
    void deleteKitten_ShouldSuccess_WhenExists() throws Exception {
        Owner owner = createValidOwnerWithKitten("Pspspsps", "Meowmwow");
        Kitten kitten = owner.getOwnedKittens().iterator().next();

        kittenService.deleteKitten(kitten);
        assertNull(kittenService.getById(kitten.getId()));
    }

    @Test
    void deleteAllKittens_ShouldEmptyDatabase() throws Exception {
        Owner owner = createValidOwnerWithKitten("Owner", "Purr");
        kittenService.addKitten(createTestKitten("Purrrrr", owner));

        kittenService.deleteAllKittens();
        assertEquals(0, kittenService.getAllKittens().size());
    }

    @Test
    void updateKitten_ShouldChangeName() throws Exception {
        Owner owner = createValidOwnerWithKitten("Owner", "Meow");
        Kitten kitten = owner.getOwnedKittens().iterator().next();
        kitten.setName("Gav");

        Kitten updated = kittenService.update(kitten);
        assertEquals("Gav", updated.getName());
    }

    @Test
    void getAllKittens_ShouldReturnAll() throws Exception {
        Owner owner1 = createValidOwnerWithKitten("Owner", "Kotik");
        Owner owner2 = createValidOwnerWithKitten("Renwo", "Kitok");

        assertEquals(2, kittenService.getAllKittens().size());
    }


    // Complex Scenarios

    @Test
    void testOwnerWithMultipleKittens() throws Exception {
        Owner owner = createOwnerWithNameOnly("Owner");
        Kitten kitten1 = createTestKitten("Kotik", owner);
        Kitten kitten2 = createTestKitten("Kitik", owner);
        owner.setOwnedKittens(Set.of(kitten1, kitten2));

        Owner savedOwner = ownerService.addOwner(owner);
        assertEquals(2, savedOwner.getOwnedKittens().size());
    }

    @Test
    void testKittenFriendship() throws Exception {
        Owner owner = createValidOwnerWithKitten("Owner", "Kitik");
        Kitten cat1 = owner.getOwnedKittens().iterator().next();
        Kitten cat2 = createTestKitten("Kotik", owner);
        kittenService.addKitten(cat2);

        kittenService.addFriendship(cat1, cat2);

        Kitten updated = kittenService.getById(cat1.getId());
        assertEquals(1, updated.getFriends().size());

        Kitten other = kittenService.getById(cat2.getId());
        assertEquals(1, other.getFriends().size());
        assertEquals(cat1.getId(), other.getFriends().iterator().next().getId());
    }

    @Test
    void testTransferKittenToNewOwner() throws Exception {
        Owner oldOwner = createValidOwnerWithKitten("cool", "meow");
        Owner newOwner = createValidOwnerWithKitten("supercool", "gav");

        Kitten catToTransfer = oldOwner.getOwnedKittens().iterator().next();
        Long kittenId = catToTransfer.getId();

        assertNotNull(kittenService.getById(kittenId));
        assertEquals(1, ownerService.getById(oldOwner.getId()).getOwnedKittens().size());
        assertEquals(1, ownerService.getById(newOwner.getId()).getOwnedKittens().size());

        Kitten updateData = new Kitten();
        updateData.setId(kittenId);
        updateData.setName(catToTransfer.getName());
        updateData.setBirthTimestamp(catToTransfer.getBirthDateTime());
        updateData.setBreed(catToTransfer.getBreed());
        updateData.setCoatColor(catToTransfer.getCoatColor());
        updateData.setOwner(newOwner);

        Kitten updatedKitten = kittenService.update(updateData);
        assertNotNull(updatedKitten);

        em.clear();

        Kitten persistedKitten = kittenService.getById(kittenId);
        assertNotNull(persistedKitten);
        assertEquals(newOwner.getId(), persistedKitten.getOwner().getId());

        Owner persistedOldOwner = ownerService.getById(oldOwner.getId());
        Owner persistedNewOwner = ownerService.getById(newOwner.getId());

        Hibernate.initialize(persistedOldOwner.getOwnedKittens());
        Hibernate.initialize(persistedNewOwner.getOwnedKittens());

        assertEquals(0, persistedOldOwner.getOwnedKittens().size());
        assertEquals(2, persistedNewOwner.getOwnedKittens().size());
    }


    @Test
    void testCannotCreateOrphanKitten() {
        Owner owner = createOwnerWithNameOnly("Pspspspsp");
        Kitten kitten = createTestKitten("Mewemewmew", owner);

        assertThrows(DatabaseException.class, () -> kittenService.addKitten(kitten));
    }


    // Helper Methods

    private Owner createOwnerWithNameOnly(String name) {
        Owner owner = new Owner();
        owner.setName(name);
        owner.setBirthTimestamp(LocalDateTime.now());
        return owner;
    }

    private Owner createValidOwnerWithKitten(String ownerName, String kittenName) throws Exception {
        Owner owner = new Owner();
        owner.setName(ownerName);
        owner.setBirthTimestamp(LocalDateTime.now());

        Kitten kitten = new Kitten();
        kitten.setName(kittenName);
        kitten.setBirthTimestamp(LocalDateTime.now());
        kitten.setBreed(KittenBreed.BRITISH_SHORTHAIR);
        kitten.setCoatColor(KittenCoatColor.CAPPUCCINO);

        owner.setOwnedKittens(new HashSet<>());
        owner.getOwnedKittens().add(kitten);
        kitten.setOwner(owner);

        em.getTransaction().begin();
        try {
            em.persist(owner);
            em.persist(kitten);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
        return owner;
    }

    private Kitten createTestKitten(String name, Owner owner) {
        Kitten kitten = new Kitten();
        kitten.setName(name);
        kitten.setBirthTimestamp(LocalDateTime.now());
        kitten.setBreed(KittenBreed.BRITISH_SHORTHAIR);
        kitten.setCoatColor(KittenCoatColor.LATTE);
        kitten.setOwner(owner);
        return kitten;
    }
}