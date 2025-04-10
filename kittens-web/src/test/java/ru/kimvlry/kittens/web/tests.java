package ru.kimvlry.kittens.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.kimvlry.kittens.entities.KittenBreed;
import ru.kimvlry.kittens.entities.KittenCoatColor;
import ru.kimvlry.kittens.web.controller.KittenController;
import ru.kimvlry.kittens.web.dto.KittenDto;
import ru.kimvlry.kittens.web.service.KittenFilter;
import ru.kimvlry.kittens.web.service.KittenService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = KittenController.class)
class tests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KittenService kittenService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void searchKittens_returnsKittensPage() throws Exception {
        String name = "Kitkat";
        KittenCoatColor color = KittenCoatColor.ESPRESSO;
        KittenBreed breed = KittenBreed.BRITISH_SHORTHAIR;

        KittenDto kitten = new KittenDto(
                1L, name, LocalDateTime.now(),
                breed, color,
                9, 100L, Set.of(2L, 3L)
        );

        Page<KittenDto> page = new PageImpl<>(List.of(kitten), PageRequest.of(0, 10), 1);

        when(kittenService.getKittensFiltered(any(KittenFilter.class), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(post("/api/kittens/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new KittenFilter())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value(name))
                .andExpect(jsonPath("$.content[0].breed").value(breed.toString()))
                .andExpect(jsonPath("$.content[0].coatColor").value(color.toString()));
    }

    @Test
    void searchKittens_returnsEmptyPage() throws Exception {
        Page<KittenDto> emptyPage = Page.empty();

        when(kittenService.getKittensFiltered(any(KittenFilter.class), any(Pageable.class)))
                .thenReturn(emptyPage);

        mockMvc.perform(post("/api/kittens/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new KittenFilter())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void searchKittens_filtersByBreed() throws Exception {
        String name = "Kitkat";
        KittenCoatColor color = KittenCoatColor.ESPRESSO;
        KittenBreed breed = KittenBreed.BRITISH_SHORTHAIR;

        KittenDto britishKitten = new KittenDto(
                10L, name, LocalDateTime.now().minusYears(1),
                breed, color,
                5, 200L, Set.of()
        );

        Page<KittenDto> page = new PageImpl<>(List.of(britishKitten));

        when(kittenService.getKittensFiltered(any(KittenFilter.class), any(Pageable.class)))
                .thenReturn(page);

        KittenFilter filter = new KittenFilter();
        filter.setBreeds(Set.of(KittenBreed.BRITISH_SHORTHAIR));

        mockMvc.perform(post("/api/kittens/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].breed").value(breed.toString()));
    }

    @Test
    void searchKittens_handlesPagination() throws Exception {
        String name = "Kitkat";
        KittenCoatColor color = KittenCoatColor.ESPRESSO;
        KittenBreed breed = KittenBreed.BRITISH_SHORTHAIR;

        KittenDto kitty = new KittenDto(
                99L, name, LocalDateTime.now().minusMonths(6),
                breed, color,
                6, 300L, Set.of()
        );

        Pageable pageable = PageRequest.of(1, 1); // page 1, size 1 (second page)
        Page<KittenDto> page = new PageImpl<>(List.of(kitty), pageable, 2);

        when(kittenService.getKittensFiltered(any(KittenFilter.class), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(post("/api/kittens/search?page=1&size=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new KittenFilter())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].name").value(name));
    }

    @Test
    void searchKittens_withInvalidJson_returnsBadRequest() throws Exception {
        String invalidJson = "{ \"breeds\": [\"not a breed\"] }";

        mockMvc.perform(post("/api/kittens/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
}
