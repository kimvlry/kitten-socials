package ru.kimvlry.kittens.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.kimvlry.kittens.entities.Kitten;

public interface KittenRepository extends JpaRepository<Kitten, Long>, JpaSpecificationExecutor<Kitten> {
}