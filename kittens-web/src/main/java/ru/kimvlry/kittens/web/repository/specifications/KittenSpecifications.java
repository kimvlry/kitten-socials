package ru.kimvlry.kittens.web.repository.specifications;

import org.springframework.data.jpa.domain.Specification;
import ru.kimvlry.kittens.entities.Kitten;
import ru.kimvlry.kittens.entities.KittenBreed;
import ru.kimvlry.kittens.entities.KittenCoatColor;

import java.time.Instant;
import java.util.Set;

public class KittenSpecifications {
    public static Specification<Kitten> hasName(String name) {
        return (root, query, criteriaBuilder) ->
                name == null ? null : criteriaBuilder.equal(root.get("name"), name);
    }

    public static Specification<Kitten> hasBreedIn(Set<KittenBreed> breeds) {
        return (root, query, criteriaBuilder) ->
                breeds == null || breeds.isEmpty() ? null : root.get("breed").in(breeds);
    }

    public static Specification<Kitten> hasCoatColorIn(Set<KittenCoatColor> colors) {
        return (root, query, criteriaBuilder) ->
                colors == null || colors.isEmpty() ? null : root.get("coatColor").in(colors);
    }

    public static Specification<Kitten> hasPurrRateBetween(Integer min, Integer max) {
        return (root, query, criteriaBuilder) -> {
            if (min != null && max != null) {
                return criteriaBuilder.between(root.get("purrLoudnessRate"), min, max);
            }
            if (min != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("purrLoudnessRate"), min);
            }
            if (max != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("purrLoudnessRate"), max);
            }
            return null;
        };
    }

    public static Specification<Kitten> bornBetween(Instant from, Instant to) {
        return (root, query, criteriaBuilder) -> {
            if (from != null && to != null) {
                return criteriaBuilder.between(root.get("birthTimestamp"), from, to);
            }
            if (from != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("birthTimestamp"), from);
            }
            if (to != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("birthTimestamp"), to);
            }
            return null;
        };
    }

    public static Specification<Kitten> hasOwnerIn(Set<Long> ownerIds) {
        return (root, query, criteriaBuilder) ->
                ownerIds == null || ownerIds.isEmpty() ? null : root.get("owner").get("id").in(ownerIds);
    }

    public static Specification<Kitten> hasFriends(Set<Long> friendIds) {
        return (root, query, criteriaBuilder) -> {
            if (friendIds == null || friendIds.isEmpty()) {
                return null;
            }

            var friendsJoin = root.join("friends");
            query.distinct(true);

            return friendsJoin.get("id").in(friendIds);
        };
    }
}
