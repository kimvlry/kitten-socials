package ru.kimvlry.kittens.entities;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

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
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "breed", columnDefinition = "kitten_breed")
    private KittenBreed breed;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "coat_color", columnDefinition = "kitten_coat_color")
    private KittenCoatColor coatColor;

    @Column(name = "purr_loudness_rate")
    private int purrLoudnessRate;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    @ManyToMany
    @JoinTable(
            name = "friendship",
            joinColumns = @JoinColumn(name = "kitten1_id"),
            inverseJoinColumns = @JoinColumn(name = "kitten2_id")
    )
    private Set<Kitten> friends = new HashSet<>();

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

    public int getPurrLoudnessRate() {
        return purrLoudnessRate;
    }

    public Owner getOwner() {
        return owner;
    }

    public Set<Kitten> getFriends() {
        return friends;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime setBirthTimestamp(LocalDateTime birthTimestamp) {
        this.birthTimestamp = birthTimestamp;
        return birthTimestamp;
    }

    public void setBreed(KittenBreed breed) {
        this.breed = breed;
    }

    public void setCoatColor(KittenCoatColor coatColor) {
        this.coatColor = coatColor;
    }

    public void setPurrLoudnessRate(int purrLoudness) {
        this.purrLoudnessRate = purrLoudness;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public void setFriends(Set<Kitten> friends) {
        this.friends = new HashSet<>(friends);
    }
}
