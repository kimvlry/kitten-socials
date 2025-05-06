package ru.kimvlry.kittens.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.kimvlry.kittens.web.controller.KittenController;
import ru.kimvlry.kittens.web.dto.KittenDto;
import ru.kimvlry.kittens.web.entities.KittenBreed;
import ru.kimvlry.kittens.web.entities.KittenCoatColor;
import ru.kimvlry.kittens.web.repository.KittenRepository;
import ru.kimvlry.kittens.web.repository.OwnerRepository;
import ru.kimvlry.kittens.web.repository.security.RefreshTokenRepository;
import ru.kimvlry.kittens.web.repository.security.RoleRepository;
import ru.kimvlry.kittens.web.repository.security.UserOwnerMappingRepository;
import ru.kimvlry.kittens.web.repository.security.UserRepository;
import ru.kimvlry.kittens.web.security.utils.annotation.AnnotationUtils;
import ru.kimvlry.kittens.web.security.utils.config.SecurityConfig;
import ru.kimvlry.kittens.web.security.utils.jwt.JwtAuthFilter;
import ru.kimvlry.kittens.web.security.utils.jwt.JwtTokenProvider;
import ru.kimvlry.kittens.web.service.KittenService;
import ru.kimvlry.kittens.web.service.filters.KittenFilter;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = KittenController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
class KittenControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private KittenController kittenController;

    @MockitoBean
    private KittenService kittenService;
    @MockitoBean
    private UserRepository userRepository;
    @MockitoBean
    private OwnerRepository ownerRepository;
    @MockitoBean
    private RoleRepository roleRepository;
    @MockitoBean
    private UserOwnerMappingRepository userOwnerMappingRepository;
    @MockitoBean
    private KittenRepository kittenRepository;
    @MockitoBean
    private RefreshTokenRepository refreshTokenRepository;
    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;
    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;
    @MockitoBean
    private AnnotationUtils annotationUtils;


    //  GET

    @Test
    @WithMockUser
    void searchKittens_returnsKittensPage() throws Exception {
        String name = "Kitkat";
        KittenCoatColor color = KittenCoatColor.ESPRESSO;
        KittenBreed breed = KittenBreed.BRITISH_SHORTHAIR;

        KittenDto kitten = new KittenDto(
                1L, name, Instant.now(),
                breed, color,
                9, 100L, Set.of(2L, 3L)
        );

        Page<KittenDto> page = new PageImpl<>(List.of(kitten), PageRequest.of(0, 10), 1);

        when(kittenService.getKittensFiltered(any(KittenFilter.class), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/kittens/search")
                        .param("name", "Kitkat")
                        .param("breeds", "BRITISH_SHORTHAIR")
                        .param("coat", "ESPRESSO")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value(name))
                .andExpect(jsonPath("$.content[0].breed").value(breed.toString()))
                .andExpect(jsonPath("$.content[0].coatColor").value(color.toString()));
    }

    @Test
    @WithMockUser
    void searchKittens_returnsEmptyPage() throws Exception {
        Page<KittenDto> emptyPage = Page.empty();

        when(kittenService.getKittensFiltered(any(KittenFilter.class), any(Pageable.class)))
                .thenReturn(emptyPage);

        mockMvc.perform(get("/kittens/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new KittenFilter())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    @WithMockUser
    void searchKittens_filtersByBreed() throws Exception {
        String name = "Kitkat";
        KittenBreed breed = KittenBreed.BRITISH_SHORTHAIR;
        KittenCoatColor color = KittenCoatColor.ESPRESSO;

        KittenDto britishKitten = new KittenDto(
                1L, name, Instant.now(),
                breed, color,
                3, 6L, Set.of()
        );

        Page<KittenDto> page = new PageImpl<>(List.of(britishKitten));

        when(kittenService.getKittensFiltered(any(), any())).thenReturn(page);

        mockMvc.perform(get("/kittens/search")
                        .param("breeds", breed.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].breed").value(breed.toString()));
    }

    @Test
    @WithMockUser
    void searchKittens_handlesPagination() throws Exception {
        String name = "Kitkat";
        KittenCoatColor color = KittenCoatColor.ESPRESSO;
        KittenBreed breed = KittenBreed.BRITISH_SHORTHAIR;

        KittenDto kitty = new KittenDto(
                99L, name, Instant.now(),
                breed, color,
                6, 300L, Set.of()
        );

        Pageable pageable = PageRequest.of(1, 1);
        Page<KittenDto> page = new PageImpl<>(List.of(kitty), pageable, 2);

        when(kittenService.getKittensFiltered(any(KittenFilter.class), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/kittens/search?page=1&size=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new KittenFilter())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].name").value(name));
    }

    @Test
    @WithMockUser
    void searchKittens_withInvalidJson_returnsBadRequest() throws Exception {

        mockMvc.perform(get("/kittens/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("breeds", "not a breed"))
                .andExpect(status().isBadRequest());
    }


    //  POST

    @Test
    @WithMockUser
    void createKitten_returnsCreatedKitten() throws Exception {
        KittenDto requestDto = new KittenDto(
                null, "Kitkat", Instant.now(),
                KittenBreed.SIBERIAN, KittenCoatColor.CARAMEL,
                7, 1L, Set.of()
        );

        KittenDto responseDto = new KittenDto(
                42L, requestDto.name(), requestDto.birthDate(),
                requestDto.breed(), requestDto.coatColor(),
                requestDto.purrLoudnessRate(), requestDto.ownerId(), requestDto.friendIds()
        );

        when(kittenService.createKitten(any(KittenDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/kittens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.name").value("Kitkat"))
                .andExpect(jsonPath("$.breed").value("SIBERIAN"))
                .andExpect(jsonPath("$.coatColor").value("CARAMEL"));
    }

    @Test
    void createKitten_Unauthenticated_returns401() throws Exception {
        KittenDto requestDto = new KittenDto(
                null, "Kitkat", Instant.now(),
                KittenBreed.SIBERIAN, KittenCoatColor.CARAMEL,
                7, 1L, Set.of()
        );

        mockMvc.perform(post("/kittens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    void createKitten_noRole_returns403() throws Exception {
        KittenDto requestDto = new KittenDto(
                null, "Kitkat", Instant.now(),
                KittenBreed.SIBERIAN, KittenCoatColor.CARAMEL,
                7, 1L, Set.of()
        );

        mockMvc.perform(post("/kittens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void createKitten_withInvalidData_returnsBadRequest() throws Exception {
        String badJson = "{ \"name\": \"Whiskers\", \"purrLoudnessRate\": \"invalid rate\" }";

        mockMvc.perform(post("/kittens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badJson))
                .andExpect(status().isBadRequest());
    }


    // DELETE

    @Test
    @WithMockUser(username = "user")
    void deleteKitten_returnsNoContent() throws Exception {
        when(annotationUtils.isKittenOwner(eq("user"), eq(99L))).thenReturn(true);

        mockMvc.perform(delete("/kittens/{id}", 99L))
                .andExpect(status().isNoContent());
        verify(kittenService).deleteKitten(eq(99L));
    }
}