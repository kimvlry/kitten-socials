package ru.kimvlry.kittens.socials.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.kimvlry.kittens.socials.entities.Kitten;

public interface KittenRepository extends JpaRepository<Kitten, Long>, JpaSpecificationExecutor<Kitten> {
}