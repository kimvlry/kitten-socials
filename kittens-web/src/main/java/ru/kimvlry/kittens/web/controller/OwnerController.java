package ru.kimvlry.kittens.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.kimvlry.kittens.web.dto.OwnerDto;
import ru.kimvlry.kittens.web.security.utils.annotation.IsOwnerOrAdmin;
import ru.kimvlry.kittens.web.service.filters.OwnerFilter;
import ru.kimvlry.kittens.web.service.OwnerService;

@Tag(name = "Owners", description = "Endpoints for owner catalog search and management")
@RestController
@RequestMapping("/owners")
public class OwnerController {

    private final OwnerService ownerService;

    public OwnerController(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @PermitAll
    @GetMapping()
    public String hello() {
        return "Hello from owners!";
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get owner by ID")
    @GetMapping("/{id}")
    public OwnerDto getOwnerById(@PathVariable Long id) {
        return ownerService.getOwnerById(id);
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Search owners with filter",
            description = "Search owners using flexible filters like name, birth date, and owned kittens."
    )
    @GetMapping("/search")
    public Page<OwnerDto> searchOwners(
            @RequestBody OwnerFilter filter,
            @ParameterObject Pageable pageable
    ) {
        return ownerService.getOwnersFiltered(filter, pageable);
    }

    @IsOwnerOrAdmin
    @Operation(summary = "Update an existing owner")
    @PutMapping("/{id}")
    public OwnerDto updateOwner(@PathVariable Long id, @Valid @RequestBody OwnerDto dto) {
        return ownerService.updateOwner(id, dto);
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create a new owner")
    @PostMapping
    public OwnerDto createOwner(@Valid @RequestBody OwnerDto ownerDto) {
        return ownerService.createOwner(ownerDto);
    }

    @IsOwnerOrAdmin
    @Operation(summary = "Delete an owner by ID")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOwner(@PathVariable Long id) {
        ownerService.deleteOwner(id);
    }
}