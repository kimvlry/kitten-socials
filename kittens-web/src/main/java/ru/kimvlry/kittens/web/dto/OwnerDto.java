package ru.kimvlry.kittens.web.dto;

import java.time.Instant;
import java.util.Set;

public record OwnerDto(
        Long id,
        String name,
        Instant birthTimestamp,
        Set<Long> ownedKittensIds
) {
}
