package ru.kimvlry.kittens.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;


@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User user;

    private Instant expiryTimestamp;

    private boolean revoked = false;
}
