package ru.kimvlry.kittens.web.repository;

import org.springframework.data.jpa.domain.Specification;
import ru.kimvlry.kittens.entities.Kitten;
import ru.kimvlry.kittens.entities.KittenBreed;
import ru.kimvlry.kittens.entities.KittenCoatColor;

import java.time.LocalDateTime;
import java.util.List;

public class KittenSpecifications {
    public static Specification<Kitten> hasName(String name) {
        return (root, query, cb) ->
                name == null ? null : cb.equal(root.get("name"), name);
    }

    public static Specification<Kitten> hasBreeds(List<KittenBreed> breeds) {
        return (root, query, cb) ->
                breeds == null || breeds.isEmpty() ? null : root.get("breed").in(breeds);
    }

    public static Specification<Kitten> hasCoatColors(List<KittenCoatColor> colors) {
        return (root, query, cb) ->
                colors == null || colors.isEmpty() ? null : root.get("coatColor").in(colors);
    }

    public static Specification<Kitten> purrRateBetween(Integer min, Integer max) {
        return (root, query, cb) -> {
            if (min != null && max != null) return cb.between(root.get("purrLoudnessRate"), min, max);
            if (min != null) return cb.greaterThanOrEqualTo(root.get("purrLoudnessRate"), min);
            if (max != null) return cb.lessThanOrEqualTo(root.get("purrLoudnessRate"), max);
            return null;
        };
    }

    public static Specification<Kitten> bornBetween(LocalDateTime from, LocalDateTime to) {
        return (root, query, cb) -> {
            if (from != null && to != null) return cb.between(root.get("birthTimestamp"), from, to);
            if (from != null) return cb.greaterThanOrEqualTo(root.get("birthTimestamp"), from);
            if (to != null) return cb.lessThanOrEqualTo(root.get("birthTimestamp"), to);
            return null;
        };
    }

    public static Specification<Kitten> hasOwners(List<Long> ownerIds) {
        return (root, query, cb) ->
                ownerIds == null || ownerIds.isEmpty() ? null : root.get("owner").get("id").in(ownerIds);
    }

    public static Specification<Kitten> hasFriends(List<Long> friendIds) {
        return (root, query, cb) -> {
            if (friendIds == null || friendIds.isEmpty()) {
                return null;
            }

            var friendsJoin = root.join("friends");
            query.distinct(true);

            return friendsJoin.get("id").in(friendIds);
        };
    }
}
