package ru.kimvlry.kittens.socials.security.utils.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.kimvlry.kittens.socials.entities.Owner;
import ru.kimvlry.kittens.socials.entities.User;

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
    private User user;

    @OneToOne
    @JoinColumn(name = "owner_id", unique = true)
    private Owner owner;
}