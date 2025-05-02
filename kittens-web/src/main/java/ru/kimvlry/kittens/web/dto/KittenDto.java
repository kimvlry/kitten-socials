package ru.kimvlry.kittens.web.dto;

import ru.kimvlry.kittens.entities.KittenBreed;
import ru.kimvlry.kittens.entities.KittenCoatColor;

import java.time.LocalDate;
import java.util.Set;

public record KittenDto(
        Long id,
        String name,
        LocalDate birthDate,
        KittenBreed breed,
        KittenCoatColor coatColor,
        int purrLoudnessRate,
        Long ownerId,
        Set<Long> friendIds
) {
}