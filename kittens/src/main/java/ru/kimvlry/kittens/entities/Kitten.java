package ru.kimvlry.kittens.entities;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "kittens")
public class Kitten {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp birthTimestamp;

    @Column()
    private KittenBreed breed;

    @Column
    private KittenCoatColor coatColor;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    @ManyToMany
    @JoinTable(
            name = "friendship",
            joinColumns = @JoinColumn(name = "kitten1_id"),
            inverseJoinColumns = @JoinColumn(name = "kitten2_id")
    )
    private Set<Kitten> friends;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Timestamp getBirthDateTime() {
        return birthTimestamp;
    }

    public KittenBreed getBreed() {
        return breed;
    }

    public KittenCoatColor getCoatColor() {
        return coatColor;
    }

    public Owner getOwner() {
        return owner;
    }
}
