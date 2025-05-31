package ru.kimvlry.kittens.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.kimvlry.kittens.dto.OwnerDto;
import ru.kimvlry.kittens.service.OwnerFilter;
import ru.kimvlry.kittens.service.OwnerService;

@Tag(name = "Owners", description = "Endpoints for owner catalog search and management")
@RequiredArgsConstructor
@RestController
@RequestMapping("/owners")
public class Controller {

    private final OwnerService ownerService;

    @Operation(summary = "Get all existing owners")
    @GetMapping
    public Page<OwnerDto> getKittens(@ParameterObject Pageable pageable) {
        return ownerService.getAllOwners(pageable);
    }

    @GetMapping("/ping")
    public String hello() {
        return "Hello from owners!";
    }

    @Operation(summary = "Get owner by ID")
    @GetMapping("/{id}")
    public OwnerDto getOwnerById(@PathVariable Long id) {
        return ownerService.getOwnerById(id);
    }

    @Operation(summary = "Search owners with filter")
    @GetMapping("/search")
    public Page<OwnerDto> searchOwners(
            @ModelAttribute OwnerFilter filter,
            @ParameterObject Pageable pageable
    ) {
        return ownerService.getOwnersFiltered(filter, pageable);
    }
}