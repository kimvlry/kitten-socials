package ru.kimvlry.kittens.entities;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Entity
@Table(name = "kittens")
@Getter
@Setter
public class Kitten {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "birth_timestamp")
    private LocalDate birthDate;

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

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    @ManyToMany
    @JoinTable(
            name = "friendship",
            joinColumns = @JoinColumn(name = "kitten1_id"),
            inverseJoinColumns = @JoinColumn(name = "kitten2_id")
    )
    private Set<Kitten> friends = new HashSet<>();
}
