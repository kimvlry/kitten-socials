package ru.kimvlry.kittens.web.service;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.kimvlry.kittens.entities.Kitten;

import java.time.LocalDateTime;
import java.util.Set;

public class OwnerFilter {
    @Schema(description = "Exact owner name")
    private String name;

    @Schema(description = "Born after this date")
    private LocalDateTime birthAfter;

    @Schema(description = "Born before this date")
    private LocalDateTime birthBefore;

    @Schema(description = "Owned kittens' IDs to match")
    private Set<Long> ownedKittensIds;

    public Set<Long> getOwnedKittensIds() {
        return ownedKittensIds;
    }

    public LocalDateTime getBirthBefore() {
        return birthBefore;
    }

    public LocalDateTime getBirthAfter() {
        return birthAfter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBirthAfter(LocalDateTime birthAfter) {
        this.birthAfter = birthAfter;
    }

    public void setBirthBefore(LocalDateTime birthBefore) {
        this.birthBefore = birthBefore;
    }

    public void setOwnedKittensIds(Set<Long> ownedKittensIds) {
        this.ownedKittensIds = ownedKittensIds;
    }
}
