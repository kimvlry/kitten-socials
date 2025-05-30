package ru.kimvlry.kittens.common;

import jakarta.validation.constraints.NotBlank;

import java.util.Date;
import java.util.Set;

public record OwnerDto(
        Long id,
        @NotBlank
        String name,
        Date birthDate,
        Set<Long> ownedKittensIds
) {
}