package ru.kimvlry.kittens.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.kimvlry.kittens.entities.Owner;
import ru.kimvlry.kittens.dto.OwnerDto;
import ru.kimvlry.kittens.dto.OwnerMapper;
import ru.kimvlry.kittens.repository.OwnerRepository;
import ru.kimvlry.kittens.repository.OwnerSpecifications;

import java.time.Instant;

@Service
public class OwnerService {
    private final OwnerRepository ownerRepository;
    private final OwnerMapper ownerMapper;

    public OwnerService(OwnerRepository ownerRepository, OwnerMapper ownerMapper) {
        this.ownerRepository = ownerRepository;
        this.ownerMapper = ownerMapper;
    }

    public Page<OwnerDto> getAllOwners(Pageable pageable) {
        Page<Owner> found = ownerRepository.findAll(pageable);
        if (found.isEmpty()) {
            throw new EntityNotFoundException();
        }
        return found.map(ownerMapper::toDto);
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