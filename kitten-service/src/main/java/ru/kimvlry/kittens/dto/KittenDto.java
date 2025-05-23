package ru.kimvlry.kittens.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.kimvlry.kittens.entities.KittenBreed;
import ru.kimvlry.kittens.entities.KittenCoatColor;

import java.time.Instant;
import java.util.Set;

public record KittenDto(
        Long id,
        @NotBlank
        String name,
        Instant birthDate,
        KittenBreed breed,
        KittenCoatColor coatColor,
        int purrLoudnessRate,
        @NotNull
        Long ownerId,
        Set<Long> friendIds
) {
}