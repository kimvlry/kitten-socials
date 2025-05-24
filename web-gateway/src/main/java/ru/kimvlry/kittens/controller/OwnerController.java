package ru.kimvlry.kittens.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.kimvlry.kittens.common.OwnerDto;
import ru.kimvlry.kittens.common.OwnerFilter;
import ru.kimvlry.kittens.service.publisher.MessageClient;

import java.util.Map;

@Tag(name = "Owners", description = "Endpoints for owner catalog search and management")
@RestController
@RequestMapping("/owners")
public class OwnerController {
    private final String messageBrokerQueueName = "owners.request";

    private final MessageClient messageClient;

    public OwnerController(MessageClient messageClient) {
        this.messageClient = messageClient;
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all existing owners")
    @GetMapping
    public Page<OwnerDto> getKittens(@ParameterObject Pageable pageable) {
        return messageClient.sendPageRequest(messageBrokerQueueName, "GET_KITTENS", pageable, OwnerDto.class);
    }

    @PermitAll
    @GetMapping("/ping")
    public String hello() {
        return "Hello from owners!";
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get owner by ID")
    @GetMapping("/{id}")
    public OwnerDto getOwnerById(@PathVariable Long id) {
        return messageClient.sendRequest(messageBrokerQueueName, "GET_OWNER", id, OwnerDto.class);
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Search owners with filter")
    @GetMapping("/search")
    public Page<OwnerDto> searchOwners(
            @ModelAttribute OwnerFilter filter,
            @ParameterObject Pageable pageable
    ) {
        return messageClient.sendPageRequest(
                messageBrokerQueueName,
                "SEARCH_OWNERS",
                Map.of("filter", filter, "pageable", pageable),
                OwnerDto.class);
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create a new owner")
    @PostMapping
    public OwnerDto createOwner(@Valid @RequestBody OwnerDto ownerDto) {
        return messageClient.sendRequest(messageBrokerQueueName, "CREATE_OWNER", ownerDto, OwnerDto.class);
    }

    @PreAuthorize("@validationUtils.isOwner(authentication.name, #id)")
    @Operation(summary = "Update an existing owner")
    @PutMapping("/{id}")
    public OwnerDto updateOwner(@PathVariable Long id, @Valid @RequestBody OwnerDto dto) {
        return messageClient.sendRequest(
                messageBrokerQueueName,
                "UPDATE_OWNER",
                Map.of("id", id, "dto", dto),
                OwnerDto.class);
    }

    @PreAuthorize("@validationUtils.isOwner(authentication.name, #id)")
    @Operation(summary = "Delete an owner by ID")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOwner(@PathVariable Long id) {
        messageClient.sendRequest(messageBrokerQueueName, "DELETE_OWNER", id, OwnerDto.class);
    }
}