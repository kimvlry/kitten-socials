package ru.kimvlry.kittens.web.security.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.kimvlry.kittens.entities.Owner;

@Entity
@Table(name = "user_owner_mapping")
@Getter
@Setter
public class UserOwnerMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private UserDetailsImpl user;

    @OneToOne
    @JoinColumn(name = "owner_id", unique = true)
    private Owner owner;
}