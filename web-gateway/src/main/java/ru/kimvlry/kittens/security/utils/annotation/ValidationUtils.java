package ru.kimvlry.kittens.security.utils.annotation;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.kimvlry.kittens.common.KittenDto;
import ru.kimvlry.kittens.service.publisher.MessageClient;
import ru.kimvlry.kittens.repository.UserRepository;
import ru.kimvlry.kittens.entities.User;

@Service
public class ValidationUtils {
    private final String queue = "kittens.request";
    private final UserRepository userRepository;
    private final MessageClient messageClient;

    public ValidationUtils(UserRepository userRepository,
                           MessageClient messageClient
    ) {
        this.userRepository = userRepository;
        this.messageClient = messageClient;
    }

    public boolean isKittenOwner(String ownerUsername, Long id) {
        User user = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + ownerUsername));
        KittenDto kitten = messageClient.sendRequest(
                queue,
                "GET_KITTEN",
                id,
                KittenDto.class
        );
        return kitten != null && user.getOwnerId().equals(kitten.ownerId());
    }

    public boolean isOwner(String username, Long ownerId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        return user.getOwnerId().equals(ownerId);
    }

    public void validateOwnerAssignment(KittenDto dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new InsufficientAuthenticationException("No authenticated user found");
        }

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        Long currentOwnerId = user.getOwnerId();
        if (currentOwnerId == null) {
            throw new IllegalArgumentException("No Owner associated with user: " + username);
        };

        if (!currentOwnerId.equals(dto.ownerId())) {
            throw new AccessDeniedException("You can't assign kitten to another owner.");
        }
    }
}