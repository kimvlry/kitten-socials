package ru.kimvlry.kittens.socials;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.kimvlry.kittens.socials.controller.OwnerController;
import ru.kimvlry.kittens.socials.dto.OwnerDto;
import ru.kimvlry.kittens.socials.repository.KittenRepository;
import ru.kimvlry.kittens.socials.repository.OwnerRepository;
import ru.kimvlry.kittens.socials.repository.security.RefreshTokenRepository;
import ru.kimvlry.kittens.socials.repository.security.RoleRepository;
import ru.kimvlry.kittens.socials.repository.security.UserOwnerMappingRepository;
import ru.kimvlry.kittens.socials.repository.security.UserRepository;
import ru.kimvlry.kittens.socials.security.utils.annotation.AnnotationUtils;
import ru.kimvlry.kittens.socials.security.utils.config.SecurityConfig;
import ru.kimvlry.kittens.socials.security.utils.jwt.JwtAuthFilter;
import ru.kimvlry.kittens.socials.security.utils.jwt.JwtTokenProvider;
import ru.kimvlry.kittens.socials.service.OwnerService;

import java.util.Collections;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = OwnerController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
class OwnerControllerTests {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private OwnerController ownerController;

    @MockitoBean private OwnerService ownerService;
    @MockitoBean private UserRepository userRepository;
    @MockitoBean private OwnerRepository ownerRepository;
    @MockitoBean private RoleRepository roleRepository;
    @MockitoBean private UserOwnerMappingRepository userOwnerMappingRepository;
    @MockitoBean private KittenRepository kittenRepository;
    @MockitoBean private RefreshTokenRepository refreshTokenRepository;
    @MockitoBean private JwtTokenProvider jwtTokenProvider;
    @MockitoBean private JwtAuthFilter jwtAuthFilter;
    @MockitoBean private AnnotationUtils annotationUtils;

    private Faker faker;

    @BeforeEach
    void setUp() {
        faker = new Faker();
    }

    // GET

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getOwner_Success_ReturnsOwnerDto() throws Exception {
        long id = 1L;
        String name = faker.name().fullName();
        Date birthday = faker.date().birthday();
        OwnerDto response = new OwnerDto(id, name, birthday, Collections.emptySet());

        when(annotationUtils.isAdmin()).thenReturn(true);
        when(ownerService.getOwnerById(eq(id))).thenReturn(response);

        mockMvc.perform(get("/owners/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getOwner_NotFound_Returns404() throws Exception {
        long id = 1L;
        when(annotationUtils.isAdmin()).thenReturn(true);
        when(ownerService.getOwnerById(eq(id))).thenThrow(new EntityNotFoundException("Owner not found: " + id));

        mockMvc.perform(get("/owners/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void getOwner_Forbidden_Returns403() throws Exception {
        long id = 1L;

        mockMvc.perform(get("/owners/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void getOwner_Unauthenticated_Returns401() throws Exception {
        long id = 1L;
        mockMvc.perform(get("/owners/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    // POST

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void createOwner_Success_ReturnsCreatedOwnerDto() throws Exception {
        String name = faker.name().fullName();
        Date birthday = faker.date().birthday();
        OwnerDto request = new OwnerDto(null, name, birthday, Collections.emptySet());
        OwnerDto response = new OwnerDto(1L, name, birthday, Collections.emptySet());

        when(annotationUtils.isAdmin()).thenReturn(true);
        when(ownerService.createOwner(any(OwnerDto.class))).thenReturn(response);

        mockMvc.perform(post("/owners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value(name));
        verify(ownerService).createOwner(any(OwnerDto.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void createOwner_InvalidDto_Returns400() throws Exception {
        String invalidJson = "{ \"name\": null, \"birthDate\": null }";

        mockMvc.perform(post("/owners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithAnonymousUser
    void createOwner_Forbidden_Returns403() throws Exception {
        String name = faker.name().fullName();
        Date birthday = faker.date().birthday();
        OwnerDto request = new OwnerDto(null, name, birthday, Collections.emptySet());

        when(annotationUtils.isAdmin()).thenReturn(false);

        mockMvc.perform(post("/owners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    // PUT

    @Test
    @WithMockUser(username = "test", roles = {"ADMIN"})
    void updateOwner_Success_ReturnsUpdatedOwnerDto() throws Exception {
        long id = 1L;
        String name = faker.name().fullName();
        Date birthday = faker.date().birthday();
        OwnerDto request = new OwnerDto(id, name, birthday, Collections.emptySet());
        OwnerDto response = new OwnerDto(id, name, birthday, Collections.emptySet());

        when(annotationUtils.isOwner("test", id)).thenReturn(true);
        when(ownerService.updateOwner(eq(id), any(OwnerDto.class))).thenReturn(response);

        mockMvc.perform(put("/owners/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name));
        verify(ownerService).updateOwner(eq(id), any(OwnerDto.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void updateOwner_InvalidDto_Returns400() throws Exception {
        long id = 1L;
        String invalidJson = "{ \"name\": null, \"birthDate\": null }";

        mockMvc.perform(put("/owners/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test", roles = {"ADMIN"})
    void updateOwner_NotFound_Returns404() throws Exception {
        long id = 1L;
        String name = faker.name().fullName();
        Date birthday = faker.date().birthday();
        OwnerDto request = new OwnerDto(id, name, birthday, Collections.emptySet());

        when(annotationUtils.isOwner("test", id)).thenReturn(true);
        when(ownerService.updateOwner(eq(id), any(OwnerDto.class)))
                .thenThrow(new EntityNotFoundException("Owner not found: " + id));

        mockMvc.perform(put("/owners/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void updateOwner_Forbidden_Returns403() throws Exception {
        long id = 1L;
        String name = faker.name().fullName();
        Date birthday = faker.date().birthday();
        OwnerDto request = new OwnerDto(id, name, birthday, Collections.emptySet());

        when(annotationUtils.isAdmin()).thenReturn(false);

        mockMvc.perform(put("/owners/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    // DELETE

    @Test
    @WithMockUser(username = "test", roles = {"ADMIN"})
    void deleteOwner_Success_ReturnsNoContent() throws Exception {
        long id = 1L;
        when(annotationUtils.isOwner("test", id)).thenReturn(true);
        doNothing().when(ownerService).deleteOwner(eq(id));

        mockMvc.perform(delete("/owners/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        verify(ownerService).deleteOwner(eq(id));
    }

    @Test
    @WithMockUser(username = "test", roles = {"ADMIN"})
    void deleteOwner_NotFound_Returns404() throws Exception {
        long id = 1L;
        when(annotationUtils.isOwner("test", id)).thenReturn(true);
        doThrow(new EntityNotFoundException("Owner not found: " + id)).when(ownerService).deleteOwner(eq(id));

        mockMvc.perform(delete("/owners/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void deleteOwner_Forbidden_Returns403() throws Exception {
        long id = 1L;
        when(annotationUtils.isAdmin()).thenReturn(false);

        mockMvc.perform(delete("/owners/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}