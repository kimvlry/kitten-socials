package ru.kimvlry.kittens.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.kimvlry.kittens.common.KittenDto;
import ru.kimvlry.kittens.common.KittenFilter;
import ru.kimvlry.kittens.service.publisher.MessageClient;
import ru.kimvlry.kittens.security.utils.annotation.ValidationUtils;

import java.util.Map;

@Slf4j
@Tag(name = "Kittens", description = "Endpoints for kitten catalog search and management")
@RestController
@RequestMapping("/kittens")
public class KittenController {
    private final String messageBrokerQueueName = "kittens.request";

    private final MessageClient messageClient;
    private final ValidationUtils validationUtils;

    public KittenController(MessageClient messageClient, ValidationUtils validationUtils) {
        this.messageClient = messageClient;
        this.validationUtils = validationUtils;
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all existing kittens")
    @GetMapping
    public Page<KittenDto> getKittens(@ParameterObject Pageable pageable) {
        return messageClient.sendPageRequest(messageBrokerQueueName, "GET_ALL_KITTENS", pageable, KittenDto.class);
    }

    @PermitAll
    @GetMapping("/ping")
    public String ping() {
        return "Hello from kittens!";
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get kitten by ID")
    @GetMapping("/{id}")
    public KittenDto getKittenById(@PathVariable Long id) {
        return messageClient.sendRequest(messageBrokerQueueName, "GET_KITTEN", id, KittenDto.class);
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Search kittens with filter")
    @GetMapping("/search")
    public Page<KittenDto> searchKittens(
            @ModelAttribute KittenFilter filter,
            @ParameterObject Pageable pageable
    ) {
        return messageClient.sendPageRequest(
                messageBrokerQueueName, "SEARCH_KITTENS",
                Map.of("filter", filter, "pageable", pageable),
                KittenDto.class);
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Create a new kitten")
    @PostMapping
    public KittenDto createKitten(@Valid @RequestBody KittenDto dto) {
        validationUtils.validateOwnerAssignment(dto);
        return messageClient.sendRequest(messageBrokerQueueName, "CREATE_KITTEN", dto, KittenDto.class);
    }

    @PreAuthorize("@validationUtils.isKittenOwner(authentication.name, #id)")
    @Operation(summary = "Update an existing kitten")
    @PutMapping("/{id}")
    public KittenDto updateKitten(@PathVariable Long id, @Valid @RequestBody KittenDto dto) {
        validationUtils.validateOwnerAssignment(dto);
        return messageClient.sendRequest(messageBrokerQueueName, "UPDATE_KITTEN", dto, KittenDto.class);
    }

    @PreAuthorize("@validationUtils.isKittenOwner(authentication.name, #id)")
    @Operation(summary = "Delete a kitten by ID")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteKitten(@PathVariable Long id) {
        messageClient.sendRequest(messageBrokerQueueName, "DELETE_KITTEN", id, KittenDto.class);
    }
}
