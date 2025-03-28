package ru.kimvlry.kittens.entities;

import java.time.LocalDateTime;
import java.util.Set;
import jakarta.persistence.*;


@Entity
@Table(name = "kittens")
public class Kitten {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "birth_timestamp")
    private LocalDateTime birthTimestamp;

    @Enumerated(EnumType.STRING)
    private KittenBreed breed;

    @Enumerated(EnumType.STRING)
    private KittenCoatColor coatColor;

    @ManyToOne(cascade = CascadeType.ALL)
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

    public LocalDateTime getBirthDateTime() {
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

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBirthTimestamp(LocalDateTime birthTimestamp) {
        this.birthTimestamp = birthTimestamp;
    }

    public void setBreed(KittenBreed breed) {
        this.breed = breed;
    }

    public void setCoatColor(KittenCoatColor coatColor) {
        this.coatColor = coatColor;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public void setFriends(Set<Kitten> friends) {
        this.friends = friends;
    }
}
