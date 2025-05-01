package ru.kimvlry.kittens.web.service;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.kimvlry.kittens.entities.Kitten;
import ru.kimvlry.kittens.web.dto.KittenDto;
import ru.kimvlry.kittens.web.dto.mappers.KittenMapper;
import ru.kimvlry.kittens.web.exception.NonNullableException;
import ru.kimvlry.kittens.web.exception.EntityNotFoundException;
import ru.kimvlry.kittens.web.repository.KittenRepository;
import ru.kimvlry.kittens.web.repository.specifications.KittenSpecifications;
import ru.kimvlry.kittens.web.repository.OwnerRepository;
import ru.kimvlry.kittens.web.service.filters.KittenFilter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
public class KittenService {

    private final KittenRepository kittenRepository;
    private final OwnerRepository ownerRepository;
    private final KittenMapper kittenMapper;

    public KittenService(KittenRepository kittenRepository, OwnerRepository ownerRepository, KittenMapper kittenMapper) {
        this.kittenRepository = kittenRepository;
        this.ownerRepository = ownerRepository;
        this.kittenMapper = kittenMapper;
    }

    public KittenDto getKittenById(Long id) {
        return kittenRepository.findById(id)
                .map(kittenMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("kitten", id));
    }

    public Page<KittenDto> getKittensFiltered(KittenFilter filter, Pageable pageable) {
        Specification<Kitten> spec = Specification.where(null);

        if (filter.getName() != null) {
            spec = spec.and(KittenSpecifications.hasName(filter.getName()));
        }

        if (filter.getBreeds() != null && !filter.getBreeds().isEmpty()) {
            spec = spec.and(KittenSpecifications.hasBreedIn(filter.getBreeds()));
        }

        if (filter.getCoatColors() != null && !filter.getCoatColors().isEmpty()) {
            spec = spec.and(KittenSpecifications.hasCoatColorIn(filter.getCoatColors()));
        }

        if (filter.getMinPurr() != null || filter.getMaxPurr() != null) {
            int min = filter.getMinPurr() != null ? filter.getMinPurr() : 0;
            int max = filter.getMaxPurr() != null ? filter.getMaxPurr() : 10;
            spec = spec.and(KittenSpecifications.hasPurrRateBetween(min, max));
        }

        if (filter.getBirthAfter() != null || filter.getBirthBefore() != null) {
            LocalDateTime from = filter.getBirthAfter() != null ? filter.getBirthAfter() : LocalDateTime.MIN;
            LocalDateTime to = filter.getBirthBefore() != null ? filter.getBirthBefore() : LocalDateTime.MAX;
            spec = spec.and(KittenSpecifications.bornBetween(from, to));
        }

        if (filter.getOwnerIds() != null && !filter.getOwnerIds().isEmpty()) {
            spec = spec.and(KittenSpecifications.hasOwnerIn(filter.getOwnerIds()));
        }

        if (filter.getFriendIds() != null && !filter.getFriendIds().isEmpty()) {
            spec = spec.and(KittenSpecifications.hasFriends(filter.getFriendIds()));
        }

        return kittenRepository.findAll(spec, pageable).map(kittenMapper::toDto);
    }

    private void fillKittenFromDto(Kitten kitten, KittenDto dto) {
        kitten.setName(dto.name());
        kitten.setBirthTimestamp(dto.birthTimestamp());
        kitten.setBreed(dto.breed());
        kitten.setCoatColor(dto.coatColor());
        kitten.setPurrLoudnessRate(dto.purrLoudnessRate());

        if (dto.ownerId() == null) {
            throw new NonNullableException("owner");
        }
        else {
            kitten.setOwner(ownerRepository.findById(dto.ownerId())
                    .orElseThrow(() -> new EntityNotFoundException("owner", dto.ownerId())));
        }

        if (dto.friendIds() == null || dto.friendIds().isEmpty()) {
            kitten.setFriends(null);
        }
        else {
            Set<Kitten> friends = new HashSet<>(kittenRepository.findAllById(dto.friendIds()));
            kitten.setFriends(friends);
        }
    }

    @Transactional
    public KittenDto updateKitten(Long id, KittenDto dto) {
        Kitten kitten = kittenRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("kitten", id));

        fillKittenFromDto(kitten, dto);
        Kitten updated = kittenRepository.save(kitten);
        return kittenMapper.toDto(updated);
    }

    @Transactional
    public KittenDto createKitten(KittenDto dto) {
        Kitten kitten = new Kitten();

        fillKittenFromDto(kitten, dto);
        Kitten saved = kittenRepository.save(kitten);
        return kittenMapper.toDto(saved);
    }

    @Transactional
    public void deleteKitten(Long id) {
        if (!kittenRepository.existsById(id)) {
            throw new EntityNotFoundException("kitten", id);
        }
        kittenRepository.deleteById(id);
    }
}
