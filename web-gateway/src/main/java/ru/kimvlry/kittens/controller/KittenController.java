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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.kimvlry.kittens.common.KittenDto;
import ru.kimvlry.kittens.common.KittenFilter;
import ru.kimvlry.kittens.security.utils.annotation.ValidationUtils;

import java.util.Map;

@Slf4j
@Tag(name = "Kittens", description = "Endpoints for kitten catalog search and management")
@RestController
@RequestMapping("/kittens")
@RequiredArgsConstructor
public class KittenController {
    private final RabbitTemplate rabbitTemplate;
    private final ValidationUtils validationUtils;
    private final RestTemplate restTemplate;

    private static final String KITTEN_QUEUE = "kitten.queue";
    private static final String KITTEN_SERVICE_URL = "http://kitten-service:8080/kittens";
    private final AuthHeadersBuilder authHeadersBuilder;

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all existing kittens")
    @GetMapping
    public Page<KittenDto> getKittens(@ParameterObject Pageable pageable) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(KITTEN_SERVICE_URL)
                .queryParam("page", pageable.getPageNumber())
                .queryParam("size", pageable.getPageSize());

        HttpEntity<?> entity = new HttpEntity<>(authHeadersBuilder.build());

        return restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<Page<KittenDto>>() {}
        ).getBody();
    }

    @PermitAll
    @GetMapping("/ping")
    public String ping() {
        return "Hello from kittens!";
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get kitten by ID")
    @GetMapping("/{id}")
    public ResponseEntity<KittenDto> getKittenById(@PathVariable Long id) {
        return restTemplate.exchange(
                KITTEN_SERVICE_URL + "/" + id,
                HttpMethod.GET,
                new HttpEntity<>(authHeadersBuilder.build()),
                KittenDto.class
        );
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Search kittens with filter")
    @GetMapping("/search")
    public Page<KittenDto> searchKittens(
            @ModelAttribute KittenFilter filter,
            @ParameterObject Pageable pageable) {

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(KITTEN_SERVICE_URL + "/search")
                .queryParam("page", pageable.getPageNumber())
                .queryParam("size", pageable.getPageSize());

        return restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                new HttpEntity<>(authHeadersBuilder.build()),
                new ParameterizedTypeReference<Page<KittenDto>>() {}
        ).getBody();

    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<?> createKitten(@Valid @RequestBody KittenDto dto) {
        validationUtils.validateOwnerAssignment(dto);

        String requester = SecurityContextHolder.getContext().getAuthentication().getName();

        rabbitTemplate.convertAndSend(KITTEN_QUEUE, dto, message -> {
            message.getMessageProperties().setHeader("action", "CREATE");
            message.getMessageProperties().setHeader("requester", requester);
            return message;
        });

        return ResponseEntity.accepted().build();
    }

    @PreAuthorize("@validationUtils.isKittenOwner(authentication.name, #id)")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateKitten(@PathVariable Long id, @Valid @RequestBody KittenDto dto) {
        validationUtils.validateOwnerAssignment(dto);

        String requester = SecurityContextHolder.getContext().getAuthentication().getName();

        rabbitTemplate.convertAndSend(KITTEN_QUEUE,
                Map.of("id", id, "dto", dto, "requester", requester),
                message -> {
                    message.getMessageProperties().setHeader("action", "UPDATE");
                    return message;
                });

        return ResponseEntity.accepted().build();
    }

    @PreAuthorize("@validationUtils.isKittenOwner(authentication.name, #id)")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteKitten(@PathVariable Long id) {
        String requester = SecurityContextHolder.getContext().getAuthentication().getName();

        rabbitTemplate.convertAndSend(KITTEN_QUEUE,
                Map.of("id", id, "requester", requester),
                message -> {
                    message.getMessageProperties().setHeader("action", "DELETE");
                    return message;
                });
    }

}