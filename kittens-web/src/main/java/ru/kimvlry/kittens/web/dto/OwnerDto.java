package ru.kimvlry.kittens.web.dto;

import java.time.LocalDate;
import java.util.Set;

public record OwnerDto(
        Long id,
        String name,
        LocalDate birthTimestamp,
        Set<Long> ownedKittensIds
) {
}
