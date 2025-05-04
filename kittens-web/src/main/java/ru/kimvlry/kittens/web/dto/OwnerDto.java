package ru.kimvlry.kittens.web.dto;

import java.util.Date;
import java.util.Set;

public record OwnerDto(
        Long id,
        String name,
        Date birthDate,
        Set<Long> ownedKittensIds
) {
}
