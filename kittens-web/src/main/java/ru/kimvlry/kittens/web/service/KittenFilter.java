package ru.kimvlry.kittens.web.service;

import ru.kimvlry.kittens.entities.KittenBreed;
import ru.kimvlry.kittens.entities.KittenCoatColor;

import java.time.LocalDateTime;
import java.util.Set;

public class KittenFilter {
    private String name;
    private Set<KittenBreed> breeds;
    private Set<KittenCoatColor> coatColors;
    private Integer minPurr;
    private Integer maxPurr;
    private LocalDateTime birthAfter;
    private LocalDateTime birthBefore;
    private Set<Long> ownerIds;
    private Set<Long> friendIds;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<KittenBreed> getBreeds() {
        return breeds;
    }

    public void setBreeds(Set<KittenBreed> breeds) {
        this.breeds = breeds;
    }

    public Set<KittenCoatColor> getCoatColors() {
        return coatColors;
    }

    public void setCoatColors(Set<KittenCoatColor> coatColors) {
        this.coatColors = coatColors;
    }

    public Integer getMinPurr() {
        return minPurr;
    }

    public void setMinPurr(Integer minPurr) {
        this.minPurr = minPurr;
    }

    public Integer getMaxPurr() {
        return maxPurr;
    }

    public void setMaxPurr(Integer maxPurr) {
        this.maxPurr = maxPurr;
    }

    public LocalDateTime getBirthAfter() {
        return birthAfter;
    }

    public void setBirthAfter(LocalDateTime birthAfter) {
        this.birthAfter = birthAfter;
    }

    public LocalDateTime getBirthBefore() {
        return birthBefore;
    }

    public void setBirthBefore(LocalDateTime birthBefore) {
        this.birthBefore = birthBefore;
    }

    public Set<Long> getOwnerIds() {
        return ownerIds;
    }

    public void setOwnerIds(Set<Long> ownerIds) {
        this.ownerIds = ownerIds;
    }

    public Set<Long> getFriendIds() {
        return friendIds;
    }

    public void setFriendIds(Set<Long> friendIds) {
        this.friendIds = friendIds;
    }
}
