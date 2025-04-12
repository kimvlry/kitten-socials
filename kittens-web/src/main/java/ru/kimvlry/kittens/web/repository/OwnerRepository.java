package ru.kimvlry.kittens.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kimvlry.kittens.entities.Owner;

public interface OwnerRepository extends JpaRepository<Owner, Long> {
}
