package ru.kimvlry.kittens.socials.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.kimvlry.kittens.socials.entities.KittenBreed;
import ru.kimvlry.kittens.socials.entities.KittenCoatColor;
import ru.kimvlry.kittens.socials.dto.KittenDto;
import ru.kimvlry.kittens.socials.security.utils.annotation.ValidationUtils;
import ru.kimvlry.kittens.socials.service.filters.KittenFilter;
import ru.kimvlry.kittens.socials.service.KittenService;

import java.time.Instant;
import java.util.Set;

@Slf4j
@Tag(name = "Kittens", description = "Endpoints for kitten catalog search and management")
@RestController
@RequestMapping("/kittens")
public class KittenController {

    private final KittenService kittenService;
    private final ValidationUtils validationUtils;

    public KittenController(KittenService kittenService, ValidationUtils validationUtils) {
        this.kittenService = kittenService;
        this.validationUtils = validationUtils;
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all existing kittens")
    @GetMapping
    public Page<KittenDto> getKittens(@ParameterObject Pageable pageable) {
        return kittenService.getAllKittens(pageable);
    }

    @PermitAll
    @GetMapping("/ping")
    public String ping() {
        return "Hello from kittens!";
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get kitten by ID")
    @GetMapping("/{id}")
    public KittenDto getKittenById(@PathVariable Long id) {
        return kittenService.getKittenById(id);
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Search kittens with filter")
    @GetMapping("/search")
    public Page<KittenDto> searchKittens(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Set<KittenBreed> breeds,
            @RequestParam(required = false) Set<KittenCoatColor> coatColor,
            @RequestParam(required = false) Integer minPurr,
            @RequestParam(required = false) Integer maxPurr,
            @RequestParam(required = false) Instant birthAfter,
            @RequestParam(required = false) Instant birthBefore,
            @RequestParam(required = false) Set<Long> ownerIds,
            @RequestParam(required = false) Set<Long> friendIds,
            @ParameterObject Pageable pageable
    ) {
        KittenFilter filter = new KittenFilter();
        filter.setName(name);
        filter.setBreeds(breeds);
        filter.setCoatColors(coatColor);
        filter.setMinPurr(minPurr);
        filter.setMaxPurr(maxPurr);
        filter.setBirthAfter(birthAfter);
        filter.setBirthBefore(birthBefore);
        filter.setOwnerIds(ownerIds);
        filter.setFriendIds(friendIds);

        return kittenService.getKittensFiltered(filter, pageable);
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Create a new kitten")
    @PostMapping
    public KittenDto createKitten(@Valid @RequestBody KittenDto dto) {
        validationUtils.validateOwnerAssignment(dto);
        return kittenService.createKitten(dto);
    }

    @PreAuthorize("@validationUtils.isKittenOwner(authentication.name, #id)")
    @Operation(summary = "Update an existing kitten")
    @PutMapping("/{id}")
    public KittenDto updateKitten(@PathVariable Long id, @Valid @RequestBody KittenDto dto) {
        validationUtils.validateOwnerAssignment(dto);
        return kittenService.updateKitten(id, dto);
    }

    @PreAuthorize("@validationUtils.isKittenOwner(authentication.name, #id)")
    @Operation(summary = "Delete a kitten by ID")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteKitten(@PathVariable Long id) {
        kittenService.deleteKitten(id);
    }
}
