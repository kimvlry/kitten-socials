package ru.kimvlry.kittens.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.kimvlry.kittens.web.dto.OwnerDto;
import ru.kimvlry.kittens.web.service.OwnerFilter;
import ru.kimvlry.kittens.web.service.OwnerService;

@Tag(name = "Owners", description = "Endpoints for owner catalog search and management")
@RestController
@RequestMapping("/api/owners")
public class OwnerController {

    private final OwnerService ownerService;

    public OwnerController(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @GetMapping()
    public String hello() {
        return "Hello from owners!";
    }

    @Operation(summary = "Get owner by ID")
    @GetMapping("/{id}")
    public OwnerDto getOwnerById(@PathVariable Long id) {
        return ownerService.getOwnerById(id);
    }

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

    @Operation(summary = "Update an existing owner")
    @PutMapping("/{id}")
    public OwnerDto updateOwner(@PathVariable Long id, @RequestBody OwnerDto dto) {
        return ownerService.updateOwner(id, dto);
    }

    @Operation(summary = "Create a new owner")
    @PostMapping
    public OwnerDto createOwner(@RequestBody OwnerDto ownerDto) {
        return ownerService.createOwner(ownerDto);
    }

    @Operation(summary = "Delete an owner by ID")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOwner(@PathVariable Long id) {
        ownerService.deleteOwner(id);
    }
}