package ru.kimvlry.kittens.web.service;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.kimvlry.kittens.entities.KittenBreed;
import ru.kimvlry.kittens.entities.KittenCoatColor;

import java.time.LocalDateTime;
import java.util.Set;

public class KittenFilter {

    @Schema(description = "Exact kitten name")
    private String name;

    @Schema(description = "List of acceptable breeds")
    private Set<KittenBreed> breeds;

    @Schema(description = "List of acceptable coat colors")
    private Set<KittenCoatColor> coatColors;

    @Schema(description = "Minimum purr loudness")
    private Integer minPurr;

    @Schema(description = "Maximum purr loudness")
    private Integer maxPurr;

    @Schema(description = "Born after this date")
    private LocalDateTime birthAfter;

    @Schema(description = "Born before this date")
    private LocalDateTime birthBefore;

    @Schema(description = "Owner IDs to match")
    private Set<Long> ownerIds;

    @Schema(description = "Friend kitten IDs to match")
    private Set<Long> friendIds;

    public String getName() {
        return name;
    }

    public Set<KittenBreed> getBreeds() {
        return breeds;
    }

    public Set<KittenCoatColor> getCoatColors() {
        return coatColors;
    }

    public Integer getMinPurr() {
        return minPurr;
    }

    public Integer getMaxPurr() {
        return maxPurr;
    }

    public LocalDateTime getBirthAfter() {
        return birthAfter;
    }

    public LocalDateTime getBirthBefore() {
        return birthBefore;
    }


    public Set<Long> getOwnerIds() {
        return ownerIds;
    }

    public Set<Long> getFriendIds() {
        return friendIds;
    }

    public void setFriendIds(Set<Long> friendIds) {
        this.friendIds = friendIds;
    }

    public void setOwnerIds(Set<Long> ownerIds) {
        this.ownerIds = ownerIds;
    }

    public void setBirthBefore(LocalDateTime birthBefore) {
        this.birthBefore = birthBefore;
    }

    public void setBirthAfter(LocalDateTime birthAfter) {
        this.birthAfter = birthAfter;
    }

    public void setMaxPurr(Integer maxPurr) {
        this.maxPurr = maxPurr;
    }

    public void setMinPurr(Integer minPurr) {
        this.minPurr = minPurr;
    }

    public void setCoatColors(Set<KittenCoatColor> coatColors) {
        this.coatColors = coatColors;
    }

    public void setBreeds(Set<KittenBreed> breeds) {
        this.breeds = breeds;
    }

    public void setName(String name) {
        this.name = name;
    }
}
