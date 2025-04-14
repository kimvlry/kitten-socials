package ru.kimvlry.kittens.web.dto;

import java.time.LocalDateTime;
import java.util.Set;

public record OwnerDto(
        Long id,
        String name,
        LocalDateTime birthTimestamp,
        Set<Long> ownedKittensIds
) {
}
