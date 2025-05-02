package ru.kimvlry.kittens.web.security.role;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.kimvlry.kittens.web.security.user.User;

import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "roles")
@Getter
@Setter
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();
}