package ru.kimvlry.kittens.web.dto;

import ru.kimvlry.kittens.entities.KittenBreed;
import ru.kimvlry.kittens.entities.KittenCoatColor;

import java.time.LocalDateTime;
import java.util.List;

public record KittenDto(
        Long id,
        String name,
        LocalDateTime birthTimestamp,
        KittenBreed breed,
        KittenCoatColor coatColor,
        int purrLoudnessRate,
        Long ownerId,
        List<Long> friendIds
) {}

