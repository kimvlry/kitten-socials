package ru.kimvlry.kittens.web.service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.kimvlry.kittens.entities.Kitten;
import ru.kimvlry.kittens.entities.KittenBreed;
import ru.kimvlry.kittens.entities.KittenCoatColor;
import ru.kimvlry.kittens.web.dto.KittenDto;
import ru.kimvlry.kittens.web.dto.KittenMapper;
import ru.kimvlry.kittens.web.repository.KittenRepository;
import ru.kimvlry.kittens.web.repository.KittenSpecifications;

import java.time.LocalDateTime;
import java.util.Set;

@Service
public class KittenService {

    private final KittenRepository kittenRepository;
    private final KittenMapper kittenMapper;

    public KittenService(KittenRepository kittenRepository, KittenMapper kittenMapper) {
        this.kittenRepository = kittenRepository;
        this.kittenMapper = kittenMapper;
    }

    public KittenDto getKittenById(Long id) {
        return kittenRepository.findById(id)
                .map(kittenMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Kitten not found"));
    }

    public Page<KittenDto> getKittensFiltered(
            String name,
            Set<KittenBreed> breeds,
            Set<KittenCoatColor> coatColors,
            Integer minPurr,
            Integer maxPurr,
            LocalDateTime birthAfter,
            LocalDateTime birthBefore,
            Set<Long> ownerIds,
            Set<Long> friendIds,
            Pageable pageable
    ) {
        Specification<Kitten> spec = Specification.where(null);

        if (name != null) {
            spec = spec.and(KittenSpecifications.hasName(name));
        }

        if (breeds != null && !breeds.isEmpty()) {
            spec = spec.and(KittenSpecifications.hasBreedIn(breeds));
        }

        if (coatColors != null && !coatColors.isEmpty()) {
            spec = spec.and(KittenSpecifications.hasCoatColorIn(coatColors));
        }

        if (minPurr != null) {
            spec = spec.and(KittenSpecifications.hasPurrRateBetween(minPurr, 10));
        }

        if (maxPurr != null) {
            spec = spec.and(KittenSpecifications.hasPurrRateBetween(0, maxPurr));
        }

        if (birthAfter != null) {
            spec = spec.and(KittenSpecifications.bornBetween(birthAfter, LocalDateTime.MAX));
        }

        if (birthBefore != null) {
            spec = spec.and(KittenSpecifications.bornBetween(LocalDateTime.MIN, birthBefore));
        }

        if (ownerIds != null && !ownerIds.isEmpty()) {
            spec = spec.and(KittenSpecifications.hasOwnerIn(ownerIds));
        }

        if (friendIds != null && !friendIds.isEmpty()) {
            spec = spec.and(KittenSpecifications.hasFriends(friendIds));
        }

        return kittenRepository.findAll(spec, pageable)
                .map(kittenMapper::toDto);
    }
}
