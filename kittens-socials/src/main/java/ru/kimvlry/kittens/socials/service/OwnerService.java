package ru.kimvlry.kittens.socials.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.kimvlry.kittens.socials.entities.Kitten;
import ru.kimvlry.kittens.socials.entities.Owner;
import ru.kimvlry.kittens.socials.dto.OwnerDto;
import ru.kimvlry.kittens.socials.dto.mappers.OwnerMapper;
import ru.kimvlry.kittens.socials.repository.KittenRepository;
import ru.kimvlry.kittens.socials.repository.OwnerRepository;
import ru.kimvlry.kittens.socials.repository.specifications.OwnerSpecifications;
import ru.kimvlry.kittens.socials.service.filters.OwnerFilter;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Service
@Validated
public class OwnerService {
    private final OwnerRepository ownerRepository;
    private final KittenRepository kittenRepository;
    private final OwnerMapper ownerMapper;

    public OwnerService(OwnerRepository ownerRepository, KittenRepository kittenRepository, OwnerMapper ownerMapper) {
        this.ownerRepository = ownerRepository;
        this.kittenRepository = kittenRepository;
        this.ownerMapper = ownerMapper;
    }

    public OwnerDto getOwnerById(long id) {
        return ownerRepository.findById(id)
                .map(ownerMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("owner " + id));
    }

    public Page<OwnerDto> getOwnersFiltered(OwnerFilter filter, Pageable pageable) {
        Specification<Owner> spec = Specification.where(null);

        if (filter.getName() != null) {
            spec = spec.and(OwnerSpecifications.hasName(filter.getName()));
        }

        if (filter.getBirthAfter() != null || filter.getBirthBefore() != null) {
            Instant from = filter.getBirthAfter() != null ? filter.getBirthAfter().toInstant() : Instant.MIN;
            Instant to = filter.getBirthBefore() != null ? filter.getBirthBefore().toInstant() : Instant.MAX;
            spec = spec.and(OwnerSpecifications.bornBetween(from, to));
        }

        if (filter.getOwnedKittensIds() != null && !filter.getOwnedKittensIds().isEmpty()) {
            spec = spec.and(OwnerSpecifications.hasKittensIn(filter.getOwnedKittensIds()));
        }

        return ownerRepository.findAll(spec, pageable).map(ownerMapper::toDto);
    }

    private void fillOwnerFromDto(Owner owner, @Valid OwnerDto dto) {
        owner.setName(dto.name());
        owner.setBirthDate(dto.birthDate());

        if (dto.ownedKittensIds() == null || dto.ownedKittensIds().isEmpty()) {
            owner.setOwnedKittens(new HashSet<>());
        }
        else {
            Set<Kitten> kittens = new HashSet<>(kittenRepository.findAllById(dto.ownedKittensIds()));
            owner.setOwnedKittens(kittens);
        }
    }

    @Transactional
    public OwnerDto createOwner(@Valid OwnerDto dto) {
        Owner owner = new Owner();
        fillOwnerFromDto(owner, dto);
        Owner saved = ownerRepository.save(owner);
        return ownerMapper.toDto(saved);
    }

    @Transactional
    public OwnerDto updateOwner(Long id, @Valid OwnerDto dto) {
        Owner owner = ownerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("owner " + id));

        fillOwnerFromDto(owner, dto);
        Owner updated = ownerRepository.save(owner);
        return ownerMapper.toDto(updated);
    }

    @Transactional
    public void deleteOwner(Long id) {
        if (!ownerRepository.existsById(id)) {
            throw new EntityNotFoundException("owner " + id);
        }
        ownerRepository.deleteById(id);
    }
}