package ru.kimvlry.kittens.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.kimvlry.kittens.web.dto.KittenDto;
import ru.kimvlry.kittens.web.service.KittenFilter;
import ru.kimvlry.kittens.web.service.KittenService;

@Tag(name = "Kittens", description = "Endpoints for kitten catalog search and filter")
@RestController
@RequestMapping("/api/kittens")
public class KittenController {

    private final KittenService kittenService;

    public KittenController(KittenService kittenService) {
        this.kittenService = kittenService;
    }

    @GetMapping()
    public String hello() {
        return "Hello from kittens!";
    }

    @Operation(summary = "Get kitten by ID")
    @GetMapping("/{id}")
    public KittenDto getKittenById(@PathVariable Long id) {
        return kittenService.getKittenById(id);
    }

    @Operation(
            summary = "Search kittens with filter",
            description = "Search kittens using flexible filters like name, breed, coat color, birth date, purr loudness, owner, and friends."
    )
    @GetMapping("/search")
    public Page<KittenDto> searchKittens(
            @RequestBody KittenFilter filter,
            @ParameterObject Pageable pageable
    ) {
        return kittenService.getKittensFiltered(filter, pageable);
    }

    @Operation(summary = "Update an existing kitten")
    @PutMapping("/{id}")
    public KittenDto updateKitten(@PathVariable Long id, @RequestBody KittenDto dto) {
        return kittenService.updateKitten(id, dto);
    }

    @Operation(summary = "Create a new kitten")
    @PostMapping
    public KittenDto createKitten(@RequestBody KittenDto kittenDto) {
        return kittenService.createKitten(kittenDto);
    }

    @Operation(summary = "Delete a kitten by ID")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteKitten(@PathVariable Long id) {
        kittenService.deleteKitten(id);
    }
}
