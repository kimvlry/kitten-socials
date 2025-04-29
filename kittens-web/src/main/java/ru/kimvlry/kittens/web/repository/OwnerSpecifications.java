package ru.kimvlry.kittens.web.repository;

import org.springframework.data.jpa.domain.Specification;
import ru.kimvlry.kittens.entities.Kitten;
import ru.kimvlry.kittens.entities.Owner;

import java.time.LocalDateTime;
import java.util.Set;

public class OwnerSpecifications {
    public static Specification<Owner> hasName(String name) {
        return ((root, query, criteriaBuilder) ->
                name == null ? null : criteriaBuilder.equal(root.get("name"), name));
    }

    public static Specification<Owner> bornBetween(LocalDateTime from, LocalDateTime to) {
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

    public static Specification<Owner> hasKittensIn(Set<Long> kittenIds) {
        return (root, query, criteriaBuilder) ->
                kittenIds == null || kittenIds.isEmpty() ? null :
                        root.join("ownedKittens").get("id").in(kittenIds);
    }
}
