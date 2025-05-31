package ru.kimvlry.kittens.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.kimvlry.kittens.dto.KittenDto;
import ru.kimvlry.kittens.service.KittenFilter;
import ru.kimvlry.kittens.service.KittenService;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "Kittens", description = "Endpoints for kitten catalog search and management")
@RestController
@RequestMapping("/kittens")
public class Controller {
    private final KittenService kittenService;

    @Operation(summary = "Get all existing kittens")
    @GetMapping
    public Page<KittenDto> getKittens(@ParameterObject Pageable pageable) {
        return kittenService.getAllKittens(pageable);
    }

    @GetMapping("/ping")
    public String ping() {
        return "Hello from kittens!";
    }

    @Operation(summary = "Get kitten by ID")
    @GetMapping("/{id}")
    public KittenDto getKittenById(@PathVariable Long id) {
        return kittenService.getKittenById(id);
    }

    @Operation(summary = "Search kittens with filter")
    @GetMapping("/search")
    public Page<KittenDto> searchKittens(
            @ModelAttribute KittenFilter filter,
            @ParameterObject Pageable pageable
    ) {
        return kittenService.getKittensFiltered(filter, pageable);
    }
}
