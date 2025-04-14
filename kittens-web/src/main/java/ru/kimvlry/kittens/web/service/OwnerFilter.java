package ru.kimvlry.kittens.web.service;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.kimvlry.kittens.entities.Kitten;

import java.time.LocalDateTime;
import java.util.Set;

public class OwnerFilter {
    @Schema(description = "Exact owner name")
    private String name;

    @Schema(description = "Born after this date")
    private LocalDateTime bornAfter;

    @Schema(description = "Born before this date")
    private LocalDateTime bornBefore;

    @Schema(description = "Owned kittens' IDs to match")
    private Set<Kitten> ownedKittensIds;

    public Set<Kitten> getOwnedKittensIds() {
        return ownedKittensIds;
    }

    public LocalDateTime getBornBefore() {
        return bornBefore;
    }

    public LocalDateTime getBornAfter() {
        return bornAfter;
    }

    public String getName() {
        return name;
    }
}
