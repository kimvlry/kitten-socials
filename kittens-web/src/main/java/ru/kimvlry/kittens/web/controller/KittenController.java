package ru.kimvlry.kittens.web.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.kimvlry.kittens.web.dto.KittenDto;
import ru.kimvlry.kittens.web.service.KittenFilter;
import ru.kimvlry.kittens.web.service.KittenService;

@RestController
@RequestMapping("/api/kittens")
public class KittenController {

    private final KittenService kittenService;

    public KittenController(KittenService kittenService) {
        this.kittenService = kittenService;
    }

    @PostMapping("/search")
    public Page<KittenDto> searchKittens(@RequestBody KittenFilter filter, Pageable pageable) {
        return kittenService.getKittensFiltered(filter, pageable);
    }
}
