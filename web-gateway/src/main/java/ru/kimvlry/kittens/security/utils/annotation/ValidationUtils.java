package ru.kimvlry.kittens.security.utils.annotation;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.kimvlry.kittens.common.KittenDto;
import ru.kimvlry.kittens.repository.UserRepository;
import ru.kimvlry.kittens.entities.User;
import org.springframework.web.client.RestTemplate;

@Service
public class ValidationUtils {
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private static final String KITTEN_SERVICE_URL = "http://kitten-service:8080/kittens";

    public ValidationUtils(UserRepository userRepository,
                           RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
    }

    public boolean isKittenOwner(String ownerUsername, Long kittenId) {
        User user = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + ownerUsername));

        KittenDto kitten = restTemplate.getForObject(
                KITTEN_SERVICE_URL + "/" + kittenId,
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
        }

        if (!currentOwnerId.equals(dto.ownerId())) {
            throw new AccessDeniedException("You can't assign kitten to another owner.");
        }
    }
}