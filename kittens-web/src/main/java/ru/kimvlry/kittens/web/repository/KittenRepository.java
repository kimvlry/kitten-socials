package ru.kimvlry.kittens.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.kimvlry.kittens.entities.Kitten;

@Repository
public interface KittenRepository extends JpaRepository<Kitten, Long>, JpaSpecificationExecutor<Kitten> {
}