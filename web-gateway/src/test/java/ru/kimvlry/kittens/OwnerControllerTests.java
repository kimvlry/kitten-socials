package ru.kimvlry.kittens;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.kimvlry.kittens.controller.OwnerController;
import ru.kimvlry.kittens.common.OwnerDto;
import ru.kimvlry.kittens.service.publisher.MessageClient;
import ru.kimvlry.kittens.repository.RefreshTokenRepository;
import ru.kimvlry.kittens.repository.RoleRepository;
import ru.kimvlry.kittens.repository.UserRepository;
import ru.kimvlry.kittens.security.utils.annotation.ValidationUtils;
import ru.kimvlry.kittens.security.utils.config.SecurityConfig;
import ru.kimvlry.kittens.security.utils.jwt.JwtAuthFilter;
import ru.kimvlry.kittens.security.utils.jwt.JwtTokenProvider;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = OwnerController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
class OwnerControllerTests {
    private String messageBrokerQueueName = "owners.request";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private OwnerController ownerController;

    @MockitoBean
    private MessageClient messageClient;
    @MockitoBean
    private UserRepository userRepository;
    @MockitoBean
    private RoleRepository roleRepository;
    @MockitoBean
    private RefreshTokenRepository refreshTokenRepository;
    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;
    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;
    @MockitoBean
    private ValidationUtils validationUtils;

    private Faker faker;

    @BeforeEach
    void setUp() {
        faker = new Faker();
    }

    // GET

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getOwner_Success_ReturnsOwnerDto() throws Exception {
        long id = 1L;
        String name = faker.name().fullName();
        Date birthday = faker.date().birthday();
        OwnerDto response = new OwnerDto(id, name, birthday, Collections.emptySet());

        when(validationUtils.isOwner("admin", id)).thenReturn(true);
        when(messageClient.sendRequest(eq(messageBrokerQueueName), eq("GET_OWNER"), any(), eq(OwnerDto.class))).thenReturn(response);

        mockMvc.perform(get("/owners/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getOwner_NotFound_Returns404() throws Exception {
        long id = 1L;
        when(validationUtils.isOwner("admin", id)).thenReturn(true);
        when(messageClient.sendRequest(eq(messageBrokerQueueName), eq("GET_OWNER"), any(), eq(OwnerDto.class)))
                .thenThrow(new EntityNotFoundException("Owner not found: " + id));

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

    @Test
    void getOwners_unauthorized_returnsUnauthorized401() throws Exception {
        mockMvc.perform(get("/owners")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    void getOwners_anon_returnsForbidden403() throws Exception {
        mockMvc.perform(get("/owners")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void searchOwners_returnsOwnersPage() throws Exception {
        String name = "Owner";
        Date bday = faker.date().birthday();

        OwnerDto ownerDto = new OwnerDto(1L, name, bday, Collections.emptySet());
        Page<OwnerDto> page = new PageImpl<>(List.of(ownerDto), PageRequest.of(0, 10), 1);

        when(messageClient.sendPageRequest(eq(messageBrokerQueueName), eq("SEARCH_OWNERS"), any(), eq(OwnerDto.class))).thenReturn(page);

        mockMvc.perform(get("/owners/search")
                        .param("name", name)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value(name));
    }

    // POST

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createOwner_Success_ReturnsCreatedOwnerDto() throws Exception {
        Long id = 1L;
        String name = faker.name().fullName();
        Date birthday = faker.date().birthday();
        OwnerDto request = new OwnerDto(id, name, birthday, Collections.emptySet());
        OwnerDto response = new OwnerDto(id, name, birthday, Collections.emptySet());

        when(validationUtils.isOwner("admin", id)).thenReturn(true);
        when(messageClient.sendRequest(eq(messageBrokerQueueName), eq("CREATE_OWNER"), any(), eq(OwnerDto.class))).thenReturn(response);

        mockMvc.perform(post("/owners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value(name));
        verify(messageClient).sendRequest(eq(messageBrokerQueueName), eq("CREATE_OWNER"), any(), eq(OwnerDto.class));
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
        Long id = 1L;
        String name = faker.name().fullName();
        Date birthday = faker.date().birthday();
        OwnerDto request = new OwnerDto(id, name, birthday, Collections.emptySet());

        when(validationUtils.isOwner(anyString(), eq(id))).thenReturn(false);

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

        when(validationUtils.isOwner("test", id)).thenReturn(true);
        when(messageClient.sendRequest(eq(messageBrokerQueueName), eq("UPDATE_OWNER"), any(), eq(OwnerDto.class))).thenReturn(response);

        mockMvc.perform(put("/owners/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name));
        verify(messageClient).sendRequest(eq(messageBrokerQueueName), eq("UPDATE_OWNER"), any(), eq(OwnerDto.class));
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

        when(validationUtils.isOwner("test", id)).thenReturn(true);
        when(messageClient.sendRequest(eq(messageBrokerQueueName), eq("UPDATE_OWNER"), any(), eq(OwnerDto.class)))
                .thenThrow(new EntityNotFoundException("Owner not found: " + id));

        mockMvc.perform(put("/owners/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "wrongUser")
    void updateOwner_Forbidden_Returns403() throws Exception {
        long id = 1L;
        String name = faker.name().fullName();
        Date birthday = faker.date().birthday();
        OwnerDto request = new OwnerDto(id, name, birthday, Collections.emptySet());

        when(validationUtils.isOwner("wrongUser", id)).thenReturn(false);

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
        when(validationUtils.isOwner("test", id)).thenReturn(true);
        when(messageClient.sendRequest(eq(messageBrokerQueueName), eq("DELETE_OWNER"), eq(id), eq(OwnerDto.class))).thenReturn(null);

        mockMvc.perform(delete("/owners/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        verify(messageClient).sendRequest(eq(messageBrokerQueueName), eq("DELETE_OWNER"), eq(id), eq(OwnerDto.class));
    }

    @Test
    @WithMockUser(username = "test", roles = {"ADMIN"})
    void deleteOwner_NotFound_Returns404() throws Exception {
        long id = 1L;
        when(validationUtils.isOwner("test", id)).thenReturn(true);
        doThrow(new EntityNotFoundException("Owner not found: " + id)).when(messageClient)
                .sendRequest(eq(messageBrokerQueueName), eq("DELETE_OWNER"), eq(id), eq(OwnerDto.class));

        mockMvc.perform(delete("/owners/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "wrongUser")
    void deleteOwner_Forbidden_Returns403() throws Exception {
        long id = 1L;
        when(validationUtils.isOwner("wrongUser", id)).thenReturn(false);

        mockMvc.perform(delete("/owners/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}