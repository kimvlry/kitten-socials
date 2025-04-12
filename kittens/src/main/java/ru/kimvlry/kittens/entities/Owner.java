package ru.kimvlry.kittens.entities;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.*;

@Entity
@Table(name = "owners")
public class Owner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "birth_timestamp")
    private LocalDateTime birthTimestamp;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Kitten> ownedKittens;

    public Set<Kitten> getOwnedKittens() {
        return ownedKittens;
    }

    public LocalDateTime getBirthTimestamp() {
        return birthTimestamp;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public void setOwnedKittens(Set<Kitten> ownedKittens) {
        this.ownedKittens = new HashSet<>(ownedKittens);
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
}
