package ru.kimvlry.kittens.entities;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.Set;

@Entity
@Table(name = "owners")
public class Owner {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private Timestamp birthTimestamp;

    @OneToMany
    @JoinColumn(name = "owned_kitten_id", nullable = false)
    private Set<Kitten> OwnedKittens;

    public Set<Kitten> getOwnedKittens() {
        return OwnedKittens;
    }

    public Timestamp getBirthTimestamp() {
        return birthTimestamp;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public void setOwnedKittens(Set<Kitten> ownedKittens) {
        OwnedKittens = ownedKittens;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBirthTimestamp(Timestamp birthTimestamp) {
        this.birthTimestamp = birthTimestamp;
    }
}
