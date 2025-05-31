package ru.kimvlry.kittens.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.kimvlry.kittens.common.OwnerDto;
import ru.kimvlry.kittens.common.OwnerFilter;

@Slf4j
@Tag(name = "Owners", description = "Endpoints for owner catalog search and management")
@RestController
@RequestMapping("/owners")
@RequiredArgsConstructor
public class OwnerController {
    private final RabbitTemplate rabbitTemplate;
    private final RestTemplate restTemplate;
    private final AuthHeadersBuilder authHeadersBuilder;

    private static final String OWNER_EXCHANGE = "owner.exchange";
    private static final String OWNER_CREATE_ROUTING_KEY = "owner.create";
    private static final String OWNER_UPDATE_ROUTING_KEY = "owner.update";
    private static final String OWNER_DELETE_ROUTING_KEY = "owner.delete";

    private static final String OWNER_SERVICE_URL = "http://owner-service:8080/owners";
    private static final ParameterizedTypeReference<Page<OwnerDto>> PAGE_TYPE_REF = new ParameterizedTypeReference<>() {};

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all existing owners")
    @GetMapping
    public ResponseEntity<Page<OwnerDto>> getOwners(@ParameterObject Pageable pageable) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(OWNER_SERVICE_URL)
                .queryParam("page", pageable.getPageNumber())
                .queryParam("size", pageable.getPageSize());

        if (pageable.getSort().isSorted()) {
            pageable.getSort().forEach(order ->
                    builder.queryParam("sort", order.getProperty() + "," + order.getDirection()));
        }

        return restTemplate.exchange(
                builder.build().toUri(),
                HttpMethod.GET,
                new HttpEntity<>(authHeadersBuilder.build()),
                PAGE_TYPE_REF);
    }

    @PermitAll
    @GetMapping("/ping")
    public String hello() {
        return "Hello from owners!";
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get owner by ID")
    @GetMapping("/{id}")
    public ResponseEntity<OwnerDto> getOwnerById(@PathVariable Long id) {
        return restTemplate.exchange(
                OWNER_SERVICE_URL + "/" + id,
                HttpMethod.GET,
                new HttpEntity<>(authHeadersBuilder.build()),
                OwnerDto.class);
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Search owners with filter")
    @GetMapping("/search")
    public ResponseEntity<Page<OwnerDto>> searchOwners(
            @ModelAttribute OwnerFilter filter,
            @ParameterObject Pageable pageable) {

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(OWNER_SERVICE_URL + "/search")
                .queryParam("page", pageable.getPageNumber())
                .queryParam("size", pageable.getPageSize())
                .queryParam("name", filter.getName())
                .queryParam("birthBefore", filter.getBirthBefore())
                .queryParam("birthAfter", filter.getBirthAfter());

        return restTemplate.exchange(
                builder.build().toUri(),
                HttpMethod.GET,
                new HttpEntity<>(authHeadersBuilder.build()),
                PAGE_TYPE_REF);
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create a new owner")
    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void createOwner(@Valid @RequestBody OwnerDto ownerDto) {
        rabbitTemplate.convertAndSend(OWNER_EXCHANGE, OWNER_CREATE_ROUTING_KEY, ownerDto);
    }

    @PreAuthorize("@validationUtils.isOwner(authentication.name, #id)")
    @Operation(summary = "Update an existing owner")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateOwner(@PathVariable Long id, @Valid @RequestBody OwnerDto dto) {
        OwnerDto updatedDto = new OwnerDto(id, dto.name(), dto.birthDate(), dto.ownedKittensIds());
        rabbitTemplate.convertAndSend(OWNER_EXCHANGE, OWNER_UPDATE_ROUTING_KEY, updatedDto);
    }

    @PreAuthorize("@validationUtils.isOwner(authentication.name, #id)")
    @Operation(summary = "Delete an owner by ID")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteOwner(@PathVariable Long id) {
        OwnerDto dto = new OwnerDto(id, null, null, null);
        rabbitTemplate.convertAndSend(OWNER_EXCHANGE, OWNER_DELETE_ROUTING_KEY, dto);
    }
}
